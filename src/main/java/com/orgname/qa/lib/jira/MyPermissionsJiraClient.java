package com.orgname.qa.lib.jira;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Permissions;
import com.atlassian.jira.rest.client.api.domain.input.MyPermissionsInput;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum MyPermissionsJiraClient {

    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final Map<String, Permissions> permissionsByProjectKey = new HashMap<>();

    public Permissions getMyPermissions(Set<String> permissions, String projectKey) {
        if (!this.permissionsByProjectKey.containsKey(projectKey)) {
            try (ExtendedMyPermissionsRestClient client =
                         JiraRestClients.INSTANCE.getExtendedMyPermissionsRestClient()) {
                Permissions perms = client.getMyPermissions(
                                permissions, MyPermissionsInput.withProject(projectKey)
                        )
                        .claim();
                this.permissionsByProjectKey.put(projectKey, perms);

            } catch (RestClientException e) {
                LOGGER.log(Level.SEVERE, "Failed to get my permissions", e);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, JiraRestClients.FAILED_TO_CLOSE_HTTP_CLIENT_LOG_MESSAGE, e);
            }
        }
        return this.permissionsByProjectKey.get(projectKey);
    }
}

