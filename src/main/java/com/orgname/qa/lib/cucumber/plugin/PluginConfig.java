package com.orgname.qa.lib.cucumber.plugin;

public enum PluginConfig {

    INSTANCE;

    public static final String REQUIRE_NON_NULL_MESSAGE_FORMAT = "%s is missing. " +
            "Please provide either -D%s system property or set %s environment variable";

    PluginConfig() {
    }

    public boolean isApplyFeatureTags() {
        return Boolean.parseBoolean(
                System.getProperty(
                        "apply.feature.tags",
                        System.getenv("APPLY_FEATURE_TAGS")
                )
        );
    }

    public String getFeatureTagsToApply() {
        return System.getProperty(
                "feature.tags.to.apply",
                System.getenv("FEATURE_TAGS_TO_APPLY")
        );
    }
}

