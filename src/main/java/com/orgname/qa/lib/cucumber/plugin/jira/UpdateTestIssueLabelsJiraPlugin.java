package com.orgname.qa.lib.cucumber.plugin.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;

import com.orgname.qa.lib.cucumber.TestCaseTags;
import com.orgname.qa.lib.jira.IssueJiraClient;
import com.orgname.qa.lib.jira.JiraConfig;
import com.orgname.qa.lib.xray.XrayConfig;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateTestIssueLabelsJiraPlugin implements EventListener {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final boolean enabled;
    private final List<String> handledTestCaseNames;
    private final IssueJiraClient issueJiraClient;

    public UpdateTestIssueLabelsJiraPlugin() {
        this.enabled = JiraPluginConfig.INSTANCE.isUpdateTestIssueLabels();
        this.handledTestCaseNames = new ArrayList<>();
        this.issueJiraClient = IssueJiraClient.INSTANCE;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        if (this.enabled && JiraConfig.INSTANCE.isConfigured()) {
            publisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
        }
    }

    private void handleTestCaseFinished(TestCaseFinished testCaseFinished) {
        TestCase testCase = testCaseFinished.getTestCase();
        String testCaseName = testCase.getName();
        if (!handledTestCaseNames.contains(testCaseName)) {
            TestCaseTags testCaseTags = new TestCaseTags(testCase);
            String testTag = testCaseTags.findFirstTagWithPrefix(
                XrayConfig.CLOUD.getTestTagPrefix()
            );
            if (testTag != null) {
                Issue issue = this.issueJiraClient.getIssue(
                    testCaseTags.removeTagPrefix(
                        testTag, XrayConfig.CLOUD.getTestTagPrefix()
                    )
                );
                Set<String> tags = new HashSet<>(
                    testCaseTags.filterTagsWithoutPrefixes(
                        Arrays.asList(
                            XrayConfig.CLOUD.getRequirementTagPrefix(), XrayConfig.CLOUD.getTestTagPrefix()
                        )
                    )
                );
                if (!tags.equals(issue.getLabels())) {
                    updateIssueLabels(
                        issue, testCaseTags.removeTagPrefix(tags)
                    );
                }
            }
            this.handledTestCaseNames.add(testCaseName);
        }
    }

    private void updateIssueLabels(Issue issue, List<String> labels) {
        LOGGER.log(
            Level.INFO,
            "Update the labels of the test issue {0} to {1}",
            new Object[]{
                issue.getKey(), labels
            }
        );
        this.issueJiraClient.updateIssueLabels(issue, labels);
    }
}
