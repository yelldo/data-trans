package com.transform.jdbc;


import java.util.*;

/**
 * where的查询条件，如 eq, noteq, like 等
 */
public class Ops {

    public static void main(String[] args) {
        System.out.println(Ops.get().eq("id", 2).like("name", "%luke").str());
    }

    private String prefix;

    protected Ops(String prefix) {
        this.prefix = prefix;
    }

    public static Ops get(String prefix) {
        return new Ops(prefix);
    }

    public static Ops get() {
        return new Ops("");
    }

    private String sqlstr = "";

    @SuppressWarnings("rawtypes")
    private List params = new ArrayList();

    /**
     * like
     */
    public Ops like(String name, Object param) {
        if (param == null || param.equals("")) return this;
        if (sqlstr.equals("")) {
            sqlstr = name + " like ?";
        } else {
            sqlstr = sqlstr + " and " + name + " like ?";
        }
        params.add("%" + param + "%");
        return this;
    }

    /**
     * =等于
     */
    public Ops eq(String name, Object param) {
        return eqopt(name, param, false);
    }

    public Ops mustEq(String name, Object param) {
        return eqopt(name, param, true);
    }

    private Ops eqopt(String name, Object param, boolean must) {
        if (!must) {
            if (param == null || param.equals("")) return this;
        }
        if (param != null && param.getClass().isArray()) {
            in(name, Arrays.asList(((Object[]) param)));
        } else if (param != null && (param instanceof Collection)) {
            in(name, (Collection) param);
        } else {
            if (param == null || param.equals("")) {
                if (sqlstr.equals("")) {
                    sqlstr = name + " is null";
                } else {
                    sqlstr = sqlstr + " and " + name + " is null";
                }
            } else {
                if (sqlstr.equals("")) {
                    sqlstr = name + " = ?";
                } else {
                    sqlstr = sqlstr + " and " + name + " = ?";
                }
                params.add(param);
            }
        }
        return this;
    }

    /**
     * <>
     *
     * @param name
     * @param param
     * @return
     */
    public Ops noteq(String name, Object param) {
        return noteqopt(name, param, false);
    }

    public Ops mustNoteq(String name, Object param) {
        return noteqopt(name, param, true);
    }

    private Ops noteqopt(String name, Object param, boolean must) {
        if (!must) {
            if (param == null || param.equals("")) return this;
        }
        if (param != null && param.getClass().isArray()) {
            notin(name, Arrays.asList(((Object[]) param)));
        } else if (param != null && (param instanceof Collection)) {
            notin(name, (Collection) param);
        } else {
            if (param == null || param.equals("")) {
                sqlstr = name + " is not null";
            } else {
                if (sqlstr.equals("")) {
                    sqlstr = name + " <> ?";
                } else {
                    sqlstr = sqlstr + " and " + name + " <> ?";
                }
                params.add(param);
            }
        }
        return this;
    }

    public Ops weq(String name, Object param) {
        return eq(name, param);
    }

    public Ops in(String name, Object[] params) {
        return in(name, Arrays.asList(params));
    }

    public Ops in(String name, Collection params) {
        if (params == null || params.isEmpty()) return this;
        List params2 = new Vector();
        String str = null;
        for (Object obj : params) {
            if (obj == null || obj.equals("")) continue;
            if (str == null) str = "?";
            else str = str + ",?";
            params2.add(obj);
        }
        if (str == null) return this;

        if (sqlstr.equals("")) {
            sqlstr = name + " in (" + str + ")";
        } else {
            sqlstr = sqlstr + " and " + name + " in (" + str + ")";
        }
        this.params.addAll(params2);
        return this;
    }

    public Ops notin(String name, Collection params) {
        if (params == null || params.isEmpty()) return this;
        List params2 = new Vector();
        String str = null;
        for (Object obj : params) {
            if (obj == null || obj.equals("")) continue;
            if (str == null) str = "?";
            else str = str + ",?";
            params2.add(obj);
        }
        if (str == null) return this;

        if (sqlstr.equals("")) {
            sqlstr = name + " not in (" + str + ")";
        } else {
            sqlstr = sqlstr + " and " + name + " not in (" + str + ")";
        }
        this.params.addAll(params2);
        return this;
    }

