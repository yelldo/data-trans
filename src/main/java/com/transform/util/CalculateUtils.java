package com.transform.util;

import java.text.DecimalFormat;

/**
 * 数字计算工具
 * Created by tianhc on 2018/10/17.
 */
public class CalculateUtils {
    public static String percentage(long a, long b) {
        double a1 = a * 1.0;
        double b1 = b * 1.0;
        DecimalFormat df1 = new DecimalFormat("##.00%");
        return df1.format(a1/b1);
    }
}
