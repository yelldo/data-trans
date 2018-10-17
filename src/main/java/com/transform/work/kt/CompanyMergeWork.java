package com.transform.work.kt;

import com.transform.jdbc.SQL;
import com.transform.util.CalculateUtils;
import com.transform.util.StrUtils;
import com.transform.util.ValChangeUtils;
import com.transform.work.AbstractWorker;
import com.transform.work.MergeWork;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业信息表迁移合并
 * kt/MCS_COMPANY_INFO + kt/MCS_HOSPITAL_INFO + kt/MCS_REGULATOR_INFO -> hx/uas_org_info
 * <p>
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Service
public class CompanyMergeWork extends AbstractWorker implements MergeWork {

    @Override
    public boolean merge() {
        Object obj = tt.queryFirst(SQL.select("count(1)").from(MCS_COMPANY_INFO).where("ISDEL = '0'").build()).get("count(1)");
        int total = ValChangeUtils.toInteger(obj, null);
        int offset = 0;
        int limit = 300;
        log.info("CompanyMergeWork 任务开始 ======= total: {}", total);
        long dealTotal = 0;
        // 企业信息表
        while (true) {
            int jobNum = companyInfo(offset, limit);
            dealTotal += jobNum;
            log.info("CompanyMergeWork 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            offset += limit;
            if (offset > total) {
                break;
            }
        }
        log.info("CompanyMergeWork 任务结束 =======");
        return true;
    }

    private int companyInfo(int offset, int limit) {
        String sql = SQL.select("*").from(MCS_COMPANY_INFO).where("ISDEL = '0'").limit(limit).offset(offset).build();
        List<Map<String, Object>> ret = tt.queryForMapList(sql);
        List<Map<String, Object>> datas = new ArrayList<>();
        // 记录迁移过程的数据错误
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : ret) {
            // 清空内容，复用
            sb.delete(0, sb.length());
            Map<String, Object> volVal = new HashMap<>();
            volVal.put("type", 1);
            volVal.put("kt_org_id", map.get("ENT_ID"));
            //log.info("------------------------------ ENT_ID: {}",map.get("ENT_ID"));
            volVal.put("kt_code", map.get("COMPID"));
            volVal.put("name", map.get("COMPNAME"));
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
            volVal.put("kt_enterprise_type", (map.get("COMPTYPE") + "").substring(0, 1));
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
            volVal.put("kt_product_cert_file", map.get("FILE_PERMIT"));
            volVal.put("credit", map.get("SOCIALCODE"));
            volVal.put("kt_combined", map.get("COMBINED"));
            volVal.put("kt_combined_name", map.get("COMBINEDNAME"));
            volVal.put("kt_last_update_cgzx", map.get("LASTUPDATE_CGZX"));
            volVal.put("kt_combined_id", map.get("COMBINEDID"));
            volVal.put("kt_product_class", map.get("PRODUCT_CLASS"));
            volVal.put("legal_person_idcard_file", map.get("FILE_OWNER"));
            volVal.put("kt_auth_person_idcard_file", map.get("FILE_AUTHORIZED"));
            volVal.put("kt_insurance_file", map.get("FILE_INSURANCE"));
            volVal.put("other_ref_cert_file", map.get("FILE_OTHER"));
            volVal.put("kt_instrument_file", map.get("FILE_INSTRUMENT"));
            volVal.put("kt_instrument_cert_file", map.get("FILE_INSTRUMENTCERT"));
            volVal.put("kt_commitment_file", map.get("FILE_COMMITMENT"));
            // 审核状态
            int[] status = new int[]{4, 3, 1, -1, 5};
            Object auditStatus = map.get("DATA_PASS");
            if (!StrUtils.isBlankOrNullVal(auditStatus)) {
                volVal.put("audit_status", status[ValChangeUtils.toInteger(auditStatus, null)]);
            } else {
                volVal.put("audit_status", 6);
            }
            volVal.put("audit_status", map.get("DATA_PASS"));
            volVal.put("auth_person_idcard", map.get("AUTHORIZED_ID"));
            volVal.put("three_cert_in_one", map.get("IFTHREEINONE"));
            volVal.put("kt_enttype", map.get("ENTTYPE"));
            volVal.put("auth_person_name", map.get("AUTHORIZED_NAME"));
            volVal.put("auth_person_mobile", map.get("AUTHORIZED_TEL"));
            volVal.put("kt_cgzx_notes", map.get("CGZX_REMARK"));
            // 错误记录
            volVal.put("ts_notes", sb.toString());
            volVal.put("ts_deal_flag", 1);
            datas.add(volVal);
        }
        tt.batchInsert(UAS_ORG_INFO, datas);
        return ret.size();
    }

}
