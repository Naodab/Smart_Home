package com.smarthome.mobile.util;

public interface WebSocketListenerInterface {
    void onMessageReceived(String message);
    void onWebSocketConnected();
    void onWebSocketDisconnected();
    void onWebSocketError(String error);
}
