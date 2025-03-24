package backend.academy.samples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Month;
import java.util.stream.Stream;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeout;

/**
 * The most popular Java framework for writing tests.
 * <p>
 * <a href="https://junit.org/junit5/docs/current/user-guide/">Library documentation</a>
 */
public class Junit5ExamplesTest {
    @Nested
    class ParametrizedTests {
        @ParameterizedTest
        @ValueSource(ints = {1, 3, 5, -3, 15, Integer.MAX_VALUE}) // six numbers
        void isOdd_ShouldReturnTrueForOddNumbers(int number) {
            assertThat(number).isOdd();
        }

        @ParameterizedTest
        @EnumSource(Month.class)
        void getValueForAMonth_IsAlwaysBetweenOneAndTwelve(Month month) {
            int monthNumber = month.getValue();
            assertThat(monthNumber).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(12);
        }

        @ParameterizedTest
        @CsvSource({"test,TEST", "tEst,TEST", "Java,JAVA"})
        void toUpperCase_ShouldGenerateTheExpectedUppercaseValue(String input, String expected) {
            String actualValue = input.toUpperCase();
            assertThat(actualValue).isEqualTo(expected);
        }

        record MyData(String data) {
        }

        private static Stream<MyData> provideData() {
            return Stream.of(
                new MyData("Data 1"),
                new MyData("Data 2"),
                new MyData("Data 3")
            );
        }

        @ParameterizedTest
        @MethodSource("provideData")
        void validateData(MyData myData) {
            assertThat(myData.data()).isNotNull();
        }

        private static Stream<Arguments> provideStringsForIsBlank() {
            return Stream.of(
                Arguments.of(null, true),
                Arguments.of("", true),
                Arguments.of("  ", true),
                Arguments.of("not blank", false)
            );
        }

        @ParameterizedTest
        @MethodSource("provideStringsForIsBlank")
        void isBlank_ShouldReturnTrueForNullOrBlankStrings(String input, boolean expected) {
            assertThat(Strings.isBlank(input)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "{index} {0} is 30 days long")
        @EnumSource(value = Month.class, names = {"APRIL", "JUNE", "SEPTEMBER", "NOVEMBER"})
        void someMonths_Are30DaysLong(Month month) {
            final boolean isALeapYear = false;
            assertThat(month.length(isALeapYear)).isEqualTo(30);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void isBlank_ShouldReturnTrueForAllTypesOfBlankStrings(String input) {
            assertThat(Strings.isBlank(input)).isTrue();
        }
    }

    @Nested
    class TemporaryDirectory {
        @TempDir
        Path tmpDir;

        @Test
        public void tempDirV1_use_field() throws IOException {
            Path file = tmpDir.toAbsolutePath().resolve("temp.txt");
            Files.write(file, "ABCD".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            assertThat(new String(Files.readAllBytes(file))).isEqualTo("ABCD");
        }

        @Test
        public void tempDirV2_inject_as_argument(@TempDir Path argumentTempDir) throws IOException {
            Path file = argumentTempDir.toAbsolutePath().resolve("temp.txt");
            Files.write(file, "ABCD".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            assertThat(new String(Files.readAllBytes(file))).isEqualTo("ABCD");
        }
    }

    @Nested
    class NicheFeatures {

        @Test
        @DisplayName("Custom test name")
        public void testName() {
        }

        @Test
        @EnabledOnOs(OS.LINUX)
        public void enableOnLinux() {
        }

        @Test
        public void simpleTimeout() {
            int result = assertTimeout(Duration.ofMinutes(2), () -> 1 + 1);
            assertThat(result).isEqualTo(2);
        }

        @Test
        void testInfo(TestInfo testInfo) {
            String displayName = testInfo.getDisplayName();
            assertThat(displayName).isEqualTo("testInfo(TestInfo)");
        }

        @RepeatedTest(10)
        @DisplayName("repetition")
        void repetitionTest(RepetitionInfo repetitionInfo) {
            assertThat(repetitionInfo.getTotalRepetitions()).isEqualTo(10);
        }
    }
}
