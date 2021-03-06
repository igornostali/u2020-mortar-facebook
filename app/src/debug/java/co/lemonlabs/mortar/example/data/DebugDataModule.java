package co.lemonlabs.mortar.example.data;

import android.app.Application;
import android.content.SharedPreferences;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import co.lemonlabs.mortar.example.data.api.DebugApiModule;
import co.lemonlabs.mortar.example.data.prefs.BooleanPreference;
import co.lemonlabs.mortar.example.data.prefs.IntPreference;
import co.lemonlabs.mortar.example.data.prefs.StringPreference;
import dagger.Module;
import dagger.Provides;
import retrofit.MockRestAdapter;

@Module(
    includes = DebugApiModule.class,
    complete = false,
    library = true,
    overrides = true,
    injects = {
        IdlingDownloaderWrapper.class
    }
)
public final class DebugDataModule {
  private static final int DEFAULT_ANIMATION_SPEED = 1; // 1x (normal) speed.
  private static final boolean DEFAULT_PICASSO_DEBUGGING = false; // Debug indicators displayed
  private static final boolean DEFAULT_PIXEL_GRID_ENABLED = false; // No pixel grid overlay.
  private static final boolean DEFAULT_PIXEL_RATIO_ENABLED = false; // No pixel ratio overlay.
  private static final boolean DEFAULT_SCALPEL_ENABLED = false; // No crazy 3D view tree.
  private static final boolean DEFAULT_SCALPEL_WIREFRAME_ENABLED = false; // Draw views by default.
  private static final boolean DEFAULT_SEEN_DEBUG_DRAWER = false; // Show debug drawer first time.

  @Provides @Singleton OkHttpClient provideOkHttpClient(Application app) {
    OkHttpClient client = DataModule.createOkHttpClient(app);
    client.setSslSocketFactory(createBadSslSocketFactory());
    return client;
  }

  @Provides @Singleton @ApiEndpoint StringPreference provideEndpointPreference(SharedPreferences preferences) {
    return new StringPreference(preferences, "debug_endpoint", ApiEndpoints.MOCK_MODE.url);
  }

  @Provides @Singleton @IsMockMode boolean provideIsMockMode(@ApiEndpoint StringPreference endpoint) {
    return ApiEndpoints.isMockMode(endpoint.get());
  }

  @Provides @Singleton @NetworkProxy
  StringPreference provideNetworkProxy(SharedPreferences preferences) {
    return new StringPreference(preferences, "debug_network_proxy");
  }

  @Provides @Singleton @AnimationSpeed
  IntPreference provideAnimationSpeed(SharedPreferences preferences) {
    return new IntPreference(preferences, "debug_animation_speed", DEFAULT_ANIMATION_SPEED);
  }

  @Provides @Singleton @PicassoDebugging BooleanPreference providePicassoDebugging(SharedPreferences preferences) {
    return new BooleanPreference(preferences, "debug_picasso_debugging", DEFAULT_PICASSO_DEBUGGING);
  }

  @Provides @Singleton @PixelGridEnabled
  BooleanPreference providePixelGridEnabled(SharedPreferences preferences) {
    return new BooleanPreference(preferences, "debug_pixel_grid_enabled",
        DEFAULT_PIXEL_GRID_ENABLED);
  }

  @Provides @Singleton @PixelRatioEnabled
  BooleanPreference providePixelRatioEnabled(SharedPreferences preferences) {
    return new BooleanPreference(preferences, "debug_pixel_ratio_enabled",
        DEFAULT_PIXEL_RATIO_ENABLED);
  }

  @Provides @Singleton @SeenDebugDrawer
  BooleanPreference provideSeenDebugDrawer(SharedPreferences preferences) {
    return new BooleanPreference(preferences, "debug_seen_debug_drawer", DEFAULT_SEEN_DEBUG_DRAWER);
  }

  @Provides @Singleton @ScalpelEnabled
  BooleanPreference provideScalpelEnabled(SharedPreferences preferences) {
    return new BooleanPreference(preferences, "debug_scalpel_enabled", DEFAULT_SCALPEL_ENABLED);
  }

  @Provides @Singleton @ScalpelWireframeEnabled
  BooleanPreference provideScalpelWireframeEnabled(SharedPreferences preferences) {
    return new BooleanPreference(preferences, "debug_scalpel_wireframe_drawer",
        DEFAULT_SCALPEL_WIREFRAME_ENABLED);
  }

  @Provides @Singleton Picasso providePicasso(IdlingDownloaderWrapper downloaderWrapper, Application app) {
    return new Picasso.Builder(app)
        .downloader(downloaderWrapper)
        .build();
  }

  @Provides @Singleton Downloader provideDownloader(IdlingDownloaderWrapper idlingWrapper) {
      return idlingWrapper;
  }

  @Provides @Singleton IdlingDownloaderWrapper provideIdlingDownloaderWrapper(MockRestAdapter mockRestAdapter, Application app,
                                                                              OkHttpClient client, @IsMockMode boolean isMockMode) {
      return new IdlingDownloaderWrapper(
          (isMockMode)
            ? new MockDownloader(mockRestAdapter, app.getAssets())
            : new OkHttpDownloader(client)
      );
  }

  private static SSLSocketFactory createBadSslSocketFactory() {
    try {
      // Construct SSLSocketFactory that accepts any cert.
      SSLContext context = SSLContext.getInstance("TLS");
      TrustManager permissive = new X509TrustManager() {
        @Override public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        }

        @Override public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        }

        @Override public X509Certificate[] getAcceptedIssuers() {
          return null;
        }
      };
      context.init(null, new TrustManager[] { permissive }, null);
      return context.getSocketFactory();
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }
}
