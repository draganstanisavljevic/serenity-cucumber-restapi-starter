package com.orgname.qa.lib.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orgname.qa.lib.helpers.JsonHelper;
import com.orgname.qa.lib.properties.PropertiesSupplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UsersProvider {
    private static Map<String, User> users;

    /**
     * Gets user by user type
     * @param userType as a String. You can find userType in the list of users.
     */
    public static User getUser(final String userType) {
        String env = PropertiesSupplier.tryGetProperty("environment");
        String filePath = (env == null || env.startsWith("http") || env.equals("test"))
                ? "config/users.json" : String.format("config/users.%s.json", env);
        if (users == null) {
            TypeReference<Map<String, User>> typeReference = new TypeReference<>() {
            };
            users = JsonHelper.readItemFromJsonFile(filePath, typeReference);
        }
        User user = users.get(userType);
        Assertions.assertThat(user).as(String.format("%s does not contain %s user", filePath, userType)).isNotNull();
        return user;
    }
}
