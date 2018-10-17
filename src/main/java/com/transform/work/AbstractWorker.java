package com.transform.work;

import com.transform.config.TsMysqlTemplate;

import javax.annotation.Resource;

/**
 * Created by tianhc on 2018/10/16.
 */
public abstract class AbstractWorker {

    @Resource
    protected TsMysqlTemplate tt;

    protected static final String MCS_COMPANY_INFO = "mcs_company_info";
    protected static final String MCS_HOSPITAL_INFO = "mcs_hospital_info";
    protected static final String MCS_REGULATOR_INFO = "mcs_regulator_info";
    protected static final String UAS_ORG_INFO = "uas_org_info_tmp";
    protected static final String D_CODE = "d_code";
    protected static final String UAS_BASE_AREA = "uas_base_area";
    protected static final String SUPERVISE_AREA_TMP = "uas_supervise_area_tmp";
    protected static final String UAS_SUPERVISE_AREA = "uas_supervise_area";
}
