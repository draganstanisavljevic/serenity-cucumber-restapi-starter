package com.orgname.qa.lib.configuration;

import lombok.*;

/**
 * User constructor and user data that can be used.
 * User can be created only with all arguments, or without any arguments.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    /**
     * User name as a String
     */
    private String userName;

    /**
     * Password as a String
     */
    private String password;

    /**
     * Name as a String
     */
    private String name;
}
