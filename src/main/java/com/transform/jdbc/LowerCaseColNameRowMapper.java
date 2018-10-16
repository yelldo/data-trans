package com.transform.jdbc;

import com.alibaba.fastjson.JSONObject;
import com.transform.exception.TsException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianhc on 2018/10/16.
 */
public class LowerCaseColNameRowMapper implements RowMapper{

    private Class target;

    private LowerCaseColNameRowMapper() {

    }

    public LowerCaseColNameRowMapper(Class target) {
        if (target == null) {
            throw new TsException("必须指定结果集元素类型");
        }
        this.target = target;
    }

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        if (target == JSONObject.class) {
            JSONObject j = new JSONObject();
            ResultSetMetaData rsd = resultSet.getMetaData();
            for (int k = 1; k<=rsd.getColumnCount(); k++) {
                j.put(rsd.getColumnName(k).toLowerCase(), resultSet.getObject(k));
            }
            return j;
        }
        if (target == List.class) {
            List l = new ArrayList();
            ResultSetMetaData rsd = resultSet.getMetaData();
            for (int k = 1; k<=rsd.getColumnCount(); k++) {
                l.add(resultSet.getObject(k));
            }
            return l;
        }
        return null;
    }
}
