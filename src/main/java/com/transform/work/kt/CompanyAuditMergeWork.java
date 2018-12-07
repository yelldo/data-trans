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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业申报审核记录迁移
 * 以导进来的企业为主(创建时间>'2017-11-27')
 * 1.如果企业状态是审核通过或初始化状态（DATA_PASS）则根据mcs_company_info往uas_org_info_modify_apply表中添加一条记录
 * 2.如果企业状态是待提交、待审核、审核不通过，其中间信息是保存在mcs_company_info_do
 * <p>
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Service
public class CompanyAuditMergeWork extends AbstractWorker implements Converter {

    @Override
    public boolean convert() {
        Object obj = tt.queryFirst(SQL.select("count(1)").from(MCS_COMPANY_INFO).where("ISDEL = '0' and CREATETIME > '2017-11-27'").build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, null);
        int offset = 0;
        int limit = 300;
        log.info("CompanyAuditMergeWork 任务开始 ======= total: {}", total);
        long dealTotal = 0;
        // 企业变更操作历史记录表
        while (true) {
            int jobNum = batchMerge(offset, limit);
            dealTotal += jobNum;
            log.info("CompanyAuditMergeWork 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            offset += limit;
            if (offset >= total) {
                break;
            }
        }
        log.info("CompanyAuditMergeWork 任务结束 =======");
        return true;
    }

    private int batchMerge(int offset, int limit) {
        String sql = SQL.select("*").from(MCS_COMPANY_INFO).where("ISDEL = '0' and CREATETIME > '2017-11-27'").limit(limit).offset(offset).build();
        List<Map<String, Object>> ret = tt.queryForMapList(sql);
        List<Map<String, Object>> datas = new ArrayList<>();
        // 记录迁移过程的数据错误
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : ret) {
            Object ent_id = map.get("ENT_ID");
            //log.info("------------------------------ ENT_ID: {}",map.get("ENT_ID"));
            // 清空内容，复用
            sb.delete(0, sb.length());
            Map<String, Object> volVal = new HashMap<>();
            Integer auditS = ValChangeUtils.toIntegerIfNull(map.get("DATA_PASS"), null);
            // mcs_company_info: null未申报,0审核不通过，1审核通过，2待审核，4待提交
            // org_modify_apply: 0.初始生成,1.待审核,2.审核中,3.审核通过,4.审核不通过,5.未提交,6.管理单位修改
            // null未申报 -> 0初始生成
            if (auditS == null) {
                volVal.put("audit_status", 0);
            // 审核不通过
            } else if (auditS == 0) {
                Map<String,Object> map2 = tt.queryFirst(SQL.select("*").from(MCS_COMPANY_INFO_DO).where("ENT_ID = ?").build(), map.get("ENT_ID"));
                // kt出现脏数据：审核不通过的情况下，在do表中找不到记录，因为被采购中心修改了，在audit表中的状态是采购中心修改
                if (map2 == null) {
                    // 管理单位修改
                    volVal.put("audit_status", 6);
                } else {
                    map = map2;
                }
                volVal.put("audit_status", 4);
            // 审核通过
            } else if (auditS == 1) {
                volVal.put("audit_status", 3);
            // 待审核
            } else if (auditS == 2) {
                map = tt.queryFirst(SQL.select("*").from(MCS_COMPANY_INFO_DO).where("ENT_ID = ?").build(), map.get("ENT_ID"));
                volVal.put("audit_status", 1);
            // 待提交
            } else {
                map = tt.queryFirst(SQL.select("*").from(MCS_COMPANY_INFO_DO).where("ENT_ID = ?").build(), map.get("ENT_ID"));
                volVal.put("audit_status", 5);
            }

            // 审核信息
            Map<String, Object> auditRecord = tt.queryFirst(SQL.select("*")//
                    .from(MCS_ORGAN_AUDIT).where("LINK_ID = ? order by CREATETIME desc").build(), ent_id);
            if (auditRecord != null) {
                volVal.put("audit_time", map.get("CREATETIME"));
                volVal.put("audit_person", map.get("AUDITUSER_NAME"));
                volVal.put("kt_audit_person_id", map.get("AUDITUSER_ID"));
                volVal.put("audit_desc", map.get("AUDITOPINION"));
                volVal.put("kt_apply_person_id", map.get("USER_ID"));
            }

            volVal.put("kt_org_id", map.get("ENT_ID"));
            volVal.put("kt_code", map.get("COMPID"));
            volVal.put("name", map.get("COMPNAME"));
            volVal.put("type", 1);
            volVal.put("org_short_name", map.get("COMPNAME2"));
            volVal.put("organization_code", map.get("ORGCODE"));
            volVal.put("short_pinyin", map.get("COMPPY"));
            Object regCode = map.get("REGCODE");
            volVal.put("kt_region_code", regCode);
            // 地区字段转成 code（如：330000,330100,330103）
            String locateAreaCode = ServiceCodeGenerator.generateLocateAreaCode(regCode+"",tt);
            if (locateAreaCode == null) {
                sb.append("地区找不到;");
            } else {
                volVal.put("locate_area", locateAreaCode);
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
                } else if ("domestic".equals(entType)) {
                    // 生产企业
                    volVal.put("enterprise_type", 1);
                }
            } else if ("2".equals(compType)) {
                // 配送
                volVal.put("enterprise_type", 3);
            } else if ("3".equals(compType)) {
                // 生产及配送(暂时在kt耗材联采库中不存在这种类型的)
                volVal.put("enterprise_type", 5);
            }

            // 福建省药械联合阳光采购申请函
            volVal.put("application_file", map.get("FILE_AUTHORIZED"));

            // 凯特生产或经营许可证放同一字段，所以copy到两个字段
            volVal.put("product_cert_file", map.get("FILE_PERMIT"));
            volVal.put("bus_cert_file", map.get("FILE_PERMIT"));
            volVal.put("product_cert_end_date", map.get("REGNOENDDATE"));
            volVal.put("business_cert_end_date", map.get("REGNOENDDATE"));
            volVal.put("product_cert_num", map.get("LICENCE"));
            volVal.put("business_cert_num", map.get("LICENCE"));

            volVal.put("kt_enttype", entType);
            // COMPTYPE
            //volVal.put("kt_enterprise_type", compType);
            //volVal.put("kt_licence", map.get("LICENCE"));
            // 注册资金
            Object regFunds = map.get("REGCAP");
            if (regFunds != null) {
                BigDecimal d = new BigDecimal(regFunds.toString());
                try {
                    regFunds = d.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                } catch (Exception e) {
                    log.error("注册资金（万元）转换失败，kt_org_id:{}",map.get("ENT_ID"));
                }
            }
            volVal.put("register_funds", regFunds);
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
            Map<String, Object> hxOrgId = tt.queryFirst(SQL.select("id","code").from(UAS_ORG_INFO).where("kt_org_id = ?").build(), ent_id);
            if (hxOrgId != null) {
                volVal.put("org_info_id", ValChangeUtils.toLong(hxOrgId.get("id"),null));
                volVal.put("code", hxOrgId.get("code"));
            }else{
                // 关联不上就跳过
                continue;
            }
            // 错误记录
            volVal.put("ts_notes", sb.toString());
            volVal.put("ts_deal_flag", 1);
            datas.add(volVal);
        }
        tt.batchInsert(UAS_ORG_INFO_MODIFY_APPLY, datas);
        return ret.size();
    }

}
