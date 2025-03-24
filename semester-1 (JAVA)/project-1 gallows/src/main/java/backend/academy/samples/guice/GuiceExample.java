package backend.academy.samples.guice;

import com.google.inject.Injector;
import com.google.inject.Stage;
import lombok.extern.log4j.Log4j2;
import static com.google.inject.Guice.createInjector;
import static com.google.inject.util.Modules.override;

/**
 * Google Guice is a type-safe <a href="https://en.wikipedia.org/wiki/Dependency_injection">Dependency Injection</a> framework.
 * <p>
 * <a href="https://github.com/google/guice/wiki/">Library documentation</a>
 */
@Log4j2
public class GuiceExample {
    public static void main(String[] args) {
        Injector injector = createInjector(Stage.PRODUCTION, new ConfigModule())
            .createChildInjector(
                override(
                    new BaseAppModule()
                ).with(new OverrideModule())
            );

        Service instance = injector.getInstance(Service.class);

        log.info(instance.result());
    }
}
