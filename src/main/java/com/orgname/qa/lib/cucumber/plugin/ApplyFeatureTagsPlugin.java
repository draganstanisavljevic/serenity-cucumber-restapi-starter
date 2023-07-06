package com.orgname.qa.lib.cucumber.plugin;

import com.orgname.qa.lib.cucumber.FeatureFile;
import com.orgname.qa.lib.cucumber.TestCaseTags;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ApplyFeatureTagsPlugin implements EventListener {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final boolean enabled;
    private final String tags;
    private final List<URI> handledTestCaseURIs;

    private List<String> tagList;

    public ApplyFeatureTagsPlugin() {
        this(
                PluginConfig.INSTANCE.getFeatureTagsToApply()
        );
    }

    public ApplyFeatureTagsPlugin(String tags) {
        this.enabled = PluginConfig.INSTANCE.isApplyFeatureTags();
        this.tags = tags;
        this.handledTestCaseURIs = new ArrayList<>();
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        if (this.enabled) {
            requireTags();
            this.tagList = parseTags(this.tags);
            publisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
        }
    }

    private void handleTestCaseFinished(TestCaseFinished testCaseFinished) {
        TestCase testCase = testCaseFinished.getTestCase();
        URI testCaseUri = testCase.getUri();
        if (!handledTestCaseURIs.contains(testCaseUri)) {
            TestCaseTags testCaseTags = new TestCaseTags(testCase);
            this.tagList.forEach(
                    (String tag) -> {
                        if (!testCaseTags.hasTag(tag)) {
                            File featureFile = new FeatureFile(testCaseUri)
                                    .getFile();
                            transformFeatureFile(featureFile, tag);
                        }
                    }
            );
            handledTestCaseURIs.add(testCaseUri);
        }
    }

    private List<String> parseTags(String transitions) {
        return Arrays.stream(
                        transitions.split(",")
                )
                .map(String::trim)
                .collect(
                        Collectors.toList()
                );
    }

    private void transformFeatureFile(File featureFile, String tag) {
        try {
            List<String> currentLines = Files.readAllLines(
                    featureFile.toPath()
            );
            List<String> newLines = new ArrayList<>();
            currentLines.forEach(
                    (String line) -> {
                        if (isFeatureLine(line)) {
                            LOGGER.log(
                                    Level.INFO,
                                    "Add tag {0} to the feature {1} in the feature file {2}",
                                    new Object[]{
                                            tag, getFeatureName(line), featureFile.toPath()
                                    }
                            );
                            newLines.add(tag);
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

    private boolean isFeatureLine(String line) {
        return line.trim().startsWith("Feature");
    }

    private String getFeatureName(String line) {
        return line.replace("Feature:", "").trim();
    }

    private void requireTags() {
        Objects.requireNonNull(
                this.tags, String.format(
                        PluginConfig.REQUIRE_NON_NULL_MESSAGE_FORMAT,
                        "Feature tag(s) to apply", "feature.tags.to.apply", "FEATURE_TAGS_TO_APPLY"
                )
        );
    }

}

