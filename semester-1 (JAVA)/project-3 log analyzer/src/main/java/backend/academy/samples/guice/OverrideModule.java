package backend.academy.samples.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class OverrideModule extends AbstractModule {
    @Provides
    @Singleton
    Service service(DependentService ds) {
        return () -> 2000 * ds.result() / 5;
    }
}
