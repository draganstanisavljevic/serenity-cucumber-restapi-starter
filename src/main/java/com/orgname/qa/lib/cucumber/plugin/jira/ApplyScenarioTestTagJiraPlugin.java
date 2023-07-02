package com.orgname.qa.lib.cucumber.plugin.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.orgname.qa.lib.cucumber.FeatureFile;
import com.orgname.qa.lib.cucumber.TestCaseTags;
import com.orgname.qa.lib.cucumber.plugin.PluginConfig;
import com.orgname.qa.lib.jira.JiraConfig;
import com.orgname.qa.lib.jira.SearchJiraClient;
import com.orgname.qa.lib.xray.XrayConfig;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class ApplyScenarioTestTagJiraPlugin implements EventListener {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final boolean enabled;
    private final String jiraProjectKey;
    private final List<String> handledTestCaseNames;
    private final SearchJiraClient searchJiraClient;

    public ApplyScenarioTestTagJiraPlugin() {
        this(
            JiraPluginConfig.INSTANCE.getApplyScenarioTestTagJiraProjectKey()
        );
    }

    public ApplyScenarioTestTagJiraPlugin(String jiraProjectKey) {
        this.enabled = JiraPluginConfig.INSTANCE.isApplyScenarioTestTag();
        this.jiraProjectKey = jiraProjectKey;
        this.handledTestCaseNames = new ArrayList<>();
        this.searchJiraClient = SearchJiraClient.INSTANCE;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        if (this.enabled && JiraConfig.INSTANCE.isConfigured()) {
            requireJiraProjectKey();
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
            if (testTag == null) {
                Issue issue = this.searchJiraClient.searchIssue(this.jiraProjectKey, testCaseName);
                if (issue != null) {
                    File featureFile = new FeatureFile(
                        testCase.getUri()
                    )
                        .getFile();
                    transformFeatureFile(
                        featureFile, testCase, issue.getKey()
                    );
                }
            }
            handledTestCaseNames.add(testCaseName);
        }
    }

    private void transformFeatureFile(File featureFile, TestCase testCase, String testIssueKey) {
        try {
            List<String> currentLines = Files.readAllLines(
                featureFile.toPath()
            );
            List<String> newLines = new ArrayList<>();
            currentLines.forEach(
                (String line) -> {
                    if (isScenarioLine(line, testCase) && isTestCaseLine(line, testCase)) {
                        LOGGER.log(
                            Level.INFO,
                            "Add test tag for the test issue {0} to the scenario {1} in the feature file {2}",
                            new Object[]{
                                testIssueKey, testCase.getName(), featureFile.toPath()
                            }
                        );
                        addTestTagLine(
                            newLines, getKeywordIndentation(testCase, line), testIssueKey
                        );
                    }
                    newLines.add(line);
                }
            );
            Files.write(
                featureFile.toPath(), newLines, StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read feature file", e);
        }
    }

    private boolean isScenarioLine(String line, TestCase scenario) {
        return line.trim().startsWith(
            scenario.getKeyword()
        );
    }

    private boolean isTestCaseLine(String line, TestCase scenario) {
        return line.trim().endsWith(
            scenario.getName()
        );
    }

    private int getKeywordIndentation(TestCase scenario, String line) {
        return line.indexOf(
            scenario.getKeyword()
        );
    }

    private void addTestTagLine(List<String> newLines, int indentation, String testIssueKey) {
        newLines.add(
            buildTagLine(
                indentation, XrayConfig.CLOUD.getTestTagPrefix(), testIssueKey
            )
        );
    }

    private String buildTagLine(Integer indentation, String tagPrefix, String testIssueKey) {
        StringBuilder testTagLineBuilder = new StringBuilder();
        IntStream.range(0, indentation).forEach(
            i -> testTagLineBuilder.append(" ")
        );
        testTagLineBuilder.append(
            String.format(
                "%s%s", tagPrefix, testIssueKey
            )
        );
        return testTagLineBuilder.toString();
    }

    private void requireJiraProjectKey() {
        Objects.requireNonNull(
            this.jiraProjectKey, String.format(
                PluginConfig.REQUIRE_NON_NULL_MESSAGE_FORMAT,
                "Jira project key",
                "jira.apply.scenario.testTag.jiraProjectKey",
                "JIRA_APPLY_SCENARIO_TEST_TAG_JIRA_PROJECT_KEY"
            )
        );
    }
}
