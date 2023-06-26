package com.orgname.qa.lib.configuration;

import lombok.*;

/**
 * User constructor and user data that can be used.
 * User can be created only with all arguments, or without any arguments.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * User name as a String
     */
    private String userName;

    /**
     * Password as a String
     */
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Name as a String
     */
    private String name;
}
