package com.orgname.qa.lib.models.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TokenPayload {

    @JsonProperty("grant_type")
    public String grantType;

    @JsonProperty("client_id")
    public String clientId;

    @JsonProperty("client_secret")
    public String clientSecret;

    private String audience;
    private String scope;
    private String realm;
    private String username;
    private String password;
}
