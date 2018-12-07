package com.transform.work.kt;

import com.transform.jdbc.SQL;
import com.transform.util.CalculateUtils;
import com.transform.util.ServiceCodeGenerator;
import com.transform.util.ValChangeUtils;
import com.transform.work.AbstractWorker;
import com.transform.work.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 机构用户合并
 * kt : sys_n_users -> uas_org_user
 * hx : HEC_UPO_PRJ_USER -> uas_org_user
 * <p>
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Service
public class OrgUserMergeWork extends AbstractWorker implements Converter {

    @Value("${init.hcsb.regorg.id}")
    private Long regOrgId;

    @Value("${init.hcsb.platorg.id}")
    private Long platOrgId;

    @Override
    public boolean convert() {
        ktUserConvert();
        hxUserConvert();
        return true;
    }

    private void ktUserConvert() {
        Object obj = tt.queryFirst(SQL.select("count(1)").from(SYS_N_USERS).build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, null);
        int offset = 0;
        int limit = 300;
        log.info("OrgUserMergeWork-ktUserConvert 任务开始 ======= total: {}", total);
        long dealTotal = 0;
        while (true) {
            int jobNum = ktUser(offset, limit);
            dealTotal += jobNum;
            log.info("OrgUserMergeWork-ktUserConvert 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            offset += limit;
            if (offset >= total) {
                break;
            }
        }
        log.info("OrgUserMergeWork-ktUserConvert 任务结束 =======");
    }

    private void hxUserConvert() {
        //Object obj = tt.queryFirst(SQL.select("count(1)").from(HEC_UPO_PRJ_USER).build()).get("count(1)");
        String sql = SQL//
                .select("a.*", "b.CERT_NUMBER", "b.EXP_DATE", "b.CERT_ORG_NAME")//
                .from(HEC_UPO_PRJ_USER + " a")//
                .leftOuterJoin(AUTH_USER_CERT + " b")//
                .on("a.ID = b.USER_ID")//
                .where("b.DATA_STATUS = '1' and a.STATUS = '1'").build();
        List<Object> list = tt.queryForList(sql, null, null);
        //int total = ValChangeUtils.toIntegerIfNull(obj, null);
        int total = ValChangeUtils.toIntegerIfNull(list == null ? 0 : list.size(), null);
        int offset = 0;
        int limit = 300;
        log.info("OrgUserMergeWork-hxUserConvert 任务开始 ======= total: {}", total);
        long dealTotal = 0;
        // 管理单位表
        while (true) {
            int jobNum = hxUser(offset, limit);
            dealTotal += jobNum;
            log.info("OrgUserMergeWork-hxUserConvert 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            offset += limit;
            if (offset >= total) {
                break;
            }
        }
        log.info("OrgUserMergeWork-hxUserConvert 任务结束 =======");
    }

    private int ktUser(int offset, int limit) {
        //String sql = SQL.select("*").from(SYS_N_USERS).limit(limit).offset(offset).build();
        String sql = SQL.select("*").from(SYS_N_USERS).limit(limit).offset(offset).build();
        List<Map<String, Object>> ret = tt.queryForMapList(sql);
        List<Map<String, Object>> datas = new ArrayList<>();
        // 记录迁移过程的数据错误
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : ret) {
            // 清空内容，复用
            sb.delete(0, sb.length());
            Map<String, Object> volVal = new HashMap<>();
            volVal.put("kt_org_id", map.get("ORGANID"));
            volVal.put("kt_opno", map.get("OPNO"));
            volVal.put("realname", map.get("OPNAME"));
            volVal.put("kt_pki", map.get("PKI"));
            volVal.put("userpwd", map.get("PWD"));
            volVal.put("create_time", map.get("CREATETIME"));
            volVal.put("last_login_ip", map.get("LASTLOGINIP"));
            volVal.put("latest_login_time", map.get("ACTIVETIME"));
            volVal.put("kt_login_times", map.get("LOGINTIMES"));
            volVal.put("kt_pre_login_ip", map.get("PREVLOGINIP"));
            volVal.put("kt_pre_login_time", map.get("PREVLOGINTIME"));
            volVal.put("kt_op_limit", map.get("OPLIMIT"));
            Integer enabled = ValChangeUtils.toIntegerIfNull(map.get("ENABLED"), -1);
            if (enabled == 0) {
                volVal.put("status", 2);
            } else if (enabled == 1) {
                volVal.put("status", 1);
            } else {
                // 如果不是1或2，则标注为不可用
                volVal.put("status", 2);
            }
            volVal.put("notes", map.get("REMARK"));
            volVal.put("kt_secrecy", map.get("SECRECY"));
            volVal.put("kt_op_type", map.get("OPTYPE")); // 表中没有说明各个值分别表示什么？？？

            // kt: 1、是CA登录，2、不是CA登录
            // hx: 1:均可用,2:账号登录,3:CA登录
            Integer loginMode = ValChangeUtils.toIntegerIfNull(map.get("ISCAKEY"), -1);
            if (loginMode == 1) {
                // ca登录
                volVal.put("loginmode", 3);
            } else if (loginMode == 2) {
                // 账号登录
                volVal.put("loginmode", 2);
            } else {
                // if null then 均可
                volVal.put("loginmode", 1);
                sb.append("迁移数据登录方式为空;");
            }

            // 如果账号为空，则取用户名称
            volVal.put("username", map.get("OPACCOUNT") == null ? map.get("OPNAME") : map.get("OPACCOUNT"));
            volVal.put("general_name", map.get("OPACCOUNT2"));
            volVal.put("email", map.get("EMAIL"));
            volVal.put("modify_time", map.get("LASTUPDATE"));
            volVal.put("kt_data_network", (map.get("DATA_NETWORK") + "").substring(0, 1));
            // 关联机构id
            Map<String, Object> hxOrgId = tt.queryFirst(SQL.select("id", "code").from(UAS_ORG_INFO).where("kt_org_id = ?").build(), map.get("ORGANID"));
            if (hxOrgId != null) {
                volVal.put("org_info_id", ValChangeUtils.toLong(hxOrgId.get("id"), null));
            } else {
                //
                continue;
            }

            //1 企业主帐号，0 企业子帐号
            // 1:主账号,2:子账号,3:管理员（kt）
            Integer isadmin = ValChangeUtils.toIntegerIfNull(map.get("ISMAIN"), -1);
            if (isadmin == 1) {
                volVal.put("primary_account", 1);
            } else if (isadmin == 0) {
                volVal.put("primary_account", 2);
            } else {
                volVal.put("primary_account", isadmin);
            }
            volVal.put("kt_unitid_id", map.get("UNITID")); //UNITID和ORGANID什么区别
            volVal.put("link_man", map.get("LINKMAIN"));
            volVal.put("link_tel", map.get("LINKTEL"));
            volVal.put("mobile", map.get("LINKTEL"));
            //volVal.put("ca_cert", map.get("CERNO")); // kt不提供ca证书序列号
            volVal.put("kt_is_business", map.get("ISBUSINESS"));
            // 错误记录
            volVal.put("ts_notes", sb.toString());
            volVal.put("ts_deal_flag", 1);
            volVal.put("pwdhxupdate", 0); // 未在海西系统更改过密码
            datas.add(volVal);
        }
        tt.batchInsert(UAS_ORG_USER, datas);
        return ret.size();
    }

    /**
     * 如果找不到对应的机构，则根据用户信息来创建机构
     *
     * @param offset
     * @param limit
     * @return
     */
    private int hxUser(int offset, int limit) {
        String sql = SQL//
                .select("a.*", "b.CERT_NUMBER", "b.EXP_DATE", "b.CERT_ORG_NAME")//
                .from(HEC_UPO_PRJ_USER + " a")//
                .leftOuterJoin(AUTH_USER_CERT + " b")//
                .on("a.ID = b.USER_ID")//
                .where("b.DATA_STATUS = '1' and a.STATUS = '1'").build();
        //String sql = SQL.select("*").from(HEC_UPO_PRJ_USER).limit(limit).offset(offset).build();
        List<Map<String, Object>> ret = tt.queryForMapList(sql);
        List<Map<String, Object>> datas = new ArrayList<>();
        // 记录迁移过程的数据错误
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : ret) {
            // 清空内容，复用
            sb.delete(0, sb.length());
            Map<String, Object> volVal = new HashMap<>();
            volVal.put("kt_org_id", map.get("ORG_ID"));
            volVal.put("kt_opno", map.get("ID"));
            // 如果账号为空，则取用户名称
            volVal.put("username", map.get("ACCOUNT") == null ? map.get("USER_NAME") : map.get("ACCOUNT"));
            volVal.put("realname", map.get("USER_NAME"));
            volVal.put("userpwd", map.get("PASSWORD"));

            // ca相关
            volVal.put("ca_cert", map.get("CERT_NUMBER"));
            volVal.put("ca_express", map.get("EXP_DATE"));

            // 2 系统管理员，1 企业主帐号，0 企业子帐号
            // 1:主账号,2:子账号,3:管理员（kt）
            Integer isadmin = ValChangeUtils.toIntegerIfNull(map.get("IS_ADMIN"), -1);
            if (isadmin == 1) {
                volVal.put("primary_account", 1);
            } else if (isadmin == 0) {
                volVal.put("primary_account", 2);
            } else if (isadmin == 2) {
                volVal.put("primary_account", 3);
            } else {
                volVal.put("primary_account", isadmin);
            }

            // ORG_ID=1的账号都是没有对应机构的数据
            if ("1".equals(map.get("ORG_ID"))) {
                if ("1".equals(map.get("USER_TYPE"))) {
                    // 监管单位账号
                    volVal.put("primary_account", 3);
                    volVal.put("org_info_id", regOrgId); // 福建监管（申投诉迁移）
                    sb.append("来自申投诉系统，没有对应机构;");
                } else if ("4".equals(map.get("USER_TYPE"))) {
                    // 海西人员账号
                    volVal.put("primary_account", 3);
                    volVal.put("org_info_id", platOrgId); // 海西运营中心
                    sb.append("来自申投诉系统，没有对应机构;");
                }
            } else {
                // 关联机构id
                Map<String, Object> hxOrgId = tt.queryFirst(SQL.select("id", "code").from(UAS_ORG_INFO).where("kt_org_id = ?").build(), map.get("ORG_ID"));
                if (hxOrgId != null) {
                    volVal.put("org_info_id", ValChangeUtils.toLong(hxOrgId.get("id"), null));
                } else {
                    // 找不到对应的机构就根据用户信息来创建机构
                    //1 监管单位
                    //2 企业
                    //3 企业
                    //4 海西人员账号
                    //7 配送企业
                    Map<String, Object> newOrg = new HashMap<>();
                    if ("2".equals(map.get("USER_TYPE")) || "3".equals(map.get("USER_TYPE"))) {
                        newOrg.put("enterprise_type", 4);// 生产及代理
                    } else if ("7".equals(map.get("USER_TYPE"))) {
                        newOrg.put("enterprise_type", 3);// 配送企业
                    }
                    newOrg.put("ts_deal_flag", 2);
                    newOrg.put("audit_status", 3);
                    newOrg.put("name", "");
                    newOrg.put("type", 1);
                    newOrg.put("code", "");
                    Long newOrgId = tt.insert(UAS_ORG_INFO, newOrg);
                    String hxOrgCode = ServiceCodeGenerator.generateOrgCode(1, newOrgId);
                    newOrg.put("code", hxOrgCode);
                    newOrg.put("name", "申投诉迁移机构" + hxOrgCode);
                    tt.update("update " + UAS_ORG_INFO + " set code = ? where id = ?", hxOrgCode, newOrgId);
                    // 关联机构id
                    volVal.put("org_info_id", newOrgId);
                }
            }

            volVal.put("email", map.get("EMAIL"));

            //状态（1:启用,0:禁用）
            Integer enabled = ValChangeUtils.toIntegerIfNull(map.get("STATUS"), -1);
            if (enabled == 0) {
                volVal.put("status", 2);
            } else if (enabled == 1) {
                volVal.put("status", 1);
            } else {
                // 其他状态标注为不可用
                volVal.put("status", 2);
            }
            volVal.put("mobile", map.get("PHONE"));
            volVal.put("create_time", map.get("CREATE_TIME"));
            volVal.put("modify_time", map.get("MODIFY_TIME"));
            volVal.put("latest_login_time", map.get("LAST_LOGIN_TIME"));
            volVal.put("last_login_ip", map.get("LAST_LOGIN_IP"));
            //kt: 0：超级管理员、2：采购中心、3：医疗机构、5：生产企业、6：监管机构、8：配送企业 9：企业超级用户或生产企业超级用户或医院超级用户
            //hx-ss: 1：、2：、3：、4：、7：
            volVal.put("kt_op_type", map.get("USER_TYPE")); // 表中没有说明各个值分别表示什么？？？
            volVal.put("kt_project_id", map.get("PROJECT_ID"));
            //kt: 登录方式：0 均可，1 普通登录，2 CA登录
            //hx: 登录方式（1:均可用,2:账号登录,3:CA登录）
            Integer loginMode = ValChangeUtils.toIntegerIfNull(map.get("LOGIN_TYPE"), 0);
            if (loginMode == 2) {
                // ca登录
                volVal.put("loginmode", 3);
            } else if (loginMode == 1) {
                // 账号登录
                volVal.put("loginmode", 2);
            } else if (loginMode == 0){
                volVal.put("loginmode", 1);
            } else {
                // if null then 均可
                volVal.put("loginmode", 1);
                sb.append("迁移数据登录方式为空;");
            }
            volVal.put("link_tel", map.get("PHONE"));
            volVal.put("kt_is_activation", map.get("IS_ACTIVATION"));
            volVal.put("reasons_disable", map.get("REASONS_DISABLE"));
            volVal.put("kt_private_key", map.get("PRIVATE_KEY"));
            // 错误记录
            volVal.put("ts_notes", sb.toString());
            volVal.put("ts_deal_flag", 2);
            volVal.put("pwdhxupdate", 0); // 未在海西系统更改过密码
            datas.add(volVal);
        } tt.batchInsert(UAS_ORG_USER, datas);
        return ret.size();
    }

}
