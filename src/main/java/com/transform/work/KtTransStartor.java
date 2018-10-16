package com.transform.work;

import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.transform.config.TsMysqlTemplate;
import com.transform.work.kt.SupAreaMergeWork;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Override
    public void run(String... strings) throws Exception {
        supAreaMergeWork.merge();
    }
}
