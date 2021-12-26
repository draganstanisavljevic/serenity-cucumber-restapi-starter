package lib.exceptions;

/**
 * To be used to re-throw an error captured in catch section of try-catch block with custom error message.
 */
public class TestErrorException extends RuntimeException {
    public TestErrorException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
