package com.transform.work;

import com.transform.config.TsMysqlTemplate;

import javax.annotation.Resource;

/**
 * Created by tianhc on 2018/10/16.
 */
public abstract class AbstractWorker {

    @Resource
    protected TsMysqlTemplate tt;

    protected static final int LIMIT = 1;
    protected static final String MCS_COMPANY_INFO = "mcs_company_info";
    protected static final String MCS_HOSPITAL_INFO = "mcs_hospital_info";
    protected static final String MCS_REGULATOR_INFO = "mcs_regulator_info";
    protected static final String MCS_COMPANY_INFO_DO = "mcs_company_info_do";
    protected static final String MCS_COMPANY_INFO_DO_HIS = "mcs_company_info_do_his";
    protected static final String MCS_ORGAN_AUDIT = "mcs_organ_audit";
    protected static final String MCS_ATTACH_FILE = "mcs_attachfile";
    protected static final String HEC_UPO_PRJ_USER = "hec_upo_prj_user";
    protected static final String HEC_DUP_FM_TENDER_ORG = "hec_dup_fm_tender_org";
    protected static final String SYS_N_USERS = "sys_n_users";
    protected static final String UAS_ORG_INFO = "uas_org_info";
    //protected static final String UAS_ORG_INFO = "uas_org_info_tmp";
    protected static final String UAS_ORG_USER = "uas_org_user";
    protected static final String UAS_ORG_INFO_MODIFY_APPLY = "uas_org_info_modify_apply";
    protected static final String UAS_ORG_INFO_MODIFY_APPLY_HIS = "uas_org_info_modify_apply_his";
    protected static final String D_CODE = "d_code";
    protected static final String UAS_BASE_AREA = "uas_base_area";
    protected static final String SUPERVISE_AREA_TMP = "uas_supervise_area_tmp";
    protected static final String UAS_SUPERVISE_AREA = "uas_supervise_area";
}
