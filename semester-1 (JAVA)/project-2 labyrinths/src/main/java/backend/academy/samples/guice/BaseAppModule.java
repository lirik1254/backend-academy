package backend.academy.samples.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

class BaseAppModule extends AbstractModule {
    @Provides
    @Singleton
    DependentService dependentService(AppConfiguration appConfiguration) {
        return appConfiguration::serviceConfigParameter;
    }

    @Provides
    @Singleton
    Service service(DependentService ds) {
        return () -> 1337 * ds.result();
    }
}
