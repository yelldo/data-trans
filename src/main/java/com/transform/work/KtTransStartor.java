package com.transform.work;

import com.transform.work.kt.OrgMergeWork;
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
    private OrgMergeWork orgMergeWork;

    @Override
    public void run(String... strings) throws Exception {
        supAreaMergeWork.merge();
        orgMergeWork.merge();
    }
}
