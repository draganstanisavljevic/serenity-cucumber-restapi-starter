package com.orgname.qa.lib.cucumber.plugin.xray;

import com.atlassian.sal.api.features.MissingPermissionException;
import com.orgname.qa.lib.cucumber.FeatureFile;
import com.orgname.qa.lib.cucumber.plugin.PluginConfig;
import com.orgname.qa.lib.jira.JiraPermissionsConfig;
import com.orgname.qa.lib.xray.ImportFeatureXrayClient;
import com.orgname.qa.lib.xray.XrayConfig;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestSourceRead;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportFeatureXrayPlugin implements EventListener {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final boolean enabled;
    private final String jiraProjectKey;
    private final ImportFeatureXrayClient importFeatureXrayClient;
    private final Map<URI, File> featureFilesByUri;

    public ImportFeatureXrayPlugin() {
        this(
                XrayPluginConfig.INSTANCE.getImportFeatureJiraProjectKey()
        );
    }

    public ImportFeatureXrayPlugin(String jiraProjectKey) {
        this.enabled = XrayPluginConfig.INSTANCE.isImportFeature();
        this.jiraProjectKey = jiraProjectKey;
        this.importFeatureXrayClient = new ImportFeatureXrayClient(jiraProjectKey);
        this.featureFilesByUri = new HashMap<>();
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        if (this.enabled && XrayConfig.CLOUD.isConfigured()) {
            requireJiraProjectKey();
            if (JiraPermissionsConfig.INSTANCE.isCheckEditIssuesPermission()) {
                requireEditIssuesPermission();
            }
            publisher.registerHandlerFor(TestSourceRead.class, this::handleTestSourceRead);
            publisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
        }
    }

    private void handleTestSourceRead(TestSourceRead testSourceRead) {
        URI featureURI = testSourceRead.getUri();
        this.featureFilesByUri.put(
                featureURI, new FeatureFile(featureURI).getFile()
        );
    }

    private void handleTestCaseFinished(TestCaseFinished testCaseFinished) {
        File featureFile = this.featureFilesByUri.remove(
                testCaseFinished.getTestCase().getUri()
        );
        if (featureFile != null) {
            importFeature(featureFile);
        }
    }

    private void importFeature(File featureFile) {
        LOGGER.log(
                Level.INFO, "Import the feature file {0}", featureFile.toPath()
        );
        this.importFeatureXrayClient.importFeature(featureFile);
    }

    private void requireJiraProjectKey() {
        Objects.requireNonNull(
                this.jiraProjectKey, String.format(
                        PluginConfig.REQUIRE_NON_NULL_MESSAGE_FORMAT,
                        "Jira project key", "xray.import.feature.jiraProjectKey", "XRAY_IMPORT_FEATURE_JIRA_PROJECT_KEY"
                )
        );
    }

    private void requireEditIssuesPermission() {
        if (!JiraPermissionsConfig.INSTANCE.haveEditIssuesPermission(this.jiraProjectKey)) {
            throw new MissingPermissionException(
                    String.format(
                            JiraPermissionsConfig.MISSING_PERMISSION_MESSAGE_FORMAT,
                            JiraPermissionsConfig.EDIT_ISSUES_PERMISSION, this.jiraProjectKey
                    )
            );
        }
    }
}

