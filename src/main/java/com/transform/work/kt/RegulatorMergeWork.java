package com.transform.work.kt;

import com.transform.jdbc.SQL;
import com.transform.util.CalculateUtils;
import com.transform.util.ServiceCodeGenerator;
import com.transform.util.StrUtils;
import com.transform.util.ValChangeUtils;
import com.transform.work.AbstractWorker;
import com.transform.work.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理单位表迁移合并
 * kt/MCS_REGULATOR_INFO -> hx/uas_org_info
 * <p>
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Service
public class RegulatorMergeWork extends AbstractWorker implements Converter {

    @Override
    public boolean convert() {
        Object obj = tt.queryFirst(SQL.select("count(1)").from(MCS_REGULATOR_INFO).where("ISDEL = '0'").build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, null);
        int offset = 0;
        int limit = LIMIT;
        log.info("RegulatorMergeWork 任务开始 ======= total: {}", total);
        long dealTotal = 0;
        // 管理单位表
        while (true) {
            int jobNum = regulatorInfo(offset, limit);
            dealTotal += jobNum;
            log.info("RegulatorMergeWork 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            offset += limit;
            if (offset >= total) {
                break;
            }
        }
        log.info("RegulatorMergeWork 任务结束 =======");
        return true;
    }

    private int regulatorInfo(int offset, int limit) {
        String sql = SQL.select("*").from(MCS_REGULATOR_INFO).where("ISDEL = '0'").limit(limit).offset(offset).build();
        List<Map<String, Object>> ret = tt.queryForMapList(sql);
        List<Map<String, Object>> datas = new ArrayList<>();
        // 记录迁移过程的数据错误
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : ret) {
            // 清空内容，复用
            sb.delete(0, sb.length());
            Map<String, Object> volVal = new HashMap<>();
            volVal.put("type", 3);
            // kt: 1省级监管,2地市监管,3区县监管
            // hx: 1:省级监管,2:省属监管,3:地市监管,4:区县监管
            int[] supLevel = {-1,1,3,4};
            volVal.put("kt_sup_level", ValChangeUtils.toIntegerIfNull(map.get("REGTYPE"), 0));
            volVal.put("kt_org_id", map.get("REGID"));
            volVal.put("name", map.get("REGULATOR"));
            volVal.put("contact_address", map.get("ADDRS"));
            volVal.put("legal_person", map.get("LEREP"));
            volVal.put("link_person", map.get("LINKMAN"));
            volVal.put("fixed_telephone", map.get("TEL"));
            volVal.put("link_person_mobile", map.get("MOBILE"));
            volVal.put("email", map.get("EMAIL"));
            // 监管片区
            Object supArea = map.get("AREA");
            if (!StrUtils.isBlankOrNullVal(supArea)) {
                Map<String, Object> cno = tt.queryFirst(SQL.select("CVALUE").from(D_CODE).where("CNO = ?").build(), supArea + "00");
                String cvalue = cno.get("CVALUE") + "";
                volVal.put("supervise_area", cvalue);
                //片区id
                Map<String, Object> supAreaId = tt.queryFirst(SQL.select("id").from(UAS_SUPERVISE_AREA).where("name like ?").build(), cvalue + "%");
                if (supAreaId != null) {
                    volVal.put("supervise_area_id", supAreaId.get("id"));
                } else {
                    sb.append("在'uas_supervise_area'中找不到片区：").append(cvalue.substring(0, cvalue.length() - 1)).append(";");
                }
            }
            volVal.put("supervise_med_org", map.get("HOSPTYPE"));
            volVal.put("fax", map.get("FOX"));
            volVal.put("short_pinyin", map.get("REG_PY"));
            volVal.put("notes", map.get("REMARK"));
            volVal.put("create_time", map.get("CREATEDATE"));
            volVal.put("modify_time", map.get("UPDATETIME"));
            Object regCode = map.get("REGCODE");
            volVal.put("kt_region_code", regCode);

            // 地区字段转成 code（如：330000,330100,330103）
            String locateAreaCode = ServiceCodeGenerator.generateLocateAreaCode(regCode + "", tt);
            if (locateAreaCode == null) {
                sb.append("地区找不到;");
                //sb.append("'D_CODE'(CNO:").append(regCode).append(",CVALUE:").append(dcode.get("CVALUE")).append(")不在'uas_base_area'中;");
            } else {
                volVal.put("locate_area", locateAreaCode);
            }

            volVal.put("audit_status", 3);
            // 错误记录
            volVal.put("ts_notes", sb.toString());
            volVal.put("ts_deal_flag", 1);
            volVal.put("code", "");
            datas.add(volVal);
        }
        List<Long> newIds = tt.batchInsert(UAS_ORG_INFO, datas);
        String hxOrgCode = ServiceCodeGenerator.generateOrgCode(3, newIds.get(0));
        tt.update("update " + UAS_ORG_INFO + " set code = ? where id = ?", hxOrgCode, newIds.get(0));
        return ret.size();
    }

}
