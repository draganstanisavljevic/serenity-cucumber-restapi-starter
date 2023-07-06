package com.orgname.qa.lib.models.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class ChangePassword {
    @JsonAlias("client_id")
    private String clientId;
    private String email;
    private String connection;
}
