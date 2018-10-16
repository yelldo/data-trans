package com.transform.config;

import com.alibaba.fastjson.JSONArray;
import com.transform.exception.TsException;
import com.transform.jdbc.ResultRowExtractor;
import com.transform.utils.GlobalUtil;
import com.transform.jdbc.PageData;
import groovy.lang.Closure;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.persistence.Table;
import java.sql.*;
import java.util.*;

/**
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Component
public class TsMysqlTemplate {

    private final JdbcTemplate jt;

    @Autowired
    public TsMysqlTemplate(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }

    public void printMine() {
        System.out.println(jt);
    }

    /**
     * 执行DDL操作(Data Manipulation Language)
     *
     * @param optSql
     */
    public void adminSql(String optSql) {
        jt.execute(optSql);
    }

    public <T> T adminSql(ConnectionCallback<T> callback) {
        return jt.execute(callback);
    }

    public JSONArray filter(String table, Map<String, Object> fv) {
        return convertToArray(limitNoCache(table, fv));
    }

    private List<Object> limitNoCache(Object... args) {
        return dofilter(2, args);
    }

    private <T> T dofilter(int type, Object[] args) {
        String table = null;
        Object param = null;
        Closure<Object> closure = null;
        if (args == null || args.length < 2) {
            throw new TsException("参数不正确");
        }
        if (args[0] instanceof LinkedHashMap) {
            param = args[0];
            table = (String) args[1];
        } else if (args[0] instanceof String) {
            table = (String) args[0];
            param = args[1];
        }
        if (args.length > 2) {
            if (args[2] instanceof Closure) {
                closure = (Closure<Object>) args[2];
            } else {
                throw new TsException("参数不正确，第3个参数只能是闭包");
            }
        }
        ResultRowExtractor<Object> extractor = null;
        if (closure != null) {
            final Closure<Object> c = closure;
            extractor = new ResultRowExtractor<Object>() {
                @Override
                public Object processRow(ResultSet rs, int index) throws SQLException {
                    return c.call(rs, index);
                }
            };
        }
        Object obj = filterTable(table, param, false, type, extractor);
        return (T) obj;
    }

    /*public JSONArray filter(String table, Map<String, Object> fv, int start, int limit) {
        if(fv == null) {
            fv = new HashMap<>();
        }
        fv.put("limit", limit);
        fv.put("start", start);
        return filter(table, fv);
    }*/
    public PageData filterPage(String table, Map<String, Object> fv, int page, int pageSize) {
        if (fv == null) {
            fv = new HashMap<>();
        }
        fv.put("page", page);
        fv.put("pagesize", pageSize);
        return pageNoCache(table, fv);
    }

    private PageData pageNoCache(Object... args) {
        return dofilter(3, args);
    }

    public JSONArray query(String sql, Object[] params, RowMapper<Object> rs) {
        if (rs != null) {
            return convertToArray(query(sql, params, new RowMapperResultSetExtractor<Object>(rs)));
        } else {
            return convertToArray(find(sql, params, null));
        }
    }

    public List query(String sql) {
        return jt.queryForList(sql);
    }

    public Object queryFirst(String sql) {
        return jt.queryForList(sql).get(0);
    }

    public <T> T query(String sql, Object[] args, ResultSetExtractor<T> resultSetExtractor) {
        log.debug("执行SQL:{},{}", sql, args != null ? Arrays.asList(args) : null);
        return (T) jt.query(sql, args, resultSetExtractor);
    }

    public List<Object> find(String sql, Object[] params, Closure<Object> closure) {
        List ls = queryForMapList(sql, params);
        if (closure != null) {
            return convert(ls, closure);
        }
        return ls;
    }

    private List<Object> convert(List<Map<String, Object>> ls, Closure<Object> closure) {
        List<Object> rls = new ArrayList<>();
        int i = 0;
        for (Map<String, Object> rec : ls) {
            Object robj = closure.call(rec, i++);
            if (robj != null) {
                rls.add(robj);
            }
        }
        return rls;
    }

    /*public JSONArray query(String sql, Object[] params, RowMapper<Object> rs, int start, int limit) {
        sql = sv.convertSqlRealTable(sql);
        if(start < 0) {
            start = 0;
        }
        if(limit < 0) {
            limit = 20;
        }
        sql = sql + " limit " + start + "," + limit;
        return query(sql, params, rs);
    }*/
    /*public PageData queryPage(String sql, Object[] params, RowMapper<Object> rs, int page, int pageSize) {
        sql = sv.convertSqlRealTable(sql);
        return sv.getJt().findPage(sql, page, pageSize, params, rs);
    }*/

    public int update(String sql, Object... args) {
        return jt.update(sql, args);
    }

    public int update(PreparedStatementCreator creator, KeyHolder holder) {
        return jt.update(creator, holder);
    }

    public Long insert(String table, Map<String, Object> data) {
        KeyHolder holder = new GeneratedKeyHolder();
        String sql = "insert into " + table;
        String cstr = null;
        String qstr = null;
        List<Object> ls = new ArrayList<>();
        for (String key : data.keySet()) {
            if (key.startsWith("_")) {
                continue;
            }
            cstr = cstr == null ? key : (cstr + "," + key);
            qstr = (qstr == null) ? "?" : (qstr + ",?");
            ls.add(data.get(key));
        }
        sql = sql + "(" + cstr + ") values(" + qstr + ")";
        final String cursql = sql;
        log.debug("执行SQL:{},params:{}", sql, ls);
        jt.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(cursql, Statement.RETURN_GENERATED_KEYS);
                for (int i = 0; i < ls.size(); i++) {
                    ps.setObject(i + 1, ls.get(i));
                }
                return ps;
            }
        }, holder);
        if (holder.getKey() != null) {
            return holder.getKey().longValue();
        }
        return null;
    }

    public List<Long> batchInsert(String table, List<Map<String, Object>> datas) {
        String sql = "insert into " + table;
        String cstr = null;
        String qstr = null;
        List<String> names = new ArrayList<>();
        for (Map<String, Object> rec : datas) {
            for (String key : rec.keySet()) {
                if (!names.contains(key)) {
                    names.add(key);
                }
            }
        }
        for (String key : names) {
            cstr = cstr == null ? key : (cstr + "," + key);
            qstr = (qstr == null) ? "?" : (qstr + ",?");
        }
        sql = sql + "(" + cstr + ") values(" + qstr + ")";
        final String cursql = sql;
        log.debug("执行SQL:{}", sql);
        return jt.execute(new ConnectionCallback<List<Long>>() {
            @Override
            public List<Long> doInConnection(Connection con) throws SQLException, DataAccessException {
                PreparedStatement pstmt = con.prepareStatement(cursql, new String[]{"id"});
                int colnum = names.size();
                for (Map<String, Object> rec : datas) {
                    for (int i = 0; i < colnum; i++) {
                        pstmt.setObject(i + 1, rec.get(names.get(i)));
                    }
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                ResultSet rs = pstmt.getGeneratedKeys();
                List<Long> rls = new ArrayList<>();
                while (rs.next()) {
                    rls.add(rs.getLong(1));
                }
                return rls;
            }
        });
    }

    private JSONArray convertToArray(Object obj) {
        if (obj instanceof JSONArray) {
            return (JSONArray) obj;
        } else if (obj instanceof List) {
            return new JSONArray((List) obj);
        } else if (obj instanceof Collection) {
            JSONArray ls = new JSONArray();
            ls.addAll((Collection) obj);
            return ls;
        } else {
            throw new TsException(obj + "不是集合对象");
        }
    }

    private Object filterTable(String curtable, Object param, boolean cache, int type, ResultRowExtractor<Object> extractor) {
        Map<String, Object> params = null;
        //如果redis为空不考虑缓存
        /*if(redis == null && cache) {
            cache = false;
        }*/
        //如果在事务中也不考虑缓存
        /*if(cache) {
            boolean flag = ThreadContext.get().containsKey(ThreadContext.IN_TRANSACTION_HINT);
            if(flag) {
                cache = false;
            }
        }*/

        //考虑分页支持
        int page = 0;
        int start = 0;
        int limit = 0;
        boolean idflag = false;
        if (param instanceof Map) {
            params = (Map<String, Object>) param;
            if (type == 2 || type == 1) {
                //limit
                start = GlobalUtil.converToIntValue(params.get("start"), 0);
                start = start < 0 ? 0 : start;
                limit = GlobalUtil.converToIntValue(params.get("limit"), 20);
                limit = limit < 1 ? 20 : limit;
                params.remove("limit");
                params.remove("start");
            } else if (type == 3) {
                //分页
                page = GlobalUtil.converToIntValue(params.get("page"), 1);
                page = page < 1 ? 1 : page;
                limit = GlobalUtil.converToIntValue(params.get("pagesize"), GlobalUtil.converToIntValue(params.get("limit"), 20));
                limit = limit < 1 ? 20 : limit;
                start = (page - 1) * limit;
                params.remove("page");
                params.remove("pagesize");
                params.remove("limit");
            }
        } else {
            params = new LinkedHashMap<>();
            params.put("id", param);
        }
        if (params.containsKey("id")) {
            idflag = true;
        }
        //形成查询语句与缓存key
        String wstr = null;
        //获取真实表名
        String realtable = curtable;
        //建立刷新的key
        List<String> names = new ArrayList<>();
        names.addAll(params.keySet());
        Collections.sort(names);
        List<Object> ls = new ArrayList<>();
        for (String key : names) {
            Object vobj = params.get(key);
            String istr = null;
            if (GlobalUtil.isEmpty(vobj)) {
                istr = key + " is null";
            } else {
                istr = key + "=?";
                ls.add(params.get(key));
            }
            wstr = (wstr == null) ? istr : (wstr + " and " + istr);
        }
        Object result = null;
        String sql = "select * from " + realtable + (StringUtils.isNotBlank(wstr) ? (" where " + wstr) : "");
        //先注册刷新key
        if (type == 0) {
            //单个对象
            Map<String, Object> rec = jt.queryForMap(sql, ls.toArray());
            if (cache && rec != null) {
                Map<String, Object> cacherec = null;
                if (!idflag) {
                    cacherec = new HashMap<>();
                    cacherec.put("id", rec.get("id"));
                }
            }
            result = rec;
        } else if (type == 1 || type == 2 || type == 3) {
            List<Object> datals = null;
            if (type == 1) {
                //datals = jt.queryForMapList(sql, ls.toArray());
                datals = queryForList(sql, ls.toArray(), extractor);
                result = datals;
            } else if (type == 2 || type == 3) {
                //datals = jt.queryForMapList(sql + " limit " + start + "," + limit, ls.toArray());
                datals = queryForList(sql + " limit " + start + "," + limit, ls.toArray(), extractor);
                if (type == 3) {
                    int total = jt.queryForObject("select count(*) from (" + sql + ") dt", ls.toArray(), Integer.class);
                    result = new PageData(datals, page, limit, total);
                } else {
                    result = datals;
                }
            }
        } else {
            throw new TsException("不支持查询类型" + type);
        }
        return result;
    }

    public <T> List<T> queryForList(String sql, Object[] args, ResultRowExtractor<T> extractor) {
        log.debug("执行SQL:{},{}", sql, args != null ? Arrays.asList(args) : null);
        if (extractor == null) {
            return (List<T>) queryForMapList(sql, args);
        }
        final List<T> ls = new ArrayList<>();
        jt.query(sql, args, new RowCallbackHandler() {
            int rownum = 0;

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                T val = extractor.processRow(rs, rownum++);
                if (val != null) {
                    ls.add(val);
                }
            }
        });
        return ls;
    }

    public List<Map<String, Object>> queryForMapList(String sql, Object[] args) {
        log.debug("执行SQL:{},{}", sql, args != null ? Arrays.asList(args) : null);
        return jt.queryForList(sql, args);
    }

    /**
     * 获取表名
     *
     * @param entityClass
     * @return
     */
    public String getTableName(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        if (table == null) {
            throw new TsException(entityClass.getName() + "未添加@Table注解");
        }
        return table.name();
    }

}
