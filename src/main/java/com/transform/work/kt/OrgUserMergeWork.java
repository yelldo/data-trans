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
 * 机构用户合并
 * kt : sys_n_users -> uas_org_user
 * hx : HEC_UPO_PRJ_USER -> uas_org_user
 * <p>
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Service
public class OrgUserMergeWork extends AbstractWorker implements Converter {

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
        Object obj = tt.queryFirst(SQL.select("count(1)").from(HEC_UPO_PRJ_USER).build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, null);
        int offset = 0;
        int limit = LIMIT;
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
        String sql = SQL.select("*").from(SYS_N_USERS).limit(limit).offset(offset).build();
        List<Map<String, Object>> ret = tt.queryForMapList(sql);
        List<Map<String, Object>> datas = new ArrayList<>();
        // 记录迁移过程的数据错误
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : ret) {
            // 清空内容，复用
            sb.delete(0, sb.length());
            Map<String, Object> volVal = new HashMap<>();
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
            if ("0".equals(map.get("ENABLED"))) {
                volVal.put("status", 2);
            } else if ("1".equals(map.get("ENABLED"))) {
                volVal.put("status", 1);
            } else {
                // 如果不是1或2，则标注为不可用
                volVal.put("status", 2);
            }
            volVal.put("notes", map.get("REMARK"));
            volVal.put("kt_secrecy", map.get("SECRECY"));
            volVal.put("kt_op_type", map.get("OPTYPE")); // 表中没有说明各个值分别表示什么？？？
            volVal.put("loginmode", 1); // kt迁移过来的用户都标记为1均可的登录方式
            volVal.put("username", map.get("OPACCOUNT"));
            volVal.put("general_name", map.get("OPACCOUNT2"));
            volVal.put("email", map.get("EMAIL"));
            volVal.put("modify_time", map.get("LASTUPDATE"));
            volVal.put("kt_data_network", (map.get("DATA_NETWORK") + "").substring(0, 1));
            //volVal.put("primary_account", map.get("ISMAIN")); //????????? TODO
            // 关联机构id
            Map<String, Object> hxOrgId = tt.queryFirst(SQL.select("id","code").from(UAS_ORG_INFO).where("kt_org_id = ?").build(), map.get("ORGANID"));
            if (hxOrgId != null) {
                volVal.put("org_info_id", ValChangeUtils.toLong(hxOrgId.get("id"), null));
            } else {
                //
                continue;
            }
            volVal.put("kt_unitid_id", map.get("UNITID")); //UNITID和ORGANID什么区别
            volVal.put("link_man", map.get("LINKMAIN"));
            volVal.put("link_tel", map.get("LINKMAIN"));
            volVal.put("ca_cert", map.get("CERNO"));
            volVal.put("kt_is_business", map.get("ISBUSINESS"));
            // 错误记录
            volVal.put("ts_notes", sb.toString());
            volVal.put("ts_deal_flag", 1);
            datas.add(volVal);
        }
        tt.batchInsert(UAS_ORG_USER, datas);
        return ret.size();
    }

    private int hxUser(int offset, int limit) {
        String sql = SQL.select("*").from(SYS_N_USERS).limit(limit).offset(offset).build();
        List<Map<String, Object>> ret = tt.queryForMapList(sql);
        List<Map<String, Object>> datas = new ArrayList<>();
        // 记录迁移过程的数据错误
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : ret) {
            // 清空内容，复用
            sb.delete(0, sb.length());
            Map<String, Object> volVal = new HashMap<>();
            volVal.put("kt_opno", map.get("ID"));
            volVal.put("realname", map.get("USER_NAME"));
            volVal.put("userpwd", map.get("PASSWORD"));
            volVal.put("username", map.get("ACCOUNT"));
            // 关联机构id
            Map<String, Object> hxOrgId = tt.queryFirst(SQL.select("id","code").from(UAS_ORG_INFO).where("kt_org_id = ?").build(), map.get("ORG_ID"));
            if (hxOrgId != null) {
                volVal.put("org_info_id", ValChangeUtils.toLong(hxOrgId.get("id"), null));
            } else {
                //
                continue;
            }
            volVal.put("email", map.get("EMAIL"));
            //状态（1:启用,2:禁用,3:删除）
            if ("0".equals(map.get("ENABLED"))) {
                volVal.put("status", 2);
            } else if ("1".equals(map.get("ENABLED"))) {
                volVal.put("status", 1);
            } else if ("3".equals(map.get("ENABLED"))) {
                continue;
            } else {
                // 其他状态标注为不可用
                volVal.put("status", 2);
            }
            volVal.put("create_time", map.get("CREATE_TIME"));
            volVal.put("modify_time", map.get("MODIFY_TIME"));
            volVal.put("latest_login_time", map.get("LAST_LOGIN_TIME"));
            volVal.put("last_login_ip", map.get("LAST_LOGIN_IP"));
            volVal.put("kt_op_type", map.get("USER_TYPE")); // 表中没有说明各个值分别表示什么？？？
            volVal.put("kt_project_id", map.get("PROJECT_ID"));
            int[] lmStatus = {1,2,3};
            volVal.put("loginmode", lmStatus[ValChangeUtils.toIntegerIfNull(map.get("LOGIN_TYPE"),0)]); // kt迁移过来的用户都标记为1均可的登录方式
            volVal.put("general_name", map.get("OPACCOUNT2"));
            int[] paStatus = {2,1,3};
            volVal.put("primary_account", paStatus[ValChangeUtils.toIntegerIfNull(map.get("IS_ADMIN"),null)]); //????????? TODO
            volVal.put("link_tel", map.get("PHONE"));
            volVal.put("kt_is_activation", map.get("IS_ACTIVATION"));
            // 错误记录
            volVal.put("ts_notes", sb.toString());
            volVal.put("ts_deal_flag", 1);
            datas.add(volVal);
        }
        tt.batchInsert(UAS_ORG_USER, datas);
        return ret.size();
    }

}