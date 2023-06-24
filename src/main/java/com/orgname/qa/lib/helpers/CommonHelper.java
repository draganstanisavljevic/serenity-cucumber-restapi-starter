package com.orgname.qa.lib.helpers;

import com.orgname.qa.lib.exceptions.TestErrorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.serenitybdd.core.Serenity;

import org.awaitility.Durations;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static net.thucydides.core.webdriver.ThucydidesWebDriverSupport.getDriver;
import static org.awaitility.Awaitility.await;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CommonHelper {
    private static final String ONLY_NUMBERS_REGEX = "[^0-9]";
    private static final Pattern PATTERN_NUMBERS = Pattern.compile(ONLY_NUMBERS_REGEX);
    private static final String CAPITALIZED_WORD_REGEX = "([A-ZŠ][a-zëé]*)";
    private static final String SEPARATORS_REGEX = "[-\\s+]";
    private static final Pattern PATTERN_SEPARATORS = Pattern.compile(SEPARATORS_REGEX);
    public static final String VIN = "VIN";
    public static final Duration DEFAULT_TIMEOUT = Durations.TEN_SECONDS;

    /**
     * This method sets unique VIN
     */
    public static void setUniqueVin() {
        String vin = String.format("TEST%013X", System.nanoTime());
        Serenity.setSessionVariable(VIN).to(vin);
    }

    /**
     * This method generates unique VIN
     */
    public static void generateUniqueVin() {
        if (!Serenity.hasASessionVariableCalled(VIN)) {
            setUniqueVin();
        }
    }

    /**
     * This method extract Int number form the String value
     */
    public static int extractNumberFromString(final String text) {
        String number = PATTERN_NUMBERS.matcher(text).replaceAll("");
        return Integer.parseInt(number);
    }

    /**
     * @return whether all elements of this stream match the provided predicate.
     */
    public static boolean isTextCapitalised(final String text) {
        String[] words = PATTERN_SEPARATORS.split(text);
        return Arrays.stream(words).allMatch(word -> Pattern.matches(CAPITALIZED_WORD_REGEX, word));
    }

    /**
     * @return a random string with a specified length.
     */
    public static String generateRandomNumberString(final int length) {
        return UUID.randomUUID().toString().substring(0, length);
    }

    /**
     * This method waits till the condition returns true. Waiting happens on separate thread.
     */
    public static void waitForCondition(final int maxWaitSec, final int waitIntervalSec,
                                        final Callable<Boolean> condition, final String errorMessage) {
        try {
            await().timeout(maxWaitSec, TimeUnit.SECONDS)
                    .pollInterval(waitIntervalSec, TimeUnit.SECONDS)
                    .until(condition);
        } catch (ConditionTimeoutException cte) {
            throw new TestErrorException(errorMessage, cte);
        }
    }

    public static void waitForCondition(final int maxWaitSec, final int waitIntervalSec,
                                        final Callable<Boolean> condition) {
        waitForCondition(maxWaitSec, waitIntervalSec, condition, String.format("Condition remains false for %s seconds",
                maxWaitSec));
    }

    /**
     * This method waits until the condition returns true. Waiting happens on same thread.
     * This method simplifies getting access within condition lambda function to Serenity variable for example.
     * This case would be complicated to achieve when using waitForCondition that pools on separate thread.
     * This wait method will be useful in most cases for test automation code due to the fact that test code
     * is synchronous.
     *
     * @param maxWaitSec        Timeout to keep polling (in seconds) before it gives up and throws the exception.
     * @param waitIntervalSec   Pooling interval (in seconds).
     * @param condition         Condition evaluator. When condition is true pooling stops.
     * @param errorMessage      User friendly error message to display when condition is not met within
     * {@code maxWaitSec}.
     */
    public static void waitForConditionInSameThread(final int maxWaitSec, final int waitIntervalSec,
                                                    final Callable<Boolean> condition, final String errorMessage) {
        try {
            await().timeout(maxWaitSec, TimeUnit.SECONDS)
                    .pollInterval(waitIntervalSec, TimeUnit.SECONDS)
                    .pollInSameThread()
                    .until(condition);
        } catch (ConditionTimeoutException cte) {
            throw new TestErrorException(errorMessage, cte);
        }
    }

    /**
     * Same as {@link #waitForConditionInSameThread(int, int, Callable, String)}, but it starts to pool only after
     * {@code delayInSeconds} elapses.
     *
     * @param delayInSeconds    Initial delay (in seconds) before the pooling starts.
     * @param maxWaitSec        Timeout to keep polling (in seconds) before it gives up and throws the exception.
     * @param waitIntervalSec   Pooling interval (in seconds).
     * @param condition         Condition evaluator. When condition is true pooling stops.
     * @param errorMessage      User friendly error message to display when condition is not met within
     * {@code maxWaitSec}.
     */
    public static void waitForConditionInSameThreadWithDelay(final int delayInSeconds,
                                                             final int maxWaitSec,
                                                             final int waitIntervalSec,
                                                             Callable<Boolean> condition, String errorMessage) {
        try {
            await().timeout(maxWaitSec, TimeUnit.SECONDS)
                    .pollInSameThread()
                    .pollDelay(delayInSeconds, TimeUnit.SECONDS)
                    .pollInterval(waitIntervalSec, TimeUnit.SECONDS)
                    .until(condition);
        } catch (
                ConditionTimeoutException cte) {
            throw new TestErrorException(errorMessage, cte);
        }
    }

    /**
     * Waits for {@code durationInSeconds} seconds before resuming test execution. Should be only be used when
     * waiting with pooling like
     * {@link #waitForConditionInSameThread(int, int, Callable, String)},
     * {@link #waitForConditionInSameThreadWithDelay(int, int, int, Callable, String)},
     * {@link #waitForCondition(int, int, Callable, String)},
     * {@link #waitForCondition(int, int, Callable)},
     * are not possible.
     *
     * @param durationInSeconds Time (in seconds) to wait before resuming execution.
     * @param errorMessage      User friendly error message to display when condition is not met within
     * {@code durationInSeconds}.
     */
    public static void waitForDuration(final int durationInSeconds, String errorMessage) {
        final long MILLISECONDS_IN_SECOND = 1000L;
        try {
            Thread.sleep(durationInSeconds * MILLISECONDS_IN_SECOND);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TestErrorException(errorMessage, e);
        }
    }

    public void waitForConditiontWithDelay(final String message, final Duration pollInterval, final Duration timeout,
                                           final ExpectedCondition<WebElement> condition) {
        Wait<WebDriver> wait = new FluentWait<>(getDriver())
                .withTimeout(timeout)
                .pollingEvery(pollInterval)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .withMessage(message);

        wait.until(condition);

    }

    public void waitForConditiontWithDelay(final String message, final Duration pollInterval,
                                           final ExpectedCondition<WebElement> condition) {
        waitForConditiontWithDelay(message, pollInterval, DEFAULT_TIMEOUT, condition);
    }

    public void waitForConditiontWithDelay(final String message, final ExpectedCondition<WebElement> condition) {
        waitForConditiontWithDelay(message, Durations.ONE_SECOND, DEFAULT_TIMEOUT, condition);
    }

    public void waitForPageLoad(int timeOutInSeconds) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(timeOutInSeconds))
                .until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")
                        .equals("complete"));
    }

    public void waitUntilElementDoesNotExist(By element) {
        new WebDriverWait(getDriver(), Durations.TEN_SECONDS )
                .until(ExpectedConditions.invisibilityOfElementLocated(element));
    }
}

