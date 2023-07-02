package com.orgname.qa.lib.cucumber.plugin.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.orgname.qa.lib.cucumber.TestCaseTags;
import com.orgname.qa.lib.cucumber.plugin.PluginConfig;
import com.orgname.qa.lib.jira.IssueJiraClient;
import com.orgname.qa.lib.jira.JiraConfig;
import com.orgname.qa.lib.xray.XrayConfig;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LinkTestIssueRequirementsJiraPlugin implements EventListener {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final boolean enabled;
    private final String testIssueLinkRelation;
    private final List<String> handledTestCaseNames;
    private final IssueJiraClient issueJiraClient;

    public LinkTestIssueRequirementsJiraPlugin() {
        this(
            XrayConfig.CLOUD.getTestIssueLinkRelation()
        );
    }

    public LinkTestIssueRequirementsJiraPlugin(String testIssueLinkRelation) {
        this.enabled = JiraPluginConfig.INSTANCE.isLinkTestIssueRequirements();
        this.testIssueLinkRelation = testIssueLinkRelation;
        this.handledTestCaseNames = new ArrayList<>();
        this.issueJiraClient = IssueJiraClient.INSTANCE;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        if (this.enabled && JiraConfig.INSTANCE.isConfigured()) {
            requireTestIssueLinkRelation();
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
                List<String> requirementIssueKeys = testCaseTags.removeTagPrefix(
                    testCaseTags.filterTagsWithPrefix(
                        XrayConfig.CLOUD.getRequirementTagPrefix()
                    ), XrayConfig.CLOUD.getRequirementTagPrefix()
                );
                requirementIssueKeys.forEach(
                    (String requirementIssueKey) -> {
                        if (!isIssueLinked(issue, requirementIssueKey)) {
                            linkIssueToRequirement(issue, requirementIssueKey);
                        }
                    }
                );
            }
            this.handledTestCaseNames.add(testCaseName);
        }
    }

    private void linkIssueToRequirement(Issue issue, String requirementIssueKey) {
        LOGGER.log(
            Level.INFO,
            "Link the test issue {0} to the requirement issue {1} with link type {2}",
            new Object[]{
                issue.getKey(), requirementIssueKey, this.testIssueLinkRelation
            }
        );
        this.issueJiraClient.linkIssue(
            issue, requirementIssueKey, this.testIssueLinkRelation
        );
    }

    private boolean isIssueLinked(Issue issue, String targetIssueKey) {
        Iterable<IssueLink> issueLinks = issue.getIssueLinks();
        List<IssueLink> issueLinkList = new ArrayList<>();
        issueLinks.forEach(issueLinkList::add);
        return issueLinkList.stream().anyMatch(
            issueLink -> targetIssueKey.equals(
                issueLink.getTargetIssueKey()
            )
        );
    }

    private void requireTestIssueLinkRelation() {
        Objects.requireNonNull(
            this.testIssueLinkRelation, String.format(
                PluginConfig.REQUIRE_NON_NULL_MESSAGE_FORMAT,
                "Jira test issue link relation", "jira.testIssueLinkRelation", "JIRA_TEST_ISSUE_LINK_RELATION"
            )
        );
    }
}
