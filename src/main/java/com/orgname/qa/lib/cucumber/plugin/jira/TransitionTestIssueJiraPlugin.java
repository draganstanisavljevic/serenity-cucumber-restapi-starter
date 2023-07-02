package com.orgname.qa.lib.cucumber.plugin.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Transition;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TransitionTestIssueJiraPlugin implements EventListener {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final boolean enabled;
    private final String transitions;
    private final List<String> handledTestCaseNames;
    private final IssueJiraClient issueJiraClient;

    private List<String> transitionList;

    public TransitionTestIssueJiraPlugin() {
        this(
            JiraPluginConfig.INSTANCE.getTestIssueTransitions()
        );
    }

    public TransitionTestIssueJiraPlugin(String transitions) {
        this.enabled = JiraPluginConfig.INSTANCE.isTransitionTestIssue();
        this.transitions = transitions;
        this.handledTestCaseNames = new ArrayList<>();
        this.issueJiraClient = IssueJiraClient.INSTANCE;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        if (this.enabled && JiraConfig.INSTANCE.isConfigured()) {
            requireTransitions();
            this.transitionList = parseTransitions(this.transitions);
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
                this.transitionList.forEach(
                    (String nextTransition) -> {
                        Transition transition = findTransition(
                            nextTransition, this.issueJiraClient.getIssueTransitions(issue)
                        );
                        if (transition != null) {
                            transitionIssue(issue, transition);
                        }
                    }
                );
            }
            this.handledTestCaseNames.add(testCaseName);
        }
    }

    private List<String> parseTransitions(String transitions) {
        return Arrays.stream(
                transitions.split(",")
            )
            .map(String::trim)
            .collect(
                Collectors.toList()
            );
    }

    private void transitionIssue(Issue issue, Transition transition) {
        LOGGER.log(
            Level.INFO,
            "Transition the test issue {0} to {1}",
            new Object[]{
                issue.getKey(), transition.getName()
            }
        );
        this.issueJiraClient.transitionIssue(
            issue, transition
        );
    }

    private Transition findTransition(String expected, List<Transition> actual) {
        return actual.stream().filter(
                transition -> expected.equals(
                    transition.getName()
                )
            )
            .findFirst()
            .orElse(null);
    }

    private void requireTransitions() {
        Objects.requireNonNull(
            this.transitions, String.format(
                PluginConfig.REQUIRE_NON_NULL_MESSAGE_FORMAT,
                "Jira test issue transition(s)", "jira.testIssue.transitions", "JIRA_TEST_ISSUE_TRANSITIONS"
            )
        );
    }
}
