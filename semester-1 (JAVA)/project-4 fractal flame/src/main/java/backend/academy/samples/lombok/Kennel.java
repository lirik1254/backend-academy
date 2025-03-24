package backend.academy.samples.lombok;

import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;

@Log4j2
@Builder
@ToString
public class Kennel {
    private @NonNull Integer id;
    private String name;
    @Singular
    private Set<Dog> dogs;

    record Dog(
        @NonNull Integer id,
        @NonNull String name,
        @NonNull String breed
    ) {
    }

    public static void main(String[] args) {
        Kennel kennel = Kennel.builder()
            .id(1)
            .name("Bazz")
            .dog(new Dog(1, "Foo", "Bar"))
            .build();

        log.info(kennel);

        kennel.dogs.forEach(_ -> {
            try {
                NonInstantiatableUtilityClass.throwException();
            } catch (Exception e) {
                log.info("Caught exception inside lambda!", e);
            }
        });

        try {
            kennel.dogs.forEach(_ -> NonInstantiatableUtilityClass.sneakyThrowCheckedException()); // no compilation error
        } catch (Exception e) {
            log.info("Caught exception from outside lambda!", e);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static class NonInstantiatableUtilityClass {

        static void throwException() throws Exception {
            throw new Exception("Checked exception occurred!");
        }

        // usually you must avoid this, use only as a last resort!
        @SneakyThrows
        static void sneakyThrowCheckedException() { // notice no "throws Exception" here
            throw new Exception("Checked exception occurred!");
        }
    }
}
