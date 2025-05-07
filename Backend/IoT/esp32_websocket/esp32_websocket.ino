#include <WiFi.h>
#include <WebSocketsClient.h>
#include <ArduinoJson.h>
#include <ESP32Servo.h>

const char* ssid = "Tuyetnu";
const char* password = "123456789";
const char* serverAddress = "192.168.1.10";  // Thay bằng IP server Django 
const int serverPort = 8088;

WebSocketsClient webSocket;
const int ledPin = 2;
const int buzzerPin = 15;
const int servoPin = 5;
Servo myServo;

void webSocketEvent(WStype_t type, uint8_t * payload, size_t length) {
    switch(type) {
        case WStype_DISCONNECTED:
            Serial.println("❌ WebSocket Disconnected!");
            break;
        case WStype_CONNECTED:
            Serial.println("✅ Connected to WebSocket server!");
            webSocket.sendTXT("{\"message\": \"ESP32 connected\"}");
            break;
        case WStype_TEXT:
            Serial.print("📩 Received WebSocket message: ");
            Serial.println((char*)payload);
            handleCommand((char*)payload);
            break;
    }
}

void handleCommand(String payload) {
    Serial.printf("🔍 Parsing command: %s\n", payload.c_str());

    DynamicJsonDocument doc(256);
    DeserializationError error = deserializeJson(doc, payload);
    
    if (error) {
        Serial.println("❌ JSON parsing failed!");
        return;
    }

    String device = doc["device"];
    String state = doc["state"];
    int angle = doc["angle"];

    Serial.printf("🔧 Device: %s, State: %s\n", device.c_str(), state.c_str());

    bool success = false;

    if (device == "led") {
        Serial.println("💡 Toggling LED...");
        if (state == "on") {
            digitalWrite(ledPin, HIGH);
            success = true;
        } else if (state == "off") {
            digitalWrite(ledPin, LOW);
            success = true;
        }
    }
    else if (device == "buzzer"){
      Serial.println("Controlling Buzzer ...");
      if(state == "on"){
        digitalWrite(buzzerPin, HIGH);
        success = true;
      } else if (state == "off"){
         digitalWrite(buzzerPin, LOW);
            success = true;
      }
    }
    else if (device == "servo") {
        Serial.printf("🔄 Moving Servo to %d degrees...\n", angle);
        if (angle >= 0 && angle <= 90) { // Servo góc từ 0-180 độ
            myServo.write(angle);
            success = true;
        }
    }


    sendStatus(device, state, angle, success);
}

void sendStatus(String device, String state, int angle, bool success) {
  DynamicJsonDocument doc(256);

    doc["device"] = device;
    doc["state"] = state;
    if (device == "servo") {
        doc["angle"] = angle;
    }
    doc["status"] = success ? "success" : "failed";

    String jsonStr;
    serializeJson(doc, jsonStr);
    
    Serial.print("📤 Sending status to server: ");
    Serial.println(jsonStr);
    
    webSocket.sendTXT(jsonStr);
}

void setup() {
    Serial.begin(115200);
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(1000);
        Serial.println("Connecting to WiFi...");
    }
    Serial.println("WiFi connected!");

    pinMode(ledPin, OUTPUT);
    digitalWrite(ledPin, LOW);
    pinMode(buzzerPin, OUTPUT);
    digitalWrite(buzzerPin, LOW);

    myServo.attach(servoPin);
    myServo.write(180); // Đặt servo về góc 90 độ ban đầu

    webSocket.begin(serverAddress, serverPort, "/ws/esp32/");
    webSocket.onEvent(webSocketEvent);
}

void loop() {
    webSocket.loop();
    delay(100);
}
