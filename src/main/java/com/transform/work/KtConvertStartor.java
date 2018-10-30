package com.transform.work;

import com.transform.work.kt.*;
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

    @Override
    public void run(String... strings) throws Exception {
        //测试
        //supAreaMergeWork.convert();

        // 按顺序执行任务
        //companyMergeWork.convert();
        //hospitalMergeWork.convert();
        //regulatorMergeWork.convert();
        //hxOrgMergeWork.convert();
        //companyHisMergeWork.convert();
        //companyAuditMergeWork.convert();
        //orgUserMergeWork.convert();
        fileIdConvertWork.convert();

    }
}
