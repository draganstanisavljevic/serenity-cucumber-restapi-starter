package helpers;

public enum ExpectedText {
    NO_SEARCH_RESULT("Please enter a keyword to search for, then press the search button."),
    SIGN_IN("Sign In");

    private String value;

    ExpectedText(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static ExpectedText fromValue(String input) {
        for (ExpectedText b : ExpectedText.values()) {
            if (b.value.equals(input)) {
                return b;
            }
        }
        return null;
    }

}


