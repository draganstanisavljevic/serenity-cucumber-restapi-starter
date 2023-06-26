package com.orgname.qa.lib.properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertiesLoader {

    public static void loadSystemProperties() {
        PropertiesSupplier.loadSystemProperties();
    }

    public static void loadEnvProperties() {
        PropertiesSupplier.loadEnvProperties();
    }
}
