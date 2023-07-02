package com.orgname.qa.lib.cucumber;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class FeatureFile {

    private static final String FILE_URI_SCHEMA = "file";
    private static final String CLASSPATH_URI_SCHEMA = "classpath";

    private final URI featureFileUri;

    public FeatureFile(URI featureFileUri) {
        this.featureFileUri = featureFileUri;
    }

    public File getFile() {
        File featureFile;
        String featureFileUriScheme = this.featureFileUri.getScheme();
        if (FILE_URI_SCHEMA.equals(featureFileUriScheme)) {
            featureFile = getFilesystemFile();
        } else if (CLASSPATH_URI_SCHEMA.equals(featureFileUriScheme)) {
            featureFile = getClasspathFile();
        } else {
            throw new IllegalArgumentException("Cannot get feature file from URI");
        }
        return featureFile;
    }

    private File getFilesystemFile() {
        try {
            return new File(
                    this.featureFileUri.toURL().getPath()
            );
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Cannot convert feature file URI to URL", e);
        }
    }

    private File getClasspathFile() {
        String schemeSpecificPart = this.featureFileUri.getSchemeSpecificPart();
        if (schemeSpecificPart.startsWith("/")) {
            schemeSpecificPart = schemeSpecificPart.substring(1);
        }
        try {
            return new File(
                    Thread.currentThread().getContextClassLoader().getResource(schemeSpecificPart).toURI()
            );
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Cannot convert feature file URL to URI", e);
        }
    }
}

