package com.hirisun.cloud.common.util;


import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.springframework.core.env.AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME;

public class EnvironmentUtils {

    public static final String PRO = "pro";
    public static final String DEV = "dev";
    public static final String TEST = "test";

    /**
     * 线上环境
     */
    public static final List<String> ONLINE_PROFILES = Arrays.asList(PRO);

    /**
     * 获取当前环境
     */
    public static String getCurrentProfile(ApplicationContext app) {
        return app.getEnvironment().getProperty(ACTIVE_PROFILES_PROPERTY_NAME);
    }


}
