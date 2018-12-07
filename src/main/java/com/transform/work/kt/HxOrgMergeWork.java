package com.transform.work.kt;

import com.transform.jdbc.SQL;
import com.transform.util.CalculateUtils;
import com.transform.util.ServiceCodeGenerator;
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
 * 原申投诉系统机构数据合入统一平台
 * hec_dup_fm_tender_org -> uas_org_info
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
            int jobNum = allOrgs(offset, limit);
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

    private int allOrgs(int offset, int limit) {
        /*
        迁移所有机构，无论有没有对应的用户
         */
        //String sql = SQL.select("*").from(HEC_DUP_FM_TENDER_ORG).where("DATA_STATUS = '1'").limit(limit).offset(offset).build();

        /*
        只迁移有对应用户的机构
         */
        String sql = SQL//
                .select("a.*")//
                .from(HEC_DUP_FM_TENDER_ORG + " a," + HEC_UPO_PRJ_USER + " b")//
                .where("a.ID = b.ORG_ID and b.STATUS = '1' and a.DATA_STATUS = '1' group by a.ID")//
                .limit(limit).offset(offset)//
                .build();

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
            if (orgType != null && !orgType.toString().toLowerCase().equals("null")) {
                // 如果原始数据org_type为空，则指定为生产及代理企业（和俊杰沟通过，这些数据可能是生产企业，或代理企业（即申报企业））
                int[] ss = {4,1,2,4,3};
                int hxOrgType = ss[ValChangeUtils.toIntegerIfNull(orgType,0)];
                volVal.put("type", hxOrgType);
                if (hxOrgType == 0) {
                    // orgType == null
                    sb.append("原始数据ORG_TYPE为空;");
                }
            }
            // kt: 企业资质审核状态：0审核不通过，1审核通过，2待审核，3未提交，4审核中
            volVal.put("kt_orgdecl_status", map.get("ORGDECL_STATUS"));
            /*Object auditStatus = map.get("ORGDECL_STATUS")+"";
            if (auditStatus != null && !auditStatus.toString().toLowerCase().equals("null")) {
                int[] ss = {4,3,1,0,1};
                volVal.put("audit_status", ss[ValChangeUtils.toIntegerIfNull(auditStatus,0)]);
            }*/
            volVal.put("create_time", map.get("CREATE_TIME"));
            volVal.put("modify_time", map.get("MODIFY_TIME"));
            // 错误记录
            volVal.put("code", "");
            // 管理资质审核状态：0审核不通过，1审核通过，2待审核，3未提交，4审核中
            volVal.put("kt_orgmag_status", map.get("ORGMAG_STATUS"));
            // 是否配送企业：1是，0否
            volVal.put("kt_is_distribution", map.get("IS_DISTRIBUTION"));
            // 确认状态：0 未确认， 1未确认
            volVal.put("kt_confirm_status", map.get("CONFIRM_STATUS"));
            // 是否SMP同步：0否，1是，2挂网环节新增机构
            volVal.put("kt_is_smp", map.get("IS_SMP"));
            volVal.put("ts_notes", sb.toString());
            volVal.put("ts_deal_flag", 2); // 来源于hx公示系统
            datas.add(volVal);
        }
        List<Long> newIds = tt.batchInsert(UAS_ORG_INFO, datas);
        if (newIds.size() > 0) {
            String hxOrgCode = ServiceCodeGenerator.generateOrgCode(1, newIds.get(0));
            tt.update("update " + UAS_ORG_INFO + " set code = ? where id = ?", hxOrgCode, newIds.get(0));
        }
        return ret.size();
    }


}
