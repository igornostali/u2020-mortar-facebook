package co.lemonlabs.mortar.example.data;

import co.lemonlabs.mortar.example.data.api.ApiModule;

public enum ApiEndpoints {
    PRODUCTION("Production", ApiModule.PRODUCTION_API_URL),
    // STAGING("Staging", "https://api.staging.imgur.com/3/"),
    MOCK_MODE("Mock Mode", "mock://"),
    CUSTOM("Custom", null);

    public final String name;
    public final String url;

    public static ApiEndpoints from(String endpoint) {
        for (ApiEndpoints value : values()) {
            if (value.url != null && value.url.equals(endpoint)) {
                return value;
            }
        }
        return CUSTOM;
    }

    public static boolean isMockMode(String endpoint) {
        return from(endpoint) == MOCK_MODE;
    }

    @Override
    public String toString() {
        return name;
    }

    ApiEndpoints(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
