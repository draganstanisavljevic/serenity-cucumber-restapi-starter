package com.orgname.qa.lib.properties;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertiesSupplier {
    private static Properties globalProperties;
    private static final ThreadLocal<Properties> THREAD_PROPERTIES = new ThreadLocal<>();

    public static String tryGetProperty(final String propertyName) {
        return get(propertyName);
    }

    private static synchronized Properties getGlobalProperties() {
        if (Objects.isNull(globalProperties)) {
            globalProperties = new Properties(System.getProperties());
        }
        return globalProperties;
    }

    private static Properties getThreadProperties() {
        if (Objects.isNull(THREAD_PROPERTIES.get())) {
            THREAD_PROPERTIES.set(new Properties());
        }
        return THREAD_PROPERTIES.get();
    }

    private static String get(final String key) {
        //Firstly look to the global properties shared between all threads
        String globalProperty = globalProperties.getProperty(key, null);
        if (Objects.nonNull(globalProperty)) {
            return globalProperty;
        }

        //Then look to the system properties (for current Thread)
        String systemProperty = System.getProperties().getProperty(key, null);
        if (Objects.nonNull(systemProperty)) {
            return systemProperty;
        }

        //Otherwise look to the Thread properties
        return getThreadProperties().getProperty(key);
    }

    public static void loadSystemProperties() {
        getGlobalProperties().putAll(System.getProperties());
    }

    public static void loadEnvProperties() {
        getGlobalProperties().putAll(System.getenv());
    }
}

