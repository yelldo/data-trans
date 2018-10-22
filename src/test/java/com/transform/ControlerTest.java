package com.transform;

import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.alibaba.fastjson.JSONObject;
import com.transform.config.TsMysqlTemplate;
import com.transform.jdbc.LowerCaseColNameRowMapper;
import com.transform.jdbc.Ops;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by tianhc on 2018/10/16.
 */
@RestController
@RequestMapping("ts/test")
public class ControlerTest {

    @Resource
    private TsMysqlTemplate tt;

    @RequestMapping("query")
    public List test1() {
        Ops ops = Ops.get().add("CATEGORYNO = '0010'").in("ORDIDX", new String[]{"1","2","3","4"});
        String query = SQLiteQueryBuilder.select("CVALUE").from("d_code").where(ops.str()).build();
        //List<Map<String, Object>> ret = tt.queryForMapList(query, ops.vals());
        List ret = tt.query(query, ops.vals(), new LowerCaseColNameRowMapper(JSONObject.class));
        return ret;
    }

    @RequestMapping("insert")
    public Long test2() {
        /*String sql = SQLiteQueryBuilder.insert()
                .into("insert_test")
                .columns("id", "name", "time")
                .values(1, "John", Calendar.getInstance())
                .build();
        System.out.println(sql);*/
        Map<String, Object> colVal = new HashMap<>();
        colVal.put("id", 2);
        colVal.put("name", "John");
        colVal.put("time", new Timestamp(System.currentTimeMillis()));
        return tt.insert("insert_test", colVal);
    }

}
