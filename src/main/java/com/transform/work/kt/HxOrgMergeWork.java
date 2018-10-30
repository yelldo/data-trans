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
 * 海西公示系统机构数据合入统一平台
 * hec_dup_fm_tender_org -> mcs_company_info
 * <p>
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Service
public class HxOrgMergeWork extends AbstractWorker implements Converter {

    @Override
    public boolean convert() {
        Object obj = tt.queryFirst(SQL.select("count(1)").from(HEC_DUP_FM_TENDER_ORG).where("DATA_STATUS = '1'").build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, null);
        int offset = 0;
        int limit = LIMIT;
        log.info("HxOrgMergeWork 任务开始 ======= total: {}", total);
        long dealTotal = 0;
        while (true) {
            int jobNum = convertSub(offset, limit);
            dealTotal += jobNum;
            log.info("HxOrgMergeWork 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            offset += limit;
            if (offset >= total) {
                break;
            }
        }
        log.info("HxOrgMergeWork 任务结束 =======");
        return true;
    }

    private int convertSub(int offset, int limit) {
        String sql = SQL.select("*").from(HEC_DUP_FM_TENDER_ORG).where("DATA_STATUS = '1'").limit(limit).offset(offset).build();
        List<Map<String, Object>> ret = tt.queryForMapList(sql);
        List<Map<String, Object>> datas = new ArrayList<>();
        // 记录迁移过程的数据错误
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : ret) {
            // 清空内容，复用
            sb.delete(0, sb.length());
            Map<String, Object> volVal = new HashMap<>();
            volVal.put("kt_org_id", map.get("ID"));
            volVal.put("code", map.get("ORG_CODE"));
            volVal.put("name", map.get("ORG_NAME"));
            volVal.put("org_short_name", map.get("ORG_ABBR"));
            Object orgType = map.get("ORG_TYPE");
            // kt:  1生产企业、2代理企业、3生产及代理企业，4 配送机构
            // hx:  1.生产企业,2.代理企业,3.配送企业,4.生产及代理,5.生产及配送,6.代理及配送,7.生产,代理及配送
            if (orgType != null && orgType.toString().toLowerCase().equals("null")) {
                int[] ss = {-1,1,2,4,3};
                volVal.put("type", ss[ValChangeUtils.toIntegerIfNull(orgType,0)]);
            } else {
                continue;
            }
            Object auditStatus = map.get("ORGDECL_STATUS")+"";
            if (auditStatus != null && auditStatus.toString().toLowerCase().equals("null")) {
                int[] ss = {4,3,1,0,1};
                volVal.put("audit_status", ss[ValChangeUtils.toIntegerIfNull(auditStatus,0)]);
            }
            volVal.put("create_time", map.get("CREATE_TIME"));
            volVal.put("modify_time", map.get("MODIFY_TIME"));
            // 错误记录
            volVal.put("code", "");
            volVal.put("ts_notes", "CONFIRM_STATUS:"+map.get("CONFIRM_STATUS")+",IS_SMP:"+map.get("IS_SMP"));
            volVal.put("ts_deal_flag", 1);
            datas.add(volVal);
        }
        List<Long> newIds = tt.batchInsert(UAS_ORG_INFO, datas);
        if (newIds.size() > 0) {
            String hxOrgCode = ServiceCodeGenerator.generateOrgCode(1, newIds.get(0));
            tt.update("update " + UAS_ORG_INFO + " set code = ? where id = ?", hxOrgCode, newIds.get(0));
        }
        return ret.size();
    }

    /**
     * 合并相同企业名称，不同企业id：
     * 2配送企业->1生产或代理企业，并删除2配送企业那条数据
     * 2disrange->1-disrange
     * 2ent_id->1kt_merged_id
     * 2ent_type->1kt_merged_type
     */
    private void removeDuplicate() {

    }

}
