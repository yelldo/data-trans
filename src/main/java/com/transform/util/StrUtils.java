package com.transform.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static void diffInCollection(String jobName,String[] str1, String[] str2) {
        System.out.println(jobName + "!!!!!!!!!!!!-------------------!!!!!!!!!!!!!");
        List<String> list1 = CollectionUtils.arrayToList(str1);
        List<String> list2 = CollectionUtils.arrayToList(str2);
        System.out.println("集合一 不存在与 集合二 中的元素===========");
        for (String s : str1) {
            if (list2.contains(s)) {
                continue;
            } else {
                System.out.println(s);
            }
        }

        System.out.println("集合二 不存在与 集合一 中的元素===========");
        for (String s : str2) {
            if (list1.contains(s)) {
                continue;
            } else {
                System.out.println(s);
            }
        }
    }

    public static void main(String[] args) {
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //System.out.println(sdf.format(new Date(1540454373)));
    }



}
