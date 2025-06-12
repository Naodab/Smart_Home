package com.smarthome.mobile.network;

import static com.smarthome.mobile.BuildConfig.API_BASE_URL;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smarthome.mobile.app.MyApp;
import com.smarthome.mobile.enums.Status;
import com.smarthome.mobile.enums.Type;
import com.smarthome.mobile.network.gson.StatusDeserializer;
import com.smarthome.mobile.network.gson.TypeDeserializer;

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

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Status.class, new StatusDeserializer())
                    .registerTypeAdapter(Type.class, new TypeDeserializer())
                    .create();

            Log.d("API_BASE_URL", "getClient: " + BASE_URL);
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
