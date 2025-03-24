package backend.academy.samples.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

class ConfigModule extends AbstractModule {
    @Provides
    @Singleton
    AppConfiguration dependentService() {
        // read from file etc
        return new AppConfiguration(3);
    }
}
