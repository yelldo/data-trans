package com.transform.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static void main(String[] args) throws ParseException {
        //2018-10-30 10:02:45
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse("2018-10-30 10:02:46");
        System.out.println(date.getTime());
    }
}
