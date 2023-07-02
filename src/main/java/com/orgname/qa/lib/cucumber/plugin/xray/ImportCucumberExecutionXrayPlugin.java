package com.orgname.qa.lib.cucumber.plugin.xray;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.orgname.qa.lib.cucumber.TestCaseTags;
import com.orgname.qa.lib.xray.ImportCucumberExecutionXrayClient;
import com.orgname.qa.lib.xray.XrayConfig;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.HookType;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestRunFinished;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ImportCucumberExecutionXrayPlugin implements EventListener {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String RESULTS_JSON_TAG_ELEMENT = "tags";
    private static final String RESULTS_JSON_SCENARIOS_ELEMENT = "elements";
    private static final String RESULTS_JSON_STEPS_ELEMENT = "steps";
    private static final String RESULTS_JSON_ATTACHMENT_ELEMENT = "embeddings";
    private static final String TEST_EXECUTION_INFO_FIELDS = "fields";
    private static final String TEST_EXECUTION_INFO_PROJECT = "project";
    private static final String TEST_EXECUTION_INFO_KEY = "key";
    private static final String TEST_EXECUTION_INFO_SUMMARY = "summary";
    private static final String TEST_EXECUTION_INFO_LABELS = "labels";
    private static final String TEST_EXECUTION_INFO_XRAY_FIELDS = "xrayFields";
    private static final String TEST_EXECUTION_INFO_TEST_PLAN_KEY = "testPlanKey";
    private static final String TEST_EXECUTION_INFO_ENVIRONMENTS = "environments";
    private static final Pattern TEST_EXECUTION_JIRA_ISSUE_KEY_PATTERN = Pattern.compile("\"key\":\"(\\w+-\\d+)\"");

    private final boolean enabled;
    private final File resultsJson;
    private final ObjectMapper objectMapper;
    private final String removeHookTypes;
    private final boolean keepHookAttachments;
    private final boolean createTestExecutionJiraIssue;
    private final String testExecutionJiraProjectKey;
    private final String testExecutionLabels;
    private final String testExecutionTestPlanJiraIssueKey;
    private final String testExecutionTestEnvironments;
    private final ImportCucumberExecutionXrayClient importCucumberExecutionXrayClient;

    private boolean testCaseFinished;
    private String testExecutionJiraIssueKey;
    private String testExecutionSummary;

    public ImportCucumberExecutionXrayPlugin(File resultsJson) {
        this.enabled = XrayPluginConfig.INSTANCE.isImportCucumberExecution();
        this.resultsJson = resultsJson;
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.removeHookTypes = XrayPluginConfig.INSTANCE.getImportCucumberExecutionRemoveHookTypes();
        this.keepHookAttachments = XrayPluginConfig.INSTANCE.isImportCucumberExecutionKeepHookAttachments();
        this.testExecutionJiraIssueKey = XrayPluginConfig.INSTANCE.getImportCucumberExecutionJiraIssueKey();
        this.createTestExecutionJiraIssue = XrayPluginConfig.INSTANCE.isImportCucumberExecutionCreateJiraIssue();
        this.testExecutionJiraProjectKey = XrayPluginConfig.INSTANCE.getImportCucumberExecutionJiraProjectKey();
        this.testExecutionSummary = XrayPluginConfig.INSTANCE.getImportCucumberExecutionSummary();
        this.testExecutionLabels = XrayPluginConfig.INSTANCE.getImportCucumberExecutionLabels();
        this.testExecutionTestPlanJiraIssueKey =
                XrayPluginConfig.INSTANCE.getImportCucumberExecutionTestPlanJiraIssueKey();
        this.testExecutionTestEnvironments = XrayPluginConfig.INSTANCE.getImportCucumberExecutionTestEnvironments();
        this.importCucumberExecutionXrayClient = new ImportCucumberExecutionXrayClient();
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        if (this.enabled && XrayConfig.CLOUD.isConfigured()) {
            publisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
            publisher.registerHandlerFor(TestRunFinished.class, this::handleTestRunFinished);
        }
    }

    private void handleTestCaseFinished(TestCaseFinished testCaseFinished) {
        if (!this.testCaseFinished) {
            this.testCaseFinished = true;
        }
    }

    private void handleTestRunFinished(TestRunFinished testRunFinished) {
        if (this.testCaseFinished) {
            List<Map<String, Object>> results = readResults();
            if (this.removeHookTypes != null) {
                removeHooks(
                        parseHookTypes(this.removeHookTypes), results
                );
                writeResults(results);
            }
            if (this.createTestExecutionJiraIssue || this.testExecutionJiraIssueKey == null) {
                Map<String, Object> testExecutionInfo = readTestExecutionInfo();
                appendTestExecutionInfo(testExecutionInfo);
                importExecutionResults(
                        writeTestExecutionInfo(testExecutionInfo)
                );
            } else {
                appendTestExecutionTag(
                        new TestCaseTags().addTagPrefix(this.testExecutionJiraIssueKey), results
                );
                writeResults(results);
                importExecutionResults();
            }
        }
    }

    private List<Map<String, Object>> readResults() {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            results = this.objectMapper.readValue(this.resultsJson, new TypeReference<>() {
            });
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read results from json", e);
        }
        return results;
    }

    private void writeResults(List<Map<String, Object>> results) {
        try {
            this.objectMapper.writeValue(this.resultsJson, results);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write results to json", e);
        }
    }

    private Map<String, Object> readTestExecutionInfo() {
        Map<String, Object> results = new HashMap<>();
        try {
            results = this.objectMapper.readValue(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("test-execution-info.json"),
                    new TypeReference<>() {
                    }
            );
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read results from json", e);
        }
        return results;
    }

    private File writeTestExecutionInfo(Map<String, Object> results) {
        File testExecutionInfo = null;
        try {
            testExecutionInfo = Files.createTempFile("test-execution-info", ".json").toFile();
            this.objectMapper.writeValue(testExecutionInfo, results);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write test execution info to json", e);
        }
        return testExecutionInfo;
    }

    private void appendTestExecutionInfo(Map<String, Object> testExecutionInfo) {
        appendProjectJiraKey(this.testExecutionJiraProjectKey, testExecutionInfo);
        if (this.testExecutionSummary == null) {
            this.testExecutionSummary = formatTestExecutionSummary();
        }
        if (this.testExecutionLabels != null) {
            appendTestExecutionLabels(
                    parseList(this.testExecutionLabels), testExecutionInfo
            );
        }
        appendTestExecutionSummary(this.testExecutionSummary, testExecutionInfo);
        appendTestPlanJiraKey(this.testExecutionTestPlanJiraIssueKey, testExecutionInfo);
        if (this.testExecutionTestEnvironments != null) {
            appendEnvironments(
                    parseList(this.testExecutionTestEnvironments), testExecutionInfo
            );
        }
    }

    private void appendProjectJiraKey(String projectJiraKey, Map<String, Object> testExecutionInfo) {
        Map<String, Object> fields = (Map<String, Object>) testExecutionInfo.get(TEST_EXECUTION_INFO_FIELDS);
        Map<String, Object> project = (Map<String, Object>) fields.get(TEST_EXECUTION_INFO_PROJECT);
        project.put(TEST_EXECUTION_INFO_KEY, projectJiraKey);
    }

    private String formatTestExecutionSummary() {
        return String.format(
                "Test Execution at %s", ZonedDateTime.now(ZoneOffset.UTC).format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                )
        );
    }

    private void appendTestExecutionSummary(String summary, Map<String, Object> testExecutionInfo) {
        Map<String, Object> fields = (Map<String, Object>) testExecutionInfo.get(TEST_EXECUTION_INFO_FIELDS);
        fields.put(TEST_EXECUTION_INFO_SUMMARY, summary);
    }

    private void appendTestExecutionLabels(List<String> labels, Map<String, Object> testExecutionInfo) {
        Map<String, Object> fields = (Map<String, Object>) testExecutionInfo.get(TEST_EXECUTION_INFO_FIELDS);
        fields.put(TEST_EXECUTION_INFO_LABELS, labels);
    }

    private void appendTestPlanJiraKey(String testPlanJiraKey, Map<String, Object> testExecutionInfo) {
        Map<String, Object> xrayFields = (Map<String, Object>) testExecutionInfo.get(TEST_EXECUTION_INFO_XRAY_FIELDS);
        xrayFields.put(TEST_EXECUTION_INFO_TEST_PLAN_KEY, testPlanJiraKey);
    }

    private void appendEnvironments(List<String> environments, Map<String, Object> testExecutionInfo) {
        Map<String, Object> xrayFields = (Map<String, Object>) testExecutionInfo.get(TEST_EXECUTION_INFO_XRAY_FIELDS);
        xrayFields.put(TEST_EXECUTION_INFO_ENVIRONMENTS, environments);
    }

    private void appendTestExecutionTag(String testExecutionTag, List<Map<String, Object>> results) {
        results.forEach(
                (Map<String, Object> feature) -> {
                    List<Map<String, Object>> tags = (List<Map<String, Object>>) feature.get(RESULTS_JSON_TAG_ELEMENT);
                    if (!hasTag(testExecutionTag, tags)) {
                        List<Map<String, Object>> newTags = addTag(testExecutionTag, tags);
                        feature.put(RESULTS_JSON_TAG_ELEMENT, newTags);
                    }
                }
        );
    }

    private List<String> parseList(String list) {
        return Arrays.stream(
                        list.split(",")
                )
                .map(String::trim)
                .collect(
                        Collectors.toList()
                );
    }

    private boolean hasTag(String tag, List<Map<String, Object>> tags) {
        return tags.stream().anyMatch(
                t -> tag.equals(
                        t.get("name")
                )
        );
    }

    private List<Map<String, Object>> addTag(String tag, List<Map<String, Object>> tags) {
        Map<String, Object> newTag = new LinkedHashMap<>();
        newTag.put("name", tag);
        newTag.put("type", "Tag");

        List<Map<String, Object>> newTags = new ArrayList<>(tags);
        newTags.add(newTag);
        return newTags;
    }

    private List<HookType> parseHookTypes(String hooTypes) {
        return Arrays.stream(
                        hooTypes.split(",")
                )
                .map(String::trim)
                .map(String::toUpperCase)
                .map(HookType::valueOf)
                .collect(
                        Collectors.toList()
                );
    }

    private void removeHooks(List<HookType> hookTypes, List<Map<String, Object>> results) {
        results.forEach(
                (Map<String, Object> feature) -> ((List<Map<String, Object>>) feature.get(RESULTS_JSON_SCENARIOS_ELEMENT))
                        .forEach(
                                (Map<String, Object> scenario) -> {
                                    removeScenarioHooks(hookTypes, scenario);
                                    ((List<Map<String, Object>>) scenario.get(RESULTS_JSON_STEPS_ELEMENT))
                                            .forEach(
                                                    (Map<String, Object> step) -> removeStepHooks(hookTypes, step)
                                            );
                                }
                        )
        );
    }

    private void removeScenarioHooks(List<HookType> hookTypes, Map<String, Object> scenario) {
        if (hookTypes.contains(HookType.BEFORE)) {
            Optional.ofNullable(
                            (List<Map<String, Object>>) scenario.get(
                                    HookType.BEFORE.name().toLowerCase()
                            )
                    )
                    .ifPresent(this::removeHookIf);
        }
        if (hookTypes.contains(HookType.AFTER)) {
            Optional.ofNullable(
                            (List<Map<String, Object>>) scenario.get(
                                    HookType.AFTER.name().toLowerCase()
                            )
                    )
                    .ifPresent(this::removeHookIf);
        }
    }

    private void removeStepHooks(List<HookType> hookTypes, Map<String, Object> step) {
        if (hookTypes.contains(HookType.BEFORE_STEP)) {
            Optional.ofNullable(
                            (List<Map<String, Object>>) step.get(
                                    HookType.BEFORE.name().toLowerCase()
                            )
                    )
                    .ifPresent(this::removeHookIf);
        }
        if (hookTypes.contains(HookType.AFTER_STEP)) {
            Optional.ofNullable(
                            (List<Map<String, Object>>) step.get(
                                    HookType.AFTER.name().toLowerCase()
                            )
                    )
                    .ifPresent(this::removeHookIf);
        }
    }

    private void removeHookIf(List<Map<String, Object>> hook) {
        hook.removeIf(
                element -> !(keepHookAttachments && element.containsKey(RESULTS_JSON_ATTACHMENT_ELEMENT))
        );
    }

    private void importExecutionResults() {
        LOGGER.log(
                Level.INFO, "Import the execution results {0} into the existing Jira issue {1}", new Object[]{
                        this.resultsJson.toPath(), this.testExecutionJiraIssueKey
                }
        );
        this.importCucumberExecutionXrayClient.importCucumberExecutionResults(this.resultsJson);
        LOGGER.log(
                Level.INFO, "Imported the execution results into the Jira issue {0}", this.testExecutionJiraIssueKey
        );
    }

    private void importExecutionResults(File testExecutionInfoJson) {
        LOGGER.log(
                Level.INFO, "Import the execution results {0} into the new Jira issue", this.resultsJson.toPath()
        );
        this.testExecutionJiraIssueKey = extractTestExecutionJiraIssueKey(
                this.importCucumberExecutionXrayClient.importCucumberExecutionResults(
                        testExecutionInfoJson, this.resultsJson
                )
        );
        LOGGER.log(
                Level.INFO, "Imported the execution results into the Jira issue {0}", this.testExecutionJiraIssueKey
        );
    }

    private String extractTestExecutionJiraIssueKey(HttpResponse<String> response) {
        Matcher matcher = TEST_EXECUTION_JIRA_ISSUE_KEY_PATTERN.matcher(
                response.body()
        );
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}

