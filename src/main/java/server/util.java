package server;

import org.apache.commons.text.RandomStringGenerator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

/**
 * Contains utility methods - as the name implies!
 */
public class util {

    /**
     * This method converts a `List<<CompletableFuture<T>>` to `CompletableFuture<List<T>>`
     * @param futuresList A list of CompletableFuture objects
     * @return This method returns a `List<<CompletableFuture<T>>` converted from the `futuresList` parameter
     */
    public static <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futuresList) {
        CompletableFuture<Void> allFuturesResult =
                CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0]));

        return allFuturesResult.thenApply(v ->
                futuresList.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
        );
    }

    /**
     * @param length The length of the returned string
     * @return This method returns an alphanumeric string of the length specified
     */
    public static String randomAlphanumericString(final int length)
    {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(LETTERS, DIGITS)
                .build();

        return generator.generate(length);
    }

    /**
     * @param length The length of the returned string
     * @return This method returns a numeric string of the length specified
     */
    public static String randomNumericString(final int length)
    {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', '9')
                .filteredBy(DIGITS)
                .build();

        return generator.generate(length);
    }
}
