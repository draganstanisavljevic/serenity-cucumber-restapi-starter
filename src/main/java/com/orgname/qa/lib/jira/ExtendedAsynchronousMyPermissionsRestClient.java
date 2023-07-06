package com.orgname.qa.lib.jira;

import com.atlassian.jira.rest.client.api.domain.Permissions;
import com.atlassian.jira.rest.client.api.domain.input.MyPermissionsInput;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.jira.rest.client.internal.json.PermissionsJsonParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

public class ExtendedAsynchronousMyPermissionsRestClient
    extends AbstractAsynchronousRestClient implements ExtendedMyPermissionsRestClient {

    private static final String URI_PREFIX = "mypermissions";
    private final URI baseUri;
    private final PermissionsJsonParser permissionsJsonParser = new PermissionsJsonParser();

    public ExtendedAsynchronousMyPermissionsRestClient(final URI baseUri, final DisposableHttpClient client) {
        super(client);
        this.baseUri = baseUri;
    }

    @Override
    public Promise<Permissions> getMyPermissions(Set<String> permissions, MyPermissionsInput permissionInput) {
        final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path(URI_PREFIX);
        addRequiredParam(uriBuilder, permissions);
        addOptionalParams(uriBuilder, permissionInput);
        return getAndParse(uriBuilder.build(), permissionsJsonParser);
    }

    private void addRequiredParam(UriBuilder uriBuilder, Set<String> permissions) {
        if (permissions != null && !permissions.isEmpty()) {
            uriBuilder.queryParam("permissions", String.join(",", permissions));
        }
    }

    private void addOptionalParams(UriBuilder uriBuilder, MyPermissionsInput permissionInput) {
        if (permissionInput != null) {
            if (permissionInput.getProjectKey() != null) {
                uriBuilder.queryParam("projectKey", permissionInput.getProjectKey());
            }
            if (permissionInput.getProjectId() != null) {
                uriBuilder.queryParam("projectId", permissionInput.getProjectId());
            }
            if (permissionInput.getIssueKey() != null) {
                uriBuilder.queryParam("issueKey", permissionInput.getIssueKey());
            }
            if (permissionInput.getIssueId() != null) {
                uriBuilder.queryParam("issueId", permissionInput.getIssueId());
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            ((DisposableHttpClient) client()).destroy();
        } catch (Exception e) {
            throw (e instanceof IOException) ? ((IOException) e) : new IOException(e);
        }
    }
}
