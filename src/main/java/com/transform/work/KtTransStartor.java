package com.transform.work;

import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.transform.config.TsMysqlTemplate;
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
    private TsMysqlTemplate tsmt;

    @Override
    public void run(String... strings) throws Exception {
        String query = SQLiteQueryBuilder.select("ID","ACCOUNT").from("vm_all_user_info").build();
        System.out.println(query);
        //System.out.println(tsmt.query(query));
    }
}
