package backend.academy.samples;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Mockito is a mocking framework for Java. Mockito allows convenient creation of substitutes of real objects for testing purposes.
 * <p>
 * <a href="https://site.mockito.org">Library website</a>
 * <a href="https://github.com/mockito/mockito/wiki/FAQ">Library documentation</a>
 */
@ExtendWith(MockitoExtension.class)
public class MockitoExamplesTest {
    public interface Repository {
        int count(int a, int b);
    }

    @RequiredArgsConstructor
    public static class Service {
        private final Repository repository;

        public int calculate(int a) {
            return repository.count(a, 10);
        }
    }

    public static class FibonacciService {
        long fib(int n) {
            if (n <= 2) return n;
            return fib(n - 1) + fib(n - 2);
        }
    }

    @Mock
    private Repository repository;
    @InjectMocks
    private Service service;
    // same as
    // private Repository mock2 = mock(Repository.class);
    // private Service service2 = new Service(mock2);

    @Spy
    private FibonacciService fibonacciService = new FibonacciService();
    // same as
    // private FibonacciService fibonacciService2 = spy(new FibonacciService());

    @Test
    void mockExampleV1_inject_field() {
        when(repository.count(anyInt(), eq(2))).thenReturn(10);
        when(repository.count(anyInt(), eq(3))).thenReturn(20);

        assertThat(repository.count(0, 2)).isEqualTo(10);
        assertThat(repository.count(0, 3)).isEqualTo(20);
    }

    @Test
    void mockExampleV2_inject_argument(@Mock Repository repository) {
        when(repository.count(anyInt(), eq(2))).thenReturn(10);
        when(repository.count(anyInt(), eq(3))).thenReturn(20);

        assertThat(repository.count(0, 2)).isEqualTo(10);
        assertThat(repository.count(0, 3)).isEqualTo(20);
    }

    @Test
    void verifyExample() {
        service.calculate(10);
        verify(repository, only()).count(anyInt(), eq(10));
    }

    @Test
    void spyExample() {
        fibonacciService.fib(5);
        verify(fibonacciService).fib(5);
        verify(fibonacciService).fib(4);
        verify(fibonacciService, times(2)).fib(3);
        verify(fibonacciService, times(3)).fib(2);
    }
}
