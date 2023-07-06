package com.orgname.qa.lib.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.apache.http.HttpHeaders;

import javax.ws.rs.core.UriBuilder;

public enum JiraRestClients {

    INSTANCE;

    private static final String AUTHORIZATION_HEADER_FORMAT = "Basic %s";
    public static final String FAILED_TO_CLOSE_HTTP_CLIENT_LOG_MESSAGE = "Failed to close http client";

    public JiraRestClient getGeneralJiraRestClient() {
        return new AsynchronousJiraRestClientFactory()
            .create(
                JiraConfig.INSTANCE.getJiraUri(),
                builder -> builder.setHeader(
                    HttpHeaders.AUTHORIZATION, String.format(
                        AUTHORIZATION_HEADER_FORMAT, JiraConfig.INSTANCE.getBase64EncodedUserToken()
                    )
                )
            );
    }

    public ExtendedMyPermissionsRestClient getExtendedMyPermissionsRestClient() {
        return new ExtendedAsynchronousMyPermissionsRestClient(
            UriBuilder.fromUri(
                    JiraConfig.INSTANCE.getJiraUri()
                )
                .path("/rest/api/latest").build(),
            new AsynchronousHttpClientFactory()
                .createClient(
                    JiraConfig.INSTANCE.getJiraUri(),
                    builder -> builder.setHeader(
                        HttpHeaders.AUTHORIZATION, String.format(
                            AUTHORIZATION_HEADER_FORMAT, JiraConfig.INSTANCE.getBase64EncodedUserToken()
                        )
                    )
                )
        );
    }
}
