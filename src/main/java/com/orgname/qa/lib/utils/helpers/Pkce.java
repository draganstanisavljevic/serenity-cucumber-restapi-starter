package com.orgname.qa.lib.utils.helpers;

import com.orgname.qa.lib.utils.exceptions.AzureAuthorizationLoginException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Pkce {

    /**
     * The default entropy (in bytes) used for the code verifier.
     */
    public static final int DEFAULT_CODE_VERIFIER_ENTROPY = 64;
    private static String codeVerifier;
    private static String codeVerifierChallenge;

    String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifierValue = new byte[DEFAULT_CODE_VERIFIER_ENTROPY];
        secureRandom.nextBytes(codeVerifierValue);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifierValue);
    }

    String driveCodeVerifierChallenge(String codeVerifier) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {
        MessageDigest sha256Digester = MessageDigest.getInstance("SHA-256");
        sha256Digester.update(codeVerifier.getBytes("ISO_8859_1"));
        byte[] digestBytes = sha256Digester.digest();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digestBytes);
    }

    public static String getCodeVerifier() throws AzureAuthorizationLoginException {
        try {
            if (codeVerifier == null) {
                Pkce pkce = new Pkce();
                codeVerifier = pkce.generateCodeVerifier();
                codeVerifierChallenge = pkce.driveCodeVerifierChallenge(codeVerifier);
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new AzureAuthorizationLoginException("Error Message", ex);
        }
        return codeVerifier;
    }

    public static String getCodeVerifierChallenge() throws AzureAuthorizationLoginException {
        try {
            if (codeVerifierChallenge == null) {
                Pkce pkce = new Pkce();
                codeVerifier = pkce.generateCodeVerifier();
                codeVerifierChallenge = pkce.driveCodeVerifierChallenge(codeVerifier);
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new AzureAuthorizationLoginException("Error Message", ex);
        }
        return codeVerifierChallenge;
    }
}

