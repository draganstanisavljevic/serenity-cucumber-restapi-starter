package com.orgname.qa.lib.jira;

import java.util.Set;

public enum JiraPermissionsConfig {

    INSTANCE;

    public static final String MISSING_PERMISSION_MESSAGE_FORMAT = "User doesn't have %s permission for project %s";
    public static final String EDIT_ISSUES_PERMISSION = "EDIT_ISSUES";

    private final Boolean checkEditIssuesPermission;
    private final MyPermissionsJiraClient myPermissionsJiraClient;

    JiraPermissionsConfig() {
        this.checkEditIssuesPermission = Boolean.parseBoolean(
                System.getProperty(
                        "jira.checkPermission.editIssues",
                        System.getenv("JIRA_CHECK_PERMISSION_EDIT_ISSUES")
                )
        );
        this.myPermissionsJiraClient = MyPermissionsJiraClient.INSTANCE;
    }

    public boolean isCheckEditIssuesPermission() {
        return this.checkEditIssuesPermission;
    }

    public boolean haveEditIssuesPermission(String projectKey) {
        return this.myPermissionsJiraClient.getMyPermissions(
                        Set.of(EDIT_ISSUES_PERMISSION), projectKey
                )
                .havePermission(EDIT_ISSUES_PERMISSION);
    }
}