    public Ops win(String name, Collection params) {
        return in(name, params);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String str() {
        return sqlstr;
    }

    public String getWithPrefixSqlstr() {
        if (sqlstr == null || sqlstr.trim().equals("")) {
            return "";
        }
        if (prefix == null) return sqlstr;
        return prefix + " " + sqlstr;
    }

    public void setSqlstr(String sqlstr) {
        this.sqlstr = sqlstr;
    }

    public List getParams() {
        return params;
    }

    /**
     * 返回条件参数值
     * @return
     */
    public Object[] vals() {
        return params.toArray();
    }

    public Ops isNull(String name) {
        if (sqlstr.equals("")) {
            sqlstr = name + " is null";
        } else {
            sqlstr = sqlstr + " and " + name + " is null";
        }
        return this;
    }

    public Ops isNotNull(String name) {
        if (sqlstr.equals("")) {
            sqlstr = name + " is not null";
        } else {
            sqlstr = sqlstr + " and " + name + " is not null";
        }
        return this;
    }

    /**
     * >
     */
    public Ops gt(String name, Object param) {
        if (param == null || param.equals("")) return this;
        if (sqlstr.equals("")) {
            sqlstr = name + " > ?";
        } else {
            sqlstr = sqlstr + " and " + name + " > ?";
        }
        params.add(param);
        return this;
    }

    public Ops wgt(String name, Object param) {
        return ge(name, param);
    }

    /**
     * >=大于等于
     */
    public Ops ge(String name, Object param) {
        if (param == null || param.equals("")) return this;
        if (sqlstr.equals("")) {
            sqlstr = name + " >= ?";
        } else {
            sqlstr = sqlstr + " and " + name + " >= ?";
        }
        params.add(param);
        return this;
    }

    public Ops wge(String name, Object param) {
        return ge(name, param);
    }

    /**
     * 时间范围
     */
    public Ops addTimeRange(String name, SelDateRange dateRange) {
        if (dateRange == null) return this;
        return ge(name, dateRange.getStartTime()).lt(name, dateRange.getEndTime());
    }

    /**
     * <=小于等于
     */
    public Ops le(String name, Object param) {
        if (param instanceof Date) {
            return leDate(name, (Date) param);
        }
        if (param == null || param.equals("")) return this;
        if (sqlstr.equals("")) {
            sqlstr = name + " <= ?";
        } else {
            sqlstr = sqlstr + " and " + name + " <= ?";
        }
        params.add(param);
        return this;
    }

    public Ops wle(String name, Object param) {
        return le(name, param);
    }

    /**
     * <小于
     */
    public Ops lt(String name, Object param) {
        if (param instanceof Date) {
            return leDate(name, (Date) param);
        }
        if (param == null || param.equals("")) return this;
        if (sqlstr.equals("")) {
            sqlstr = name + " < ?";
        } else {
            sqlstr = sqlstr + " and " + name + " < ?";
        }
        params.add(param);
        return this;
    }

    /**
     * 小于等于日期
     *
     * @param name
     * @param param
     * @return
     */
    public Ops leDate(String name, Date param) {
        return leDateTime(name, param, true);
    }

    /**
     * 小于等于日期
     *
     * @param name
     * @param param
     * @return
     */
    public Ops leDateTime(String name, Date param) {
        return leDateTime(name, param, false);
    }

    /**
     * 小于等于日期
     *
     * @param name
     * @param param
     * @param ignoreTime
     * @return
     */
    private Ops leDateTime(String name, Date param, Boolean ignoreTime) {
        if (param == null) return this;
        if (sqlstr.equals("")) {
            sqlstr = name + " <= ?";
        } else {
            sqlstr = sqlstr + " and " + name + " <= ?";
        }
        int flag = 1;
        if (param instanceof java.sql.Date) {
            flag = 2;
        } else if (param instanceof java.sql.Timestamp) {
            flag = 3;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(param);
        if (ignoreTime) {
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
        }
        param = c.getTime();
        if (flag == 2) {
            param = new java.sql.Date(param.getTime());
        }
        if (flag == 3) {
            param = new java.sql.Timestamp(param.getTime());
        }
        params.add(param);
        return this;
    }

    /**
     * 列的值时间在参数时间之前或者小于参数时间
     *
     * @param name
     * @param param
     * @return
     */
    public Ops ltDate(String name, Object param) {
        if (param == null) return this;
        if (sqlstr.equals("")) {
            sqlstr = name + " < ?";
        } else {
            sqlstr = sqlstr + " and " + name + " < ?";
        }
        params.add(param);
        return this;
    }

    /**
     * 列的值时间在参数时间之前或者大于等于参数时间
     *
     * @param name
     * @param param
     * @return
     */
    public Ops geDate(String name, Date param) {
        if (param == null) return this;
        if (sqlstr.equals("")) {
            sqlstr = name + " >= ?";
        } else {
            sqlstr = sqlstr + " and " + name + " >= ?";
        }
        int flag = 1;
        if (param instanceof java.sql.Date) {
            flag = 2;
        } else if (param instanceof java.sql.Timestamp) {
            flag = 3;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(param);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        param = c.getTime();
        if (flag == 2) {
            param = new java.sql.Date(param.getTime());
        }
        if (flag == 3) {
            param = new java.sql.Timestamp(param.getTime());
        }
        params.add(param);
        return this;
    }

    public Ops wlt(String name, Object param) {
        return lt(name, param);
    }

    public Ops between(String name, Object termfrom, Object termto) {
        return this.ge(name, termfrom).lt(name, termto);
    }

    /**
     * 当param为空时也会添加 Where.get("and") .eq("a.objectId", objectId) .eq("a.type",
     * type) .addMust(
     * "a.objectId in (select b.id from ChargeObject b where b.customerId = ?)",
     * custid) .eq("a.state", 2) .eq("a.type", 1)
     *
     * @param sql
     * @param ps
     * @return
     */
    public Ops addMust(String sql, Object... ps) {
        if (sqlstr.equals("")) {
            sqlstr = sql;
        } else {
            sqlstr = sqlstr + " and (" + sql + ")";
        }
        for (Object param : ps) {
            params.add(param);
        }
        return this;
    }

    /**
     * 与addMust一致
     *
     * @param sql
     * @param param
     * @return
     */
    public Ops must(String sql, Object... param) {
        return addMust(sql, param);
    }

    /**
     * 当param为空时也会添加 Where.get("and") .eq("a.objectId", objectId) .eq("a.type",
     * type) .addMust(
     * "a.objectId in (select b.id from ChargeObject b where b.customerId = ?)",
     * custid) .eq("a.state", 2) .eq("a.type", 1)
     *
     * @param sql
     * @param param
     * @return
     */
    public Ops add(String sql, Object... param) {
        return addMust(sql, param);
    }

    /**
     * 当param为空时条件忽视，例如： Where.get("and") .eq("a.objectId", objectId)
     * .eq("a.type", type) .addOption(
     * "a.objectId in (select b.id from ChargeObject b where b.customerId = ?)",
     * custid) .eq("a.state", 2) .eq("a.type", 1)
     *
     * @param sql
     * @param param
     * @return
     */
    public Ops addOption(String sql, Object... param) {
        if (param == null || param.length <= 0 || (param.length == 1 && (param[0] == null || param[0].equals(""))))
            return this;
        // 所有元素为空时不添加
        boolean isnull = true;
        for (Object obj : param) {
            if (obj != null && !obj.equals("")) {
                isnull = false;
                break;
            }
        }
        if (isnull) return this;
        return addMust(sql, param);
    }

    /**
     * 同addOption
     *
     * @param sql
     * @param param
     * @return
     */
    public Ops option(String sql, Object... param) {
        return addOption(sql, param);
    }

    public Ops llike(String name, Object param) {
        if (param == null || param.equals("")) return this;
        if (sqlstr.equals("")) {
            sqlstr = name + " like ?";
        } else {
            sqlstr = sqlstr + " and " + name + " like ?";
        }
        params.add("%" + param);
        return this;
    }

    public Ops rlike(String name, Object param) {
        if (param == null || param.equals("")) return this;
        if (sqlstr.equals("")) {
            sqlstr = name + " like ?";
        } else {
            sqlstr = sqlstr + " and " + name + " like ?";
        }
        params.add(param + "%");
        return this;
    }

    @Override
    public Ops clone() {
        Ops w = Ops.get(this.prefix);
        w.params = this.params;
        w.sqlstr = this.sqlstr;
        return w;
    }

    /**
     * @param name
     * @param range 例如：1或1,2或3-3
     * @return
     */
    public Ops betweenInt(String name, String range) {
        if (range == null || range.equals("")) return this;
        String[] strs = range.split("[,\\-]");
        Integer min = null;
        Integer max = null;
        min = Integer.valueOf(strs[0]);
        if (strs.length > 1) {
            max = Integer.valueOf(strs[1]);
        }
        return this.ge(name, min).le(name, max);
    }

}
