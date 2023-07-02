package com.orgname.qa.lib.xray;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.io.File;
import java.io.FileNotFoundException;
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

public class ImportCucumberExecutionXrayClient {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final String IMPORT_EXECUTION_ENDPOINT_FORMAT = "%s/api/v2/import/execution/cucumber";
    private static final String IMPORT_EXECUTION_MP_ENDPOINT_FORMAT = "%s/api/v2/import/execution/cucumber/multipart";
    private static final int MULTIPART_FORM_DATA_BOUNDARY_MAX_BITS = 256;

    private final URI endpointUri;
    private final URI endpointMultipartUri;
    private final HttpClient httpClient;

    public ImportCucumberExecutionXrayClient() {
        this.endpointUri = URI.create(
                String.format(
                        IMPORT_EXECUTION_ENDPOINT_FORMAT, XrayConfig.CLOUD.getHost()
                )
        );
        this.endpointMultipartUri = URI.create(
                String.format(
                        IMPORT_EXECUTION_MP_ENDPOINT_FORMAT, XrayConfig.CLOUD.getHost()
                )
        );
        this.httpClient = HttpClient.newHttpClient();
    }

    public HttpResponse<String> importCucumberExecutionResults(File results) {
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(
                    getImportCucumberExecutionRequest(this.endpointUri, results),
                    HttpResponse.BodyHandlers.ofString()
            );
            LOGGER.log(
                    Level.INFO, "Import cucumber execution response status code: {0}", String.valueOf(
                            response.statusCode()
                    )
            );
            LOGGER.log(
                    Level.INFO, "Import cucumber execution response body: {0}", String.valueOf(
                            response.body()
                    )
            );
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to import cucumber execution", e);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Thread interrupted", e);
            Thread.currentThread().interrupt();
        }
        return response;
    }

    public HttpResponse<String> importCucumberExecutionResults(File testExecutionInfo, File results) {
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(
                    getImportCucumberExecutionRequest(
                            this.endpointMultipartUri, testExecutionInfo.toPath(), results.toPath()
                    ),
                    HttpResponse.BodyHandlers.ofString()
            );
            LOGGER.log(
                    Level.INFO, "Import cucumber execution response status code: {0}", String.valueOf(
                            response.statusCode()
                    )
            );
            LOGGER.log(
                    Level.INFO, "Import cucumber execution response body: {0}", String.valueOf(
                            response.body()
                    )
            );
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to import cucumber execution", e);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Thread interrupted", e);
            Thread.currentThread().interrupt();
        }
        return response;
    }

    private HttpRequest getImportCucumberExecutionRequest(URI endpoint, File results) {
        try {
            return HttpRequest.newBuilder(endpoint)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header(
                            HttpHeaders.AUTHORIZATION, String.format(
                                    "Bearer %s", AuthenticateXrayClient.INSTANCE.getToken()
                            )
                    )
                    .header(
                            HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()
                    )
                    .POST(
                            HttpRequest.BodyPublishers.ofFile(
                                    results.toPath()
                            )
                    )
                    .build();
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Results json file is not found", e);
            return null;
        }
    }

    private HttpRequest getImportCucumberExecutionRequest(URI endpoint, Path testExecutionInfo, Path results) {
        String boundary = new BigInteger(MULTIPART_FORM_DATA_BOUNDARY_MAX_BITS, new Random()).toString();
        try {
            return HttpRequest.newBuilder(endpoint)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header(
                            HttpHeaders.AUTHORIZATION, String.format(
                                    "Bearer %s", AuthenticateXrayClient.INSTANCE.getToken()
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
                                            "info", testExecutionInfo,
                                            "results", results
                                    ), boundary
                            )
                    )
                    .build();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read results file", e);
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

