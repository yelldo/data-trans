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
 * 医疗机构表迁移合并
 * kt/MCS_COMPANY_INFO + kt/MCS_HOSPITAL_INFO + kt/MCS_REGULATOR_INFO -> hx/uas_org_info
 * <p>
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Service
public class HospitalMergeWork extends AbstractWorker implements Converter {

    @Override
    public boolean convert() {
        Object obj = tt.queryFirst(SQL.select("count(1)").from(MCS_HOSPITAL_INFO).where("ISDEL = '0'").build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, null);
        int offset = 0;
        int limit = LIMIT;
        log.info("HospitalMergeWork 任务开始 ======= total: {}", total);
        long dealTotal = 0;
        // 医疗机构信息表
        while (true) {
            int jobNum = hospitalInfo(offset, limit);
            dealTotal += jobNum;
            log.info("HospitalMergeWork 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            offset += limit;
            if (offset >= total) {
                break;
            }
        }
        log.info("HospitalMergeWork 任务结束 =======");
        return true;
    }

    private int hospitalInfo(int offset, int limit) {
        String sql = SQL.select("*").from(MCS_HOSPITAL_INFO).where("ISDEL = '0'").limit(limit).offset(offset).build();
        List<Map<String, Object>> ret = tt.queryForMapList(sql);
        List<Map<String, Object>> datas = new ArrayList<>();
        // 记录迁移过程的数据错误
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : ret) {
            // 清空内容，复用
            sb.delete(0, sb.length());
            Map<String, Object> volVal = new HashMap<>();
            volVal.put("type", 2);
            volVal.put("kt_org_id", map.get("HOSP_ID"));
            volVal.put("kt_code", map.get("HOSPCODE"));
            volVal.put("name", map.get("HOSPNAME"));
            volVal.put("org_short_name", map.get("HOSPNAME2"));
            volVal.put("organization_code", map.get("ORGCODE"));
            volVal.put("short_pinyin", map.get("HOSPPY"));
            Object regCode = map.get("REGCODE");
            volVal.put("kt_region_code", regCode);
            // 地区字段转成 code（如：330000,330100,330103）
            String locateAreaCode = ServiceCodeGenerator.generateLocateAreaCode(regCode+"",tt);
            if (locateAreaCode == null) {
                sb.append("地区找不到;");
            } else {
                volVal.put("locate_area", locateAreaCode);
            }

            volVal.put("contact_address", map.get("ADDRS"));
            volVal.put("hospital_type", map.get("HOSPCAT"));
            volVal.put("link_person", map.get("LINKMAN"));
            volVal.put("link_person_mobile", map.get("TEL"));
            volVal.put("fax", map.get("FOX"));
            volVal.put("email", map.get("EMAIL"));
            volVal.put("notes", map.get("REMARK"));
            volVal.put("create_time", map.get("CREATETIME"));
            volVal.put("modify_time", map.get("LASTUPDATE"));
            volVal.put("kt_data_network", (map.get("DATA_NETWORK") + "").substring(0, 1));
            // 医疗机构性质 -1 占位
            int[] kinds = new int[]{-1, 1, 2, 5, 4, 3};
            Object hosKind = map.get("HOSPITALTYPE");
            if (!StrUtils.isBlankOrNullVal(hosKind)) {
                int kind = ValChangeUtils.toIntegerIfNull(hosKind, null);
                volVal.put("hospital_kind", kinds[kind]);
            } else {
                // 其他
                volVal.put("hospital_kind", 5);
            }
            volVal.put("hospital_kind", map.get("HOSPITALTYPE"));
            volVal.put("hospital_base", map.get("ISBASE"));
            // 监管片区
            Object supArea = map.get("AREA");
            if (!StrUtils.isBlankOrNullVal(supArea)) {
                Map<String, Object> cno = tt.queryFirst(SQL.select("CVALUE").from(D_CODE).where("CNO = ?").build(), supArea + "00");
                volVal.put("supervise_area", cno.get("CVALUE"));
                //片区id
                Map<String, Object> supAreaId = tt.queryFirst(SQL.select("id").from(UAS_SUPERVISE_AREA).where("name like ?").build(), cno.get("CVALUE") + "%");
                if (supAreaId != null) {
                    volVal.put("supervise_area_id", supAreaId.get("id"));
                }
            }
            // 医疗机构等级
            Object hosLevel = map.get("HOSPLEVEL");
            if (!StrUtils.isBlankOrNullVal(hosLevel)) {
                Map<String, Object> level = tt.queryFirst(SQL.select("ORDIDX").from(D_CODE).where("CATEGORYNO = ?").build(), hosLevel);
                if (level == null) {
                    sb.append("'D_CODE'中没有对应的医疗机构等级;");
                    volVal.put("hospital_level", hosLevel);
                } else {
                    volVal.put("hospital_level", level.get("CVALUE"));
                }
            }

            volVal.put("credit", map.get("SOCIALCODE"));
            volVal.put("organization_file", map.get("FILE_ORGCODE"));
            volVal.put("buz_licence_file", map.get("FILE_BUSLISCENSE"));
            volVal.put("tax_file", map.get("FILE_TAXREG"));
            volVal.put("kt_product_cert_file", map.get("FILE_PERMIT"));
            volVal.put("audit_status", 3);
            // 错误记录
            volVal.put("ts_notes", sb.toString());
            volVal.put("ts_deal_flag", 1);
            volVal.put("code", "");
            datas.add(volVal);
        }
        List<Long> newIds = tt.batchInsert(UAS_ORG_INFO, datas);
        String hxOrgCode = ServiceCodeGenerator.generateOrgCode(2, newIds.get(0));
        tt.update("update " + UAS_ORG_INFO + " set code = ? where id = ?", hxOrgCode, newIds.get(0));

        return ret.size();
    }

}
