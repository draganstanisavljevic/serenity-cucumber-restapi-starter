package com.orgname.qa.lib.jira;

import com.atlassian.jira.rest.client.api.domain.Permissions;
import com.atlassian.jira.rest.client.api.domain.input.MyPermissionsInput;
import io.atlassian.util.concurrent.Promise;

import java.io.Closeable;
import java.util.Set;

public interface ExtendedMyPermissionsRestClient extends Closeable {

    Promise<Permissions> getMyPermissions(Set<String> permissions, MyPermissionsInput permissionInput);
}
