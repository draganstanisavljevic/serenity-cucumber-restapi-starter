package com.orgname.qa.lib.cucumber.plugin.jira;

import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.FieldType;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.orgname.qa.lib.cucumber.TestCaseTags;
import com.orgname.qa.lib.jira.IssueJiraClient;
import com.orgname.qa.lib.jira.JiraConfig;
import com.orgname.qa.lib.jira.MetadataJiraClient;
import com.orgname.qa.lib.xray.XrayConfig;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UpdateTestIssueCustomFieldJiraPlugin implements EventListener {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final boolean enabled;
    private final String fieldNameValue;
    private final List<String> handledTestCaseNames;
    private final MetadataJiraClient metadataJiraClient;
    private final IssueJiraClient issueJiraClient;

    private Field customField;
    private String expectedFieldValue;

    public UpdateTestIssueCustomFieldJiraPlugin(String fieldNameValue) {
        this.enabled = JiraPluginConfig.INSTANCE.isUpdateTestIssueCustomField();
        this.fieldNameValue = fieldNameValue;
        this.handledTestCaseNames = new ArrayList<>();
        this.metadataJiraClient = MetadataJiraClient.INSTANCE;
        this.issueJiraClient = IssueJiraClient.INSTANCE;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        if (this.enabled && JiraConfig.INSTANCE.isConfigured()) {
            this.customField = findField(
                getFieldName(
                    getFieldNameValueList(this.fieldNameValue)
                ),
                filterFields(
                    FieldType.CUSTOM, this.metadataJiraClient.getFields()
                )
            );
            this.expectedFieldValue = getFieldValue(
                getFieldNameValueList(this.fieldNameValue)
            );
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
            if (testTag != null && this.customField != null) {
                Issue issue = this.issueJiraClient.getIssue(
                    testCaseTags.removeTagPrefix(
                        testTag, XrayConfig.CLOUD.getTestTagPrefix()
                    )
                );
                Object fieldValue = getFieldValue(this.customField, issue);
                if (!hasFieldValue(fieldValue, this.expectedFieldValue)) {
                    updateIssueField(issue, this.customField, this.expectedFieldValue);
                }
            }
            this.handledTestCaseNames.add(testCaseName);
        }
    }

    private void updateIssueField(Issue issue, Field field, String fieldValue) {
        LOGGER.log(
            Level.INFO,
            "Update the field {0} of the test issue {1} to {2}",
            new Object[]{
                field.getName(), issue.getKey(), fieldValue
            }
        );
        this.issueJiraClient.updateIssueField(issue, field, fieldValue);
    }

    private List<String> getFieldNameValueList(String fieldNameValuePair) {
        return Arrays.stream(
                fieldNameValuePair.split("=")
            )
            .map(String::trim)
            .collect(
                Collectors.toList()
            );
    }

    private String getFieldName(List<String> fieldNameValuePair) {
        String fieldName = null;
        if (!fieldNameValuePair.isEmpty()) {
            fieldName = fieldNameValuePair.get(0);
        }
        return fieldName;
    }

    private String getFieldValue(List<String> fieldNameValuePair) {
        String fieldName = null;
        if (fieldNameValuePair.size() > 1) {
            fieldName = fieldNameValuePair.get(1);
        }
        return fieldName;
    }

    private List<Field> filterFields(FieldType type, List<Field> fields) {
        return fields.stream().filter(
                field -> type == field.getFieldType()
            )
            .collect(
                Collectors.toList()
            );
    }

    private Field findField(String name, List<Field> fields) {
        return fields.stream().filter(
                field -> name.equals(
                    field.getName()
                )
            )
            .findFirst()
            .orElse(null);
    }

    private Object getFieldValue(Field field, Issue issue) {
        return issue.getField(
                field.getId()
            )
            .getValue();
    }

    private boolean hasFieldValue(Object actual, String expected) {
        if (isComplexFieldValue(actual)) {
            return expected.equals(
                getComplexFieldValue(actual)
            );
        } else {
            return expected.equals(actual);
        }
    }

    private boolean isComplexFieldValue(Object value) {
        return JSONObject.class.isInstance(value);
    }

    private String getComplexFieldValue(Object value) {
        String rawValue = null;
        try {
            rawValue = ((JSONObject) value).getString("value");
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "Failed to read json field value", e);
        }
        return rawValue;
    }
}
