package com.transform.work;

import com.transform.work.kt.CompanyMergeWork;
import com.transform.work.kt.HospitalMergeWork;
import com.transform.work.kt.RegulatorMergeWork;
import com.transform.work.kt.SupAreaMergeWork;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Component
public class KtTransStartor implements CommandLineRunner {

    @Resource
    private SupAreaMergeWork supAreaMergeWork;
    @Resource
    private CompanyMergeWork companyMergeWork;
    @Resource
    private HospitalMergeWork hospitalMergeWork;
    @Resource
    private RegulatorMergeWork regulatorMergeWork;

    @Override
    public void run(String... strings) throws Exception {
        //supAreaMergeWork.merge();
        //companyMergeWork.merge();
        hospitalMergeWork.merge();
        regulatorMergeWork.merge();
    }
}
