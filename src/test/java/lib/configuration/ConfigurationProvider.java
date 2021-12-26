package lib.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import lib.helpers.JsonHelper;

import java.util.Map;

public class ConfigurationProvider {

    private static Map<String, Map<String, String>> testsConfiguration;
    private static final String ENV = System.getenv().get("environment");

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


}
