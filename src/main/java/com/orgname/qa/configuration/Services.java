package com.orgname.qa.configuration;

public enum Services {

    PET("pet_api"),
    STORE("store_api"),
    USER("user_api");

    private final String service;

    Services(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }
}
