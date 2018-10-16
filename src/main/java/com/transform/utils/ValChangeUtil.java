package com.transform.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by tianhc on 2018/10/16.
 */
public class ValChangeUtil {

    public static Long toLong(Object object, Long defaultVal) {
        if (StringUtils.isBlank(object + "")) {
            return defaultVal;
        }
        return Long.valueOf(object + "");
    }
}
