package com.hirisun.cloud.common.util;

public class StringBooleanCheck {

    public static boolean check(String str) {
        return "0".equals(str) || "1".equals(str);
    }

}
