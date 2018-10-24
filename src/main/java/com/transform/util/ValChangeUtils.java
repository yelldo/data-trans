package com.transform.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by tianhc on 2018/10/16.
 */
public class ValChangeUtils {

    public static Long toLong(Object object, Long defaultVal) {
        if (object == null) {
            return defaultVal;
        }
        if (StringUtils.isBlank(object + "")) {
            return defaultVal;
        }
        return Long.valueOf(object + "");
    }

    public static Integer toIntegerIfNull(Object object, Integer defaultVal) {
        if (object == null) {
            return defaultVal;
        }
        if (StringUtils.isBlank(object + "")) {
            return defaultVal;
        }
        return Integer.valueOf(object + "");
    }
}
