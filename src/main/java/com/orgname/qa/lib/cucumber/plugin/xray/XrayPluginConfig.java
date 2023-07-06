package com.orgname.qa.lib.cucumber.plugin.xray;

public enum XrayPluginConfig {

    INSTANCE;

    XrayPluginConfig() {
    }

    public boolean isImportFeature() {
        return Boolean.parseBoolean(
                System.getProperty(
                        "xray.import.feature",
                        System.getenv("XRAY_IMPORT_FEATURE")
                )
        );
    }

    public String getImportFeatureJiraProjectKey() {
        return System.getProperty(
                "xray.import.feature.jiraProjectKey",
                System.getenv("XRAY_IMPORT_FEATURE_JIRA_PROJECT_KEY")
        );
    }

    public boolean isImportCucumberExecution() {
        return Boolean.parseBoolean(
                System.getProperty(
                        "xray.import.cucumber.execution",
                        System.getenv("XRAY_IMPORT_CUCUMBER_EXECUTION")
                )
        );
    }

    public String getImportCucumberExecutionRemoveHookTypes() {
        return System.getProperty(
                "xray.import.cucumber.execution.removeHookTypes",
                System.getenv("XRAY_IMPORT_CUCUMBER_EXECUTION_REMOVE_HOOK_TYPES")
        );
    }

    public boolean isImportCucumberExecutionKeepHookAttachments() {
        return Boolean.parseBoolean(
                System.getProperty(
                        "xray.import.cucumber.execution.keepHookAttachments",
                        System.getenv("XRAY_IMPORT_CUCUMBER_EXECUTION_KEEP_HOOK_ATTACHMENTS")
                )
        );
    }

    public String getImportCucumberExecutionJiraIssueKey() {
        return System.getProperty(
                "xray.import.cucumber.execution.jiraIssueKey",
                System.getenv("XRAY_IMPORT_CUCUMBER_EXECUTION_JIRA_ISSUE_KEY")
        );
    }

    public boolean isImportCucumberExecutionCreateJiraIssue() {
        return Boolean.parseBoolean(
                System.getProperty(
                        "xray.import.cucumber.execution.createJiraIssue",
                        System.getenv("XRAY_IMPORT_CUCUMBER_EXECUTION_CREATE_JIRA_ISSUE")
                )
        );
    }

    public String getImportCucumberExecutionJiraProjectKey() {
        return System.getProperty(
                "xray.import.cucumber.execution.jiraProjectKey",
                System.getenv("XRAY_IMPORT_CUCUMBER_EXECUTION_JIRA_PROJECT_KEY")
        );
    }

    public String getImportCucumberExecutionSummary() {
        return System.getProperty(
                "xray.import.cucumber.execution.summary",
                System.getenv("XRAY_IMPORT_CUCUMBER_EXECUTION_SUMMARY")
        );
    }

    public String getImportCucumberExecutionLabels() {
        return System.getProperty(
                "xray.import.cucumber.execution.labels",
                System.getenv("XRAY_IMPORT_CUCUMBER_EXECUTION_LABELS")
        );
    }

    public String getImportCucumberExecutionTestPlanJiraIssueKey() {
        return System.getProperty(
                "xray.import.cucumber.execution.testPlanJiraIssueKey",
                System.getenv("XRAY_IMPORT_CUCUMBER_EXECUTION_TEST_PLAN_JIRA_ISSUE_KEY")
        );
    }

    public String getImportCucumberExecutionTestEnvironments() {
        return System.getProperty(
                "xray.import.cucumber.execution.testEnvironments",
                System.getenv("XRAY_IMPORT_CUCUMBER_EXECUTION_TEST_ENVIRONMENTS")
        );
    }
}

