package com.transform.work;

import com.transform.work.kt.*;
import com.transform.work.test.SetPasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Component
public class KtConvertStartor implements CommandLineRunner {

    @Resource
    private SupAreaMergeWork supAreaMergeWork;
    @Resource
    private CompanyMergeWork companyMergeWork;
    @Resource
    private HospitalMergeWork hospitalMergeWork;
    @Resource
    private RegulatorMergeWork regulatorMergeWork;
    @Resource
    private HxOrgMergeWork hxOrgMergeWork;
    @Resource
    private CompanyHisMergeWork companyHisMergeWork;
    @Resource
    private CompanyAuditMergeWork companyAuditMergeWork;
    @Resource
    private OrgUserMergeWork orgUserMergeWork;
    @Resource
    private FileIdConvertWork fileIdConvertWork;
    @Resource
    private SetPasswordUtil setPasswordUtil;

    @Override
    public void run(String... strings) throws Exception {
        //测试
        //supAreaMergeWork.convert();

        //设置密码
        //setPasswordUtil.convert();

        /* 按顺序执行任务 */
        // kt/MCS_COMPANY_INFO -> hx/uas_org_info
        companyMergeWork.convert();
        // kt/MCS_HOSPITAL_INFO -> hx/uas_org_info
        hospitalMergeWork.convert();
        // kt/MCS_REGULATOR_INFO -> hx/uas_org_info
        regulatorMergeWork.convert();
        // hec_dup_fm_tender_org -> uas_org_info
        hxOrgMergeWork.convert();
        // 机构用户合并
        // kt : sys_n_users -> uas_org_user
        // hx : HEC_UPO_PRJ_USER -> uas_org_user
        orgUserMergeWork.convert();
        companyAuditMergeWork.convert();
        companyHisMergeWork.convert();
        fileIdConvertWork.convert();

    }
}
