package lib.assertions;

public class AssertionMessages {

    public static String isNull(final String field) {
        return String.format("%s is null", field);
    }

    public static String isNotNull(final String field) {
        return String.format("%s is not null", field);
    }

    public static String doesNotMatch(final String field) {
        return String.format("%s does not match", field);
    }

    public static String isNotCorrect(final String field) {
        return String.format("%s is not correct", field);
    }

    public static String isEmpty(final String field) {
        return String.format("%s is empty", field);
    }

    public static String isNotEmpty(final String field) {
        return String.format("%s is not empty", field);
    }

    public static String isFound(final String field) {
        return String.format("%s has been found", field);
    }

    public static String isNotFound(final String field) {
        return String.format("%s has not been found", field);
    }

    public static String isBlank(final String field) {
        return String.format("%s is blank", field);
    }
}
