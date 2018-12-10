package com.transform.util;

import com.transform.config.TsMysqlTemplate;
import com.transform.exception.TsException;
import com.transform.jdbc.SQL;
import com.transform.work.AbstractWorker;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by tianhc on 2018/10/22.
 */
@Component
public class ServiceCodeGenerator {

    /**
     * 生成企业编码
     *
     * @param orgType
     * @param orgId
     * @return
     */
    public static String generateOrgCode(Integer orgType, Long orgId) {
        if (orgType == null || orgId == null) {
            throw new TsException("参数不能为空");
        }
        // 生成机构编码（1000001，总共8位，机构类型+补零+机构id）
        StringBuffer orgCode = new StringBuffer(orgType + "");
        int size = (orgId + "").length();
        for (int i = 0; i < 7 - size; i++) {
            orgCode.append("0");
        }
        return orgCode.append(orgId).toString();
    }

    /**
     * 转换地区code
     * 地区字段转成 code（如：locate_area = 330000,330100,330103）
     *
     * @param regCode 凯特地区代码
     * @param tt
     * @return
     */
    public static String generateLocateAreaCode(String regCode, TsMysqlTemplate tt) {
        // 所在地区 locate_area
        if (!StrUtils.isBlankOrNullVal(regCode)) {
            String sql = SQL//
                    .select("a.id as id", "a.parent_id as pid", "b.parent_id as pid2")//
                    .from(AbstractWorker.UAS_BASE_AREA + " a")//
                    .leftOuterJoin(AbstractWorker.UAS_BASE_AREA + " b")//
                    .on("a.parent_id = b.id")//
                    .where("a.id = ?")//
                    .build();
            Map<String, Object> area = tt.queryFirst(sql, regCode);
            if (area == null) {
                Map<String, Object> dcode = tt.queryFirst(SQL.select("CVALUE").from(AbstractWorker.D_CODE).where("CNO = ?").build(), regCode);
                if (dcode == null) {
                    return null;
                }
            } else {
                String a = area.get("id") == null ? "" : area.get("id") + "";
                String b = area.get("pid") == null ? "" : area.get("pid") + ",";
                String c = area.get("pid2") == null ? "" : area.get("pid2") + ",";
                return c + b + a;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String sql = SQL.select("a.id as id", "a.parent_id as pid", "b.parent_id as pid2")//
                .from(AbstractWorker.UAS_BASE_AREA + " a," + AbstractWorker.UAS_BASE_AREA + " b")//
                .where("a.parent_id = b.id and a.id = ?").build();
        String sql2 = SQL//
                .select("a.id as id", "a.parent_id as pid", "b.parent_id as pid2")//
                .from(AbstractWorker.UAS_BASE_AREA + " a")//
                .leftOuterJoin(AbstractWorker.UAS_BASE_AREA + " b")//
                .on("a.parent_id = b.id")//
                .where("a.id = ?")//
                .build();
        System.out.println(sql);
        System.out.println(sql2);
    }


}
