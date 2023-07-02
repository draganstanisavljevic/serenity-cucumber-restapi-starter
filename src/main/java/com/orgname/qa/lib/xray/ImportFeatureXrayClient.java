package com.orgname.qa.lib.xray;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportFeatureXrayClient {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final String IMPORT_FEATURE_ENDPOINT_FORMAT = "%s/api/v2/import/feature?projectKey=%s";
    private static final int MULTIPART_FORM_DATA_BOUNDARY_MAX_BITS = 256;

    private final URI endpointUri;
    private final HttpClient httpClient;

    public ImportFeatureXrayClient(String jiraProjectKey) {
        this.endpointUri = URI.create(
                String.format(
                        IMPORT_FEATURE_ENDPOINT_FORMAT, XrayConfig.CLOUD.getHost(), jiraProjectKey
                )
        );
        this.httpClient = HttpClient.newHttpClient();
    }

    public HttpResponse<String> importFeature(File featureFile) {
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(
                    getImportFeatureRequest(
                            this.endpointUri, AuthenticateXrayClient.INSTANCE.getToken(), featureFile.toPath()
                    ),
                    HttpResponse.BodyHandlers.ofString()
            );
            LOGGER.log(
                    Level.INFO, "Import feature response status code: {0}", String.valueOf(
                            response.statusCode()
                    )
            );
            LOGGER.log(
                    Level.INFO, "Import feature response body: {0}", String.valueOf(
                            response.body()
                    )
            );
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to import feature", e);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Thread interrupted", e);
            Thread.currentThread().interrupt();
        }
        return response;
    }

    private HttpRequest getImportFeatureRequest(URI endpointUri, String token, Path featureFilePath) {
        String boundary = new BigInteger(MULTIPART_FORM_DATA_BOUNDARY_MAX_BITS, new Random()).toString();
        try {
            return HttpRequest.newBuilder(endpointUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header(
                            HttpHeaders.AUTHORIZATION, String.format(
                                    "Bearer %s", token
                            )
                    )
                    .header(
                            HttpHeaders.CONTENT_TYPE, String.format(
                                    "%s;boundary=%s", ContentType.MULTIPART_FORM_DATA.getMimeType(), boundary
                            )
                    )
                    .POST(
                            ofMultipartForm(
                                    Map.of(
                                            "file", featureFilePath
                                    ), boundary
                            )
                    )
                    .build();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read feature file", e);
            return null;
        }
    }

    private HttpRequest.BodyPublisher ofMultipartForm(Map<Object, Object> data, String boundary) throws IOException {
        List<byte[]> byteArrays = new ArrayList<>();
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
                .getBytes(StandardCharsets.UTF_8);
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            byteArrays.add(separator);

            if (entry.getValue() instanceof Path) {
                Path path = (Path) entry.getValue();
                String mimeType = Files.probeContentType(path);
                byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName()
                        + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n")
                        .getBytes(StandardCharsets.UTF_8));
                byteArrays.add(Files.readAllBytes(path));
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            } else {
                byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n")
                        .getBytes(StandardCharsets.UTF_8));
            }
        }
        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }
}

