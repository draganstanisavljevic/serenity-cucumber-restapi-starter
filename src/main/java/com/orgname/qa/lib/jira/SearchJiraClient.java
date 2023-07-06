package com.orgname.qa.lib.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum SearchJiraClient {

    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String SEARCH_ISSUE_JQL = "project = \"%s\" and summary ~ '\\\"%s\\\"'";

    public Issue searchIssue(String jiraProjectKey, String summary) {
        List<Issue> issues = new ArrayList<>();
        try (JiraRestClient client = JiraRestClients.INSTANCE.getGeneralJiraRestClient()) {
            client.getSearchClient().searchJql(
                    String.format(SEARCH_ISSUE_JQL, jiraProjectKey, summary)
                )
                .claim()
                .getIssues()
                .forEach(
                    issues::add
                );
        } catch (RestClientException e) {
            LOGGER.log(Level.SEVERE, "Failed to search issue", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, JiraRestClients.FAILED_TO_CLOSE_HTTP_CLIENT_LOG_MESSAGE, e);
        }
        return issues.stream().filter(
                issue -> summary.equals(
                    issue.getSummary()
                )
            )
            .findFirst()
            .orElse(null);
    }
}
