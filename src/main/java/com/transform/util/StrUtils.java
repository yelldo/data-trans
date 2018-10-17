package com.transform.util;

import org.apache.commons.lang3.StringUtils;

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
}
