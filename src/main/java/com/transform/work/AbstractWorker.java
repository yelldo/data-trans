package com.transform.work;

import com.transform.config.TsMysqlTemplate;

import javax.annotation.Resource;

/**
 * Created by tianhc on 2018/10/16.
 */
public abstract class AbstractWorker {

    @Resource
    protected TsMysqlTemplate tt;

    public static final int LIMIT = 1;
    public static final String MCS_COMPANY_INFO = "mcs_company_info";
    public static final String MCS_HOSPITAL_INFO = "mcs_hospital_info";
    public static final String MCS_REGULATOR_INFO = "mcs_regulator_info";
    public static final String MCS_COMPANY_INFO_DO = "mcs_company_info_do";
    public static final String MCS_COMPANY_INFO_DO_HIS = "mcs_company_info_do_his";
    public static final String MCS_ORGAN_AUDIT = "mcs_organ_audit";
    public static final String MCS_ATTACH_FILE = "mcs_attachfile";
    public static final String HEC_UPO_PRJ_USER = "hec_upo_prj_user";
    public static final String AUTH_USER_CERT = "auth_user_cert";
    public static final String HEC_DUP_FM_TENDER_ORG = "hec_dup_fm_tender_org";
    public static final String SYS_N_USERS = "sys_n_users";
    public static final String UAS_ORG_INFO = "uas_org_info";
    //public static final String UAS_ORG_INFO = "uas_org_info_tmp";
    public static final String UAS_ORG_USER = "uas_org_user";
    public static final String UAS_ORG_INFO_MODIFY_APPLY = "uas_org_info_modify_apply";
    public static final String UAS_ORG_INFO_MODIFY_APPLY_HIS = "uas_org_info_modify_apply_his";
    public static final String D_CODE = "d_code";
    public static final String UAS_BASE_AREA = "uas_base_area";
    public static final String SUPERVISE_AREA_TMP = "uas_supervise_area_tmp";
    public static final String UAS_SUPERVISE_AREA = "uas_supervise_area";
    public static final String TS_FILEID_CONVERT = "ts_fileid_convert";
}
