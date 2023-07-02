package com.orgname.qa.lib.models.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {

    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("expires_in")
    public int expiresIn;

    @JsonProperty("token_type")
    public String tokenType;

    public String scope;
}
