package com.transform.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tianhc on 2018/10/17.
 */
public class StrUtils {

    public static boolean isBlankOrNullVal(Object obj) {
        if (obj == null) {
            return true;
        }
        String str = obj + "";
        if (StringUtils.isBlank(str) || str.toLowerCase().contains("null")) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date(1540454373)));

    }
}
