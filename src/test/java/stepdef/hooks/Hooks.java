package stepdef.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lib.configuration.ConfigurationProvider;

public class Hooks {

    @Before
    public static void before(final Scenario scenario) {
        ConfigurationProvider.loadConfiguration();
    }

}
