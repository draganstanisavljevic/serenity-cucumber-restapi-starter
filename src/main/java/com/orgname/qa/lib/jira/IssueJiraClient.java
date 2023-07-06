package com.orgname.qa.lib.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum IssueJiraClient {

    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final Map<String, Issue> issuesByIssueKey = new HashMap<>();

    public Issue getIssue(String issueKey) {
        if (!this.issuesByIssueKey.containsKey(issueKey)) {
            try (JiraRestClient client = JiraRestClients.INSTANCE.getGeneralJiraRestClient()) {
                Issue issue = client.getIssueClient().getIssue(issueKey).claim();
                this.issuesByIssueKey.put(issueKey, issue);
            } catch (RestClientException e) {
                LOGGER.log(Level.SEVERE, "Failed to get issue", e);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, JiraRestClients.FAILED_TO_CLOSE_HTTP_CLIENT_LOG_MESSAGE, e);
            }
        }
        return this.issuesByIssueKey.get(issueKey);
    }

    public List<Transition> getIssueTransitions(Issue issue) {
        List<Transition> transitions = new ArrayList<>();
        try (JiraRestClient client = JiraRestClients.INSTANCE.getGeneralJiraRestClient()) {
            client.getIssueClient().getTransitions(issue).claim().forEach(
                transitions::add
            );
        } catch (RestClientException e) {
            LOGGER.log(Level.SEVERE, "Failed to get issue transitions", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, JiraRestClients.FAILED_TO_CLOSE_HTTP_CLIENT_LOG_MESSAGE, e);
        }
        return transitions;
    }

    public void transitionIssue(Issue issue, Transition transition) {
        try (JiraRestClient client = JiraRestClients.INSTANCE.getGeneralJiraRestClient()) {
            client.getIssueClient().transition(
                    issue, new TransitionInput(
                        transition.getId()
                    )
                )
                .claim();
        } catch (RestClientException e) {
            LOGGER.log(Level.SEVERE, "Failed to transition issue", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, JiraRestClients.FAILED_TO_CLOSE_HTTP_CLIENT_LOG_MESSAGE, e);
        }
    }

    public void updateIssueField(Issue issue, Field field, Object fieldValue) {
        try (JiraRestClient client = JiraRestClients.INSTANCE.getGeneralJiraRestClient()) {
            client.getIssueClient().updateIssue(
                    issue.getKey(), IssueInput.createWithFields(
                        new FieldInput(
                            field.getId(), ComplexIssueInputFieldValue.with("value", fieldValue)
                        )
                    )
                )
                .claim();
        } catch (RestClientException e) {
            LOGGER.log(Level.SEVERE, "Failed to update issue custom field", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, JiraRestClients.FAILED_TO_CLOSE_HTTP_CLIENT_LOG_MESSAGE, e);
        }
    }

    public void updateIssueLabels(Issue issue, List<String> labels) {
        try (JiraRestClient client = JiraRestClients.INSTANCE.getGeneralJiraRestClient()) {
            client.getIssueClient().updateIssue(
                    issue.getKey(), IssueInput.createWithFields(
                        new FieldInput(
                            IssueFieldId.LABELS_FIELD, labels
                        )
                    )
                )
                .claim();
        } catch (RestClientException e) {
            LOGGER.log(Level.SEVERE, "Failed to update issue labels", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, JiraRestClients.FAILED_TO_CLOSE_HTTP_CLIENT_LOG_MESSAGE, e);
        }
    }

    public void linkIssue(Issue fromIssue, String toIssueKey, String linkType) {
        try (JiraRestClient client = JiraRestClients.INSTANCE.getGeneralJiraRestClient()) {
            client.getIssueClient().linkIssue(
                    new LinkIssuesInput(fromIssue.getKey(), toIssueKey, linkType)
                )
                .claim();
        } catch (RestClientException e) {
            LOGGER.log(Level.SEVERE, "Failed to link issue", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, JiraRestClients.FAILED_TO_CLOSE_HTTP_CLIENT_LOG_MESSAGE, e);
        }
    }
}
