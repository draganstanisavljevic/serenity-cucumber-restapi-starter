package com.orgname.qa.lib.jira;

import com.orgname.qa.lib.cucumber.plugin.PluginConfig;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public enum JiraConfig {

    INSTANCE;

    private final String host;
    private final String userName;
    private final String userToken;

    private String userTokenBase64;

    JiraConfig() {
        this.host = System.getProperty(
            "jira.host", System.getenv("JIRA_HOST")
        );
        this.userName = System.getProperty(
            "jira.userName", System.getenv("JIRA_USER_NAME")
        );
        this.userToken = System.getProperty(
            "jira.userToken", System.getenv("JIRA_USER_TOKEN")
        );
        this.userTokenBase64 = System.getProperty(
            "jira.userToken.base64", System.getenv("JIRA_USER_TOKEN_BASE64")
        );
    }

    public boolean isConfigured() {
        return this.host != null && ((this.userName != null && this.userToken != null) || this.userTokenBase64 != null);
    }

    public URI getJiraUri() {
        requireHost();
        return URI.create(this.host);
    }

    public synchronized String getBase64EncodedUserToken() {
        if (this.userTokenBase64 == null) {
            requireUserName();
            requireUserToken();
            this.userTokenBase64 = base64EncodeUserToken();
        }
        return userTokenBase64;
    }

    private void requireHost() {
        Objects.requireNonNull(
            this.host, String.format(
                PluginConfig.REQUIRE_NON_NULL_MESSAGE_FORMAT,
                "Jira host", "jira.host", "JIRA_HOST"
            )
        );
    }

    private void requireUserName() {
        Objects.requireNonNull(
            this.userName, String.format(
                PluginConfig.REQUIRE_NON_NULL_MESSAGE_FORMAT,
                "Jira user name", "jira.userName", "JIRA_USER_NAME"
            )
        );
    }

    private void requireUserToken() {
        Objects.requireNonNull(
            this.userToken, String.format(
                PluginConfig.REQUIRE_NON_NULL_MESSAGE_FORMAT,
                "Jira user token", "jira.userToken", "JIRA_USER_TOKEN"
            )
        );
    }

    private String base64EncodeUserToken() {
        return Base64.getEncoder()
            .encodeToString(
                String.format("%s:%s", this.userName, this.userToken)
                    .getBytes(StandardCharsets.UTF_8)
            );
    }
}
