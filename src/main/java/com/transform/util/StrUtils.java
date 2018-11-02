package com.transform.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        // 密码，采用md5加密=md5(md5(opno) + md5(密码))
        // 1b6117cae481dcd2cfe21aecb3185c55
        // 1b6117cae481dcd2cfe21aecb3185c55

        //String opno = DigestUtils.md5Hex("64758F8BFA7E11B2E05346681BAC26B7");
        //String realPwd = DigestUtils.md5Hex("asd123");
        //System.out.println(DigestUtils.md5Hex(opno+realPwd));

        //System.out.println(DigestUtils.md5Hex(DigestUtils.md5Hex("76AF3A5289D698DFE05346681BACE3ED") + DigestUtils.md5Hex("hx123456")));

        // 金额格式化
        BigDecimal d = new BigDecimal("666.5653").setScale(2, RoundingMode.HALF_UP);
        System.out.println(d.toString());
    }



}
