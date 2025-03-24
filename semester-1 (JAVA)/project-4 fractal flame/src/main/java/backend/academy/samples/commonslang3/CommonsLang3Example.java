package backend.academy.samples.commonslang3;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * Apache Commons Lang3 provides a lot of helpful utilities.
 * <p>
 * <a href="https://commons.apache.org/proper/commons-lang/">Library website</a>
 */
@Log4j2
public class CommonsLang3Example {
    public static void main(String[] args) throws Exception {
        log.info(
            "Current OS: {}; Java Version: {}; System file encoding: {}",
            SystemUtils.OS_NAME,
            SystemUtils.JAVA_VERSION,
            SystemUtils.FILE_ENCODING
        );
        var someString = " Hello World ";
        log.info("Original string: '{}'", someString);
        log.info("isBlank: '{}'", StringUtils.isBlank(someString));
        log.info("Trimmed: '{}'", StringUtils.trim(someString));
        log.info("Reversed: '{}'", StringUtils.reverse(someString));
        log.info("Abbreviated: '{}'", StringUtils.abbreviate(someString, 10));

        var numbersString = "12345";
        var invalidNumbersString = "123abc";
        log.info("Converted into number string: {}", NumberUtils.toInt(numbersString));
        log.info("Converted into number invalid string: {}", NumberUtils.toInt(invalidNumbersString));

        int[] numbersArray = {1, 2, 3, 4, 5};
        log.info(
            "Max value in array: {}, min value in array: {}",
            NumberUtils.max(numbersArray),
            NumberUtils.min(numbersArray)
        );

        int[] extendedNumbersArray = ArrayUtils.addAll(numbersArray, 6, 7, 8);
        log.info("Extended numbers array: {}", ArrayUtils.toString(extendedNumbersArray));
        ArrayUtils.shuffle(extendedNumbersArray);
        log.info("Shuffled array: {}", ArrayUtils.toString(extendedNumbersArray));
        ArrayUtils.reverse(extendedNumbersArray);
        log.info("Reversed array: {}", ArrayUtils.toString(extendedNumbersArray));

        try {
            int invalidNumber = -5;

            Validate.isTrue(invalidNumber > 0, "Number must be positive");
        } catch (IllegalArgumentException e) {
            log.info("Validation of number result: '{}'", e.getMessage());
        }

        var randomString = (String) MethodUtils.invokeStaticMethod(CommonsLang3Example.class, "getRandomString");
        log.info("Random string obtained using reflection: '{}'", randomString);
    }

    public static String getRandomString() {
        return RandomStringUtils.randomAlphanumeric(6);
    }
}
