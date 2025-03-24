package backend.academy.samples;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIOException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ExceptionsTest {
    @Test
    public void testException1() {
        assertThatThrownBy(() -> {
            throw new Exception("boom!");
        }).isInstanceOf(Exception.class)
            .hasMessageContaining("boom");
    }

    @Test
    public void testException2() {
        assertThatExceptionOfType(IOException.class).isThrownBy(() -> {
                throw new IOException("boom!");
            })
            .withMessage("%s!", "boom")
            .withMessageContaining("boom")
            .withNoCause();
    }

    @Test
    public void testException3() {
        assertThatIOException().isThrownBy(() -> {
                throw new IOException("boom!");
            })
            .withMessage("%s!", "boom")
            .withMessageContaining("boom")
            .withNoCause();
    }

    @Test
    public void testException4() {
        assertThatCode(() -> {
        }).doesNotThrowAnyException();
    }

    @Test
    public void testException5() {
        // given some preconditions

        // when
        Throwable thrown = catchThrowable(() -> {
            throw new Exception("boom!");
        });

        // then
        assertThat(thrown).isInstanceOf(Exception.class)
            .hasMessageContaining("boom");
    }
}
