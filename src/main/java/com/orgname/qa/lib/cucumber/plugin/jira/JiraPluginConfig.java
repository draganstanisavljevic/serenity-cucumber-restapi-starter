package com.orgname.qa.lib.cucumber.plugin.jira;

public enum JiraPluginConfig {

    INSTANCE;

    JiraPluginConfig() {
    }

    public boolean isTransitionTestIssue() {
        return Boolean.parseBoolean(
            System.getProperty(
                "jira.transition.testIssue",
                System.getenv("JIRA_TRANSITION_TEST_ISSUE")
            )
        );
    }

    public String getTestIssueTransitions() {
        return System.getProperty(
            "jira.testIssue.transitions",
            System.getenv("JIRA_TEST_ISSUE_TRANSITIONS")
        );
    }

    public boolean isLinkTestIssueRequirements() {
        return Boolean.parseBoolean(
            System.getProperty(
                "jira.link.testIssue.requirements",
                System.getenv("JIRA_LINK_TEST_ISSUE_REQUIREMENTS")
            )
        );
    }

    public boolean isUpdateTestIssueCustomField() {
        return Boolean.parseBoolean(
            System.getProperty(
                "jira.update.testIssue.custom.field",
                System.getenv("JIRA_UPDATE_TEST_ISSUE_CUSTOM_FIELD")
            )
        );
    }

    public boolean isUpdateTestIssueLabels() {
        return Boolean.parseBoolean(
            System.getProperty(
                "jira.update.testIssue.labels",
                System.getenv("JIRA_UPDATE_TEST_ISSUE_LABELS")
            )
        );
    }

    public boolean isApplyScenarioTestTag() {
        return Boolean.parseBoolean(
            System.getProperty(
                "jira.apply.scenario.testTag",
                System.getenv("JIRA_APPLY_SCENARIO_TEST_TAG")
            )
        );
    }

    public String getApplyScenarioTestTagJiraProjectKey() {
        return System.getProperty(
            "jira.apply.scenario.testTag.jiraProjectKey",
            System.getenv("JIRA_APPLY_SCENARIO_TEST_TAG_JIRA_PROJECT_KEY")
        );
    }
}
