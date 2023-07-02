package com.orgname.qa.lib.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Field;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum MetadataJiraClient {

    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final List<Field> fields = new ArrayList<>();

    public List<Field> getFields() {
        if (this.fields.isEmpty()) {
            try (JiraRestClient client = JiraRestClients.INSTANCE.getGeneralJiraRestClient()) {
                client.getMetadataClient().getFields().claim().forEach(
                    this.fields::add
                );
            } catch (RestClientException e) {
                LOGGER.log(Level.SEVERE, "Failed to get fields", e);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, JiraRestClients.FAILED_TO_CLOSE_HTTP_CLIENT_LOG_MESSAGE, e);
            }
        }
        return this.fields;
    }

}
