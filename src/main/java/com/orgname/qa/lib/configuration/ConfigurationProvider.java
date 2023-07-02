package com.orgname.qa.lib.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orgname.qa.lib.helpers.JsonHelper;
import com.orgname.qa.lib.models.auth.TokenPayload;

import java.util.Map;

public class ConfigurationProvider {

    private static Map<String, Map<String, String>> testsConfiguration;
    private static Map<String, TokenPayload> auth0ClientConfiguration;
    private static final String ENV = System.getProperty("environment");


    /**
     *function loads tests configuration using configuration path and environment
     */
    public static void loadConfiguration() {
        TypeReference<Map<String, Map<String, String>>> typeRefEnvironment = new TypeReference<>() {
        };

        String configPath = (ENV == null || ENV.startsWith("http") || ENV.equals("test"))
                ? "config/config.json" : String.format("config/config.%s.json", ENV);

        testsConfiguration = JsonHelper.readItemFromJsonFile(configPath, typeRefEnvironment);
    }

    /**
     * function return test configuration
     * @return Map<String, Map<String, String>>
     */
    public static Map<String, Map<String, String>> getEnvironmentConfiguration() {
        return testsConfiguration;
    }

    /**
     * function loads tests configuration for Azure using configuration path and environment
     */
    public static void loadAzureConfiguration() {
        TypeReference<Map<String, Map<String, String>>> typeRefEnvironment = new TypeReference<>() {
        };

        String azureClientConfigPath = (ENV == null || ENV.startsWith("http") || ENV.equals("test"))
                ? "config/azure_clients.json" : String.format("config/azure_clients.%s.json", ENV);

        testsConfiguration = JsonHelper.readItemFromJsonFile(azureClientConfigPath, typeRefEnvironment);
    }

    /**
     * gets Auth0 client configuration using configuration path and type reference Auth0 client configuration
     */
    public static Map<String, TokenPayload> getAuth0ClientConfiguration() {
        if (auth0ClientConfiguration == null) {
            TypeReference<Map<String, TokenPayload>> typeRefAuth0ClientConfiguration = new TypeReference<>() {
            };

            String auth0ClientConfigPath = (ENV == null || ENV.startsWith("http") || ENV.equals("test")) ?
                    "config/auth0_clients.json" : String.format("config/auth0_clients.%s.json", ENV);

            auth0ClientConfiguration = JsonHelper
                    .readItemFromJsonFile(auth0ClientConfigPath, typeRefAuth0ClientConfiguration);
        }
        return auth0ClientConfiguration;
    }
}
