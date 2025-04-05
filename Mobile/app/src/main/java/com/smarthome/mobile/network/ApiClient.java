package com.smarthome.mobile.network;

import static com.smarthome.mobile.BuildConfig.API_BASE_URL;

import com.smarthome.mobile.app.MyApp;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = API_BASE_URL;
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            SessionManager sessionManager = MyApp.getInstance().getSessionManager();
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(sessionManager))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }
}
