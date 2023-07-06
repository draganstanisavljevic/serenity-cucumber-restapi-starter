package com.orgname.qa.lib.utils.exceptions;

public class AzureAuthorizationLoginException extends Exception {
    public AzureAuthorizationLoginException(String errorMessage, Throwable ex) {
        super(errorMessage, ex);
    }
}
