package com.smarthome.mobile.network;

import com.smarthome.mobile.app.MyApp;
import com.smarthome.mobile.util.WebSocketListenerInterface;
import static com.smarthome.mobile.BuildConfig.WEBSOCKET_URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {
    private WebSocket webSocket;
    private final WebSocketListenerInterface listener;

    public WebSocketClient(WebSocketListenerInterface listener) {
        this.listener = listener;
    }

    public void startWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(WEBSOCKET_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                if (listener != null) listener.onWebSocketConnected();
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("command", "init_request");
                    jsonObject.put("home_id", MyApp.getInstance().getSessionManager().fetchUserID());
                    webSocket.send(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                if (listener != null) listener.onMessageReceived(text);
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                webSocket.close(1000, null);
                if (listener != null) listener.onWebSocketDisconnected();
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                if (listener != null) listener.onWebSocketError(t.getMessage());
            }
        });
    }

    public void close() {
        if (webSocket != null) webSocket.close(1000, null);
    }
}
