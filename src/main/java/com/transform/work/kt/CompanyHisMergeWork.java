package com.transform.work.kt;

import com.transform.jdbc.SQL;
import com.transform.util.CalculateUtils;
import com.transform.util.StrUtils;
import com.transform.util.ValChangeUtils;
import com.transform.work.AbstractWorker;
import com.transform.work.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 企业申报审核记录迁移
 * kt/mcs_company_info_do + mcs_organ_audit -> org_modify_apply
 * kt/mcs_company_info_his + mcs_organ_audit -> org_modify_apply_his
 * <p>
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Service
public class CompanyHisMergeWork extends AbstractWorker implements Converter {

    @Override
    public boolean convert() {
        Object obj = tt.queryFirst(SQL.select("count(1)").from(MCS_COMPANY_INFO_DO_HIS).where("ISDEL = '0' and CREATETIME > '2017-11-27'").build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, null);
        int offset = 0;
        int limit = 300;
        log.info("CompanyHisMergeWork 任务开始 ======= total: {}", total);
        long dealTotal = 0;
        // 企业变更操作历史记录表
        while (true) {
            int jobNum = batchMerge(offset, limit);
            dealTotal += jobNum;
            log.info("CompanyHisMergeWork 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            offset += limit;
            if (offset >= total) {
                break;
            }
        }
        log.info("CompanyHisMergeWork 任务结束 =======");
        return true;
    }

    private int batchMerge(int offset, int limit) {
        String sql = SQL.select("*").from(MCS_COMPANY_INFO_DO_HIS).where("ISDEL = '0' and CREATETIME > '2017-11-27'").limit(limit).offset(offset).build();
        List<Map<String, Object>> ret = tt.queryForMapList(sql);
        List<Map<String, Object>> datas = new ArrayList<>();
        // 记录迁移过程的数据错误
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : ret) {
            // 清空内容，复用
            sb.delete(0, sb.length());
            Map<String, Object> volVal = new HashMap<>();
            volVal.put("kt_org_id", map.get("ENT_ID"));
            volVal.put("kt_code", map.get("COMPID"));
            volVal.put("name", map.get("COMPNAME"));
            volVal.put("type", 1);
            //log.info("------------------------------ ENT_ID: {}",map.get("ENT_ID"));
            volVal.put("org_short_name", map.get("COMPNAME2"));
            volVal.put("organization_code", map.get("ORGCODE"));
            volVal.put("short_pinyin", map.get("COMPPY"));
            Object regCode = map.get("REGCODE");
            volVal.put("kt_region_code", regCode);
            // 所在地区
            if (!StrUtils.isBlankOrNullVal(regCode)) {
                Map<String, Object> area = tt.queryFirst(SQL.select("name").from(UAS_BASE_AREA).where("id = ?").build(), regCode);
                if (area == null) {
                    Map<String, Object> dcode = tt.queryFirst(SQL.select("CVALUE").from(D_CODE).where("CNO = ?").build(), regCode);
                    if (dcode == null) {
                        sb.append("地区找不到;");
                    }
                } else {
                    volVal.put("locate_area", area.get("name"));
                }
            }
            volVal.put("kt_is_province", map.get("ISPROVINCE"));
            volVal.put("contact_address", map.get("ADDRS"));
            String compType = (map.get("COMPTYPE") + "").substring(0, 1);
            // 1.生产企业,2.代理企业,3.配送企业,4.生产及代理,5.生产及配送,6.代理及配送,7.生产,代理及配送
            String entType = map.get("ENTTYPE") + "";
            if ("1".equals(compType)) {
                if ("imported".equals(entType)) {
                    // 代理企业
                    volVal.put("enterprise_type", 2);
                    volVal.put("bus_cert_file", map.get("FILE_PERMIT"));
                } else if ("domestic".equals(entType)) {
                    // 生产企业
                    volVal.put("enterprise_type", 1);
                    volVal.put("product_cert_file", map.get("FILE_PERMIT"));
                }
            } else if ("2".equals(compType)) {
                // 配送
                volVal.put("enterprise_type", 3);
                volVal.put("bus_cert_file", map.get("FILE_PERMIT"));
            } else if ("3".equals(compType)) {
                // 生产及配送(暂时在kt耗材联采库中不存在这种类型的)
                volVal.put("enterprise_type", 5);
            }
            volVal.put("kt_enttype", entType);
            // COMPTYPE
            volVal.put("kt_enterprise_type", compType);
            volVal.put("kt_licence", map.get("LICENCE"));
            volVal.put("register_funds", map.get("REGCAP"));
            volVal.put("found_date", map.get("ESTDATE"));
            volVal.put("business_end_time", map.get("ENDDATE"));
            volVal.put("legal_person", map.get("LEREP"));
            volVal.put("link_person", map.get("LINKMAN"));
            volVal.put("link_person_mobile", map.get("TEL"));
            volVal.put("fax", map.get("FOX"));
            volVal.put("email", map.get("EMAIL"));
            volVal.put("postal_code", map.get("POSTCODE"));
            volVal.put("notes", map.get("REMARK"));
            volVal.put("create_time", map.get("CREATETIME"));
            volVal.put("modify_time", map.get("LASTUPDATE"));
            volVal.put("oversea", map.get("ISHOME"));
            volVal.put("kt_data_network", (map.get("DATA_NETWORK") + "").substring(0, 1));
            volVal.put("product_cert_end_date", map.get("REGNOENDDATE"));
            // 配送范围（配送地区）
            String disRange = map.get("DISRANGE") + "";
            if (!StrUtils.isBlankOrNullVal(disRange)) {
                if (disRange.contains(",")) {
                    volVal.put("kt_dis_range", disRange.replace(",", "00,") + "00");
                } else {
                    volVal.put("kt_dis_range", disRange + "00");
                }
            }
            volVal.put("kt_dis_range", map.get("DISRANGE"));
            volVal.put("kt_bak_link_person", map.get("LINKMAN2"));
            volVal.put("kt_bak_link_person_mobile", map.get("TEL2"));
            volVal.put("organization_file", map.get("FILE_ORGCODE"));
            volVal.put("buz_licence_file", map.get("FILE_BUSLISCENSE"));
            volVal.put("tax_file", map.get("FILE_TAXREG"));
            volVal.put("credit", map.get("SOCIALCODE"));
            volVal.put("kt_combined", map.get("COMBINED"));
            volVal.put("kt_combined_name", map.get("COMBINEDNAME"));
            volVal.put("kt_last_update_cgzx", map.get("LASTUPDATE_CGZX"));
            volVal.put("kt_combined_id", map.get("COMBINEDID"));
            volVal.put("product_category", map.get("PRODUCT_CLASS"));
            volVal.put("legal_person_idcard_file", map.get("FILE_OWNER"));
            volVal.put("kt_auth_person_idcard_file", map.get("FILE_AUTHORIZED"));
            volVal.put("social_insurance_file", map.get("FILE_INSURANCE"));
            volVal.put("other_ref_cert_file", map.get("FILE_OTHER"));
            volVal.put("authorization_file", map.get("FILE_INSTRUMENT"));
            volVal.put("authorization_cert_file", map.get("FILE_INSTRUMENTCERT"));
            volVal.put("kt_commitment_file", map.get("FILE_COMMITMENT"));
            volVal.put("auth_person_idcard", map.get("AUTHORIZED_ID"));
            volVal.put("three_cert_in_one", map.get("IFTHREEINONE"));

            volVal.put("auth_person_name", map.get("AUTHORIZED_NAME"));
            volVal.put("auth_person_mobile", map.get("AUTHORIZED_TEL"));
            volVal.put("kt_cgzx_notes", map.get("CGZX_REMARK"));

            // 关联hx_org_id
            Map<String, Object> hxOrgId = tt.queryFirst(SQL.select("id","code").from(UAS_ORG_INFO).where("kt_org_id = ?").build(), map.get("ENT_ID"));
            if (hxOrgId != null) {
                volVal.put("org_info_id", ValChangeUtils.toLong(hxOrgId.get("id"), null));
                volVal.put("code", hxOrgId.get("code"));
            } else {
                //
                continue;
            }
            // 关联状态
            // 在mcs_organ_audit中查找对应的记录，如有取其AUDITSTATUS
            // his中记录对应到do表可能存在2条，1条是未审核过的（isaudit=0 待审核）,1条是审核过的（isaudit=1 审核不通过）
            String orgAuditSql = SQL.select("AUDITSTATUS", "ISAUDIT").from(MCS_ORGAN_AUDIT).where("CHANGEID = ? and ISDEAL = '0'").build();
            Map<String, Object> orgAuditMap = tt.queryFirst(orgAuditSql, map.get("CHANGEID"));
            if (orgAuditMap == null) {
                sb.append("在audit表中找不到his表中对应的记录,CHANGEID:" + map.get("CHANGEID") + ";");
            }
            Integer auditStatus2 = ValChangeUtils.toIntegerIfNull(orgAuditMap.get("AUDITSTATUS"), null);
            switch (auditStatus2) {
                case 1:
                    volVal.put("audit_status", 1); // 待审核
                    break;
                case 2:
                    volVal.put("audit_status", 4); // 审核不通过
                    break;
                case 3:
                    volVal.put("audit_status", 3); // 审核通过
                    break;
                case 4:
                    volVal.put("audit_status", 5); // 管理单位修改
                    break;
                case 99:
                    volVal.put("audit_status", 0); // 初始生成
                    break;
            }
            // 错误记录
            volVal.put("ts_notes", sb.toString());
            volVal.put("ts_deal_flag", 1);
            datas.add(volVal);
        }
        tt.batchInsert(UAS_ORG_INFO_MODIFY_APPLY_HIS, datas);
        return ret.size();
    }

}
