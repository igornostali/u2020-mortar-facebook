package co.lemonlabs.mortar.example.ui;

import javax.inject.Singleton;

import co.lemonlabs.mortar.example.ui.debug.DebugAppContainer;
import co.lemonlabs.mortar.example.ui.debug.SocketActivityHierarchyServer;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = DebugAppContainer.class,
        complete = false,
        library = true,
        overrides = true
)
public class DebugUiModule {
    @Provides
    @Singleton
    AppContainer provideAppContainer(DebugAppContainer debugAppContainer) {
        return debugAppContainer;
    }

    @Provides
    @Singleton
    ActivityHierarchyServer provideActivityHierarchyServer() {
        return new SocketActivityHierarchyServer();
    }
}
