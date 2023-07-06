package com.orgname.qa.lib.xray;


import com.orgname.qa.lib.cucumber.plugin.PluginConfig;

import java.util.Objects;
import java.util.Optional;

public enum XrayConfig {

    CLOUD("https://xray.cloud.getxray.app");

    private static final String REQUIREMENT_TAG_PREFIX_DEFAULT = "@REQ_";
    private static final String TEST_TAG_PREFIX_DEFAULT = "@TEST_";
    private static final String TEST_ISSUE_LINK_RELATION_DEFAULT = "Test";

    private final String host;
    private final String clientId;
    private final String clientSecret;
    private final String requirementTagPrefix;
    private final String testTagPrefix;
    private final String testIssueLinkRelation;

    XrayConfig(String host) {
        this.host = host;
        this.clientId = System.getProperty(
                "xray.clientId", System.getenv("XRAY_CLIENT_ID")
        );
        this.clientSecret = System.getProperty(
                "xray.clientSecret", System.getenv("XRAY_CLIENT_SECRET")
        );
        this.requirementTagPrefix = Optional.ofNullable(
                        System.getProperty(
                                "xray.requirementTagPrefix", System.getenv("XRAY_REQUIREMENT_TAG_PREFIX")
                        )
                )
                .orElse(REQUIREMENT_TAG_PREFIX_DEFAULT);
        this.testTagPrefix = Optional.ofNullable(
                        System.getProperty(
                                "xray.testTagPrefix", System.getenv("XRAY_TEST_TAG_PREFIX")
                        )
                )
                .orElse(TEST_TAG_PREFIX_DEFAULT);
        this.testIssueLinkRelation = Optional.ofNullable(
                        System.getProperty(
                                "xray.testIssueLinkRelation", System.getenv("XRAY_TEST_ISSUE_LINK_RELATION")
                        )
                )
                .orElse(TEST_ISSUE_LINK_RELATION_DEFAULT);
    }

    public boolean isConfigured() {
        return this.clientId != null && this.clientSecret != null;
    }

    public String getHost() {
        return this.host;
    }

    public String getClientId() {
        requireClientId();
        return this.clientId;
    }

    public String getClientSecret() {
        requireClientSecret();
        return this.clientSecret;
    }

    public String getRequirementTagPrefix() {
        return requirementTagPrefix;
    }

    public String getTestTagPrefix() {
        return this.testTagPrefix;
    }

    public String getTestIssueLinkRelation() {
        return this.testIssueLinkRelation;
    }

    private void requireClientId() {
        Objects.requireNonNull(
                this.clientId, String.format(
                        PluginConfig.REQUIRE_NON_NULL_MESSAGE_FORMAT,
                        "Xray client id", "xray.clientId", "XRAY_CLIENT_ID"
                )
        );
    }

    private void requireClientSecret() {
        Objects.requireNonNull(
                this.clientSecret, String.format(
                        PluginConfig.REQUIRE_NON_NULL_MESSAGE_FORMAT,
                        "Xray client secret", "xray.clientSecret", "XRAY_CLIENT_SECRET"
                )
        );
    }
}

