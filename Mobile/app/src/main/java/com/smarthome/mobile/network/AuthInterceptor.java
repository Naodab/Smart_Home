package com.smarthome.mobile.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final SessionManager sessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = sessionManager.fetchAuthToken();

        if (token != null) {
            Request.Builder builder = originalRequest.newBuilder()
                    .header("Authentication", "Bearer " + token);
            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(originalRequest);
    }
}
