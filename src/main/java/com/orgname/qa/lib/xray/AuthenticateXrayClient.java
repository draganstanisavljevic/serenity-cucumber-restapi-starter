package com.orgname.qa.lib.xray;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum AuthenticateXrayClient {

    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final String AUTHENTICATE_ENDPOINT_FORMAT = "%s/api/v2/authenticate";

    private final URI endpointUri;
    private final HttpClient httpClient;

    private String token;

    AuthenticateXrayClient() {
        this.endpointUri = URI.create(
                String.format(
                        AUTHENTICATE_ENDPOINT_FORMAT, XrayConfig.CLOUD.getHost()
                )
        );
        this.httpClient = HttpClient.newHttpClient();
    }

    public synchronized String getToken() {
        if (token == null) {
            try {
                HttpResponse<String> response = httpClient.send(
                        getTokenRequest(
                                this.endpointUri, XrayConfig.CLOUD.getClientId(), XrayConfig.CLOUD.getClientSecret()
                        ),
                        HttpResponse.BodyHandlers.ofString()
                );
                LOGGER.log(
                        Level.INFO, "Authenticate response status code: {0}", String.valueOf(
                                response.statusCode()
                        )
                );
                token = extractToken(response);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to authenticate to Xray", e);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Thread interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
        return token;
    }

    private HttpRequest getTokenRequest(URI endpointUri, String clientId, String clientSecret) {
        return HttpRequest.newBuilder(endpointUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header(
                        HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()
                )
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                String.format("{\"client_id\":\"%s\",\"client_secret\":\"%s\"}", clientId, clientSecret)
                        )
                )
                .build();
    }

    private String extractToken(HttpResponse<String> response) {
        return response.body().replace("\"", "");
    }
}

