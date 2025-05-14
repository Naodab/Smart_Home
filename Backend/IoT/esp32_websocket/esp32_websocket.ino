  #include <WiFi.h>
#include <WebSocketsClient.h>
#include <ArduinoJson.h>
#include <ESP32Servo.h>
// #include <DHT.h>
#include <Arduino.h>
#include <map>
#define HOME_ID 8
std::map<String, String> deviceStateMap;

const char* ssid = "no";
const char* password = "doandoandoan";
const char* serverAddress = "192.168.151.197";  // Thay dbằng IP server Djangoe
const int serverPort = 8088;

WebSocketsClient webSocket;

Servo bedroomDoor;
Servo bathroomDoor;

// LIGHT PIN
constexpr int LIGHT_BEDROOM_PIN = 16;
constexpr int LIGHT_BATHROOM_PIN = 17;cmd
constexpr int LIGHT_KITCHEN_PIN = 18;
constexpr int LIGHT_LIVING_ROOM_PIN = 19;

// FAN PIN (L298N#1)
constexpr int FAN_BEDROOM_EN = 21, FAN_BATHROOM_EN = 22;
constexpr int FAN_BEDROOM_PIN1 = 23, FAN_BEDROOM_PIN2 = 25, FAN_BATHROOM_PIN3 = 32, FAN_BATHROOM_PIN4 = 33;

// CURTAIN + MAINDOOR PIN (L298N#2)
constexpr int curtainENA = 13, doorENA = 4;
constexpr int curtainIN1 = 14, curtainIN2 = 33, doorIN1 = 15, doorIN2 = 2;

// DOOR PIN
constexpr int DOOR_BEDROOM_PIN = 27;
constexpr int DOOR_BATHROOM_PIN = 26;

// constraints

constexpr int FAN_SPEED_LOW = 100;
constexpr int FAN_SPEED_MEDIUM = 175;
constexpr int FAN_SPEED_HIGH = 250;
constexpr int DOOR_DURATION = 700;
constexpr int DOOR_SPEED = 180;
constexpr int CURTAIN_SPEED = 70;
constexpr int CURTAIN_DURATION = 1200;
constexpr int DOOR_OPEN_ANGLE = 0;
constexpr int DOOR_CLOSE_ANGLE = 120;

void webSocketEvent(WStype_t type, uint8_t * payload, size_t length) {
  switch(type) {
    case WStype_DISCONNECTED:
      Serial.println("❌ WebSocket Disconnected!");
      break;
    case WStype_CONNECTED:
      Serial.println("✅ Connected to WebSocket server!");
      webSocket.sendTXT("{\"message\": \"ESP32 connected\"}");
      fetchInitialDeviceStates();
      break;
    case WStype_TEXT:
      Serial.print("📩 Received WebSocket message: ");
      Serial.println((char*)payload);
      handleCommand((char*)payload);
      break;
  }
}

bool handleLight(String room, String state) {
  int level = (state == "on") ? HIGH : LOW;
  bool isHasRoom = false;
  int pin;
  if (room == "living_room") {
    isHasRoom = true;
    pin = LIGHT_LIVING_ROOM_PIN;
    deviceStateMap["light_living_room"] = state;
  } else if (room == "bedroom") {
    isHasRoom = true;
    pin = LIGHT_BEDROOM_PIN;
    deviceStateMap["light_bedroom"] = state;
  } else if (room == "bathroom") {
    isHasRoom = true;  
    pin = LIGHT_BATHROOM_PIN;
    deviceStateMap["light_bathroom"] = state;
  } else if (room == "kitchen") {
    isHasRoom = true;
    pin = LIGHT_KITCHEN_PIN;
    deviceStateMap["light_kitchen"] = state;
  }
  if (isHasRoom) {
    digitalWrite(pin, level);
    return true;
  }
  return false;
}

bool handleMainDoor(String state) {
  if (state == "open") {
    digitalWrite(doorIN1, HIGH);
    digitalWrite(doorIN2, LOW);
  } else {
    digitalWrite(doorIN1, LOW);
    digitalWrite(doorIN2, HIGH);
  }
  analogWrite(doorENA, DOOR_SPEED);
  delay(DOOR_DURATION);
  digitalWrite(doorIN1, LOW);
  digitalWrite(doorIN2, LOW);
  analogWrite(doorENA, 0);
  Serial.println(">> Door toggled");
  return true;
}

bool handleDoor(String room, String state) {
  if (room == "living_room") return handleMainDoor(state);
  int angle = (state == "open") ? DOOR_OPEN_ANGLE : DOOR_CLOSE_ANGLE;
  if (room == "bedroom") {
    bedroomDoor.write(angle); 
    deviceStateMap["fan_bedroom"] = state;
    return true;
  }
  
  if (room == "bathroom") {
    bathroomDoor.write(angle); 
    deviceStateMap["fan_bathroom"] = state;
    return true;
  }
  return false;
}

bool handleCurtain(String room, String state) {
  if (state == "open") {
      digitalWrite(curtainIN1, HIGH);
      digitalWrite(curtainIN2, LOW);
    } else {
      digitalWrite(curtainIN1, LOW);
      digitalWrite(curtainIN2, HIGH);
    }
    analogWrite(curtainENA, CURTAIN_SPEED);
    delay(CURTAIN_DURATION);
    digitalWrite(curtainIN1, LOW);
    digitalWrite(curtainIN2, LOW);
    analogWrite(curtainENA, 0);
    Serial.println(">> Curtain toggled");
    return true;
}

bool handleFan(String room, String state) {
  int fanSpeed = 0;
  if (state == "1") fanSpeed = FAN_SPEED_LOW;
  if (state == "2") fanSpeed = FAN_SPEED_MEDIUM;
  if (state == "3") fanSpeed = FAN_SPEED_HIGH;

  int in1 = FAN_BEDROOM_PIN1, in2 = FAN_BEDROOM_PIN2, ena = FAN_BEDROOM_EN;
  if (room == "bathroom") {
    in1 = FAN_BATHROOM_PIN3;
    in2 = FAN_BATHROOM_PIN4;
    ena = FAN_BATHROOM_EN;
    deviceStateMap["fan_bathroom"] = state;
  } else {
    deviceStateMap["fan_bedroom"] = state;
  }

  digitalWrite(in1, HIGH);
  analogWrite(ena, fanSpeed);
  Serial.printf(">> Fan %d set to speed %d\n", fanSpeed);
  return true;
}

void handleCommand(String payload) {
    Serial.printf("🔍 Parsing command: %s\n", payload.c_str());

    DynamicJsonDocument doc(256);
    DeserializationError error = deserializeJson(doc, payload);
    
    if (error) {
        Serial.println("❌ JSON parsing failed!");
        return;
    }

    String type = doc["type"];

    if (type == "remote") {
      String device = doc["device"];
      String state = doc["state"];
      String room = doc["room"];
      int angle = doc["angle"];
  
      Serial.printf("🔧 Device: %s, State: %s\n", device.c_str(), state.c_str());
  
      bool success = false;
  
      if (device == "light") success = handleLight(room, state);
      else if (device == "door") success = handleDoor(room, state);
      else if (device == "curtain") success = handleCurtain(room, state);
      else if (device == "fan") success = handleFan(room, state);
      Serial.printf("Init state set: %s %s = %s\n", device, room, state);
  
      sendStatus(device, state, angle, success);
    } else if (type == "init_response") {
      handleInitResponse(doc["data"].as<JsonObject>());
      return;
    }
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

void handleInitResponse(JsonObject data) {
  for (JsonPair kv : data) {
    String key = kv.key().c_str();
    String value = kv.value().as<String>();
    deviceStateMap[key] = value;
    Serial.printf("Init state set: %s = %s\n", key.c_str(), value.c_str());
  }
}

void initializeDeviceStateMap() {
  String devices[] = {"light", "door", "curtain", "fan"};
  String rooms[] = {"living_room", "bedroom", "bathroom", "kitchen"};

  for (String device : devices) {
    String value = "close";
    if (device == "fan") value = "0";
    else if (device == "light") value = "off";
    for (String room : rooms) {
      String key = device + "_" + room;
      deviceStateMap[key] = value;
    }
  }

  Serial.println("✅ Device state map initialized:");
  for (auto const& pair : deviceStateMap) {
    Serial.printf("  %s : %s\n", pair.first.c_str(), pair.second.c_str());
  }
}

void fetchInitialDeviceStates() {
    DynamicJsonDocument doc(128);
    doc["command"] = "init_request";
    doc["home_id"] = HOME_ID;

    String jsonStr;
    serializeJson(doc, jsonStr);

    Serial.println("📤 Requesting initial device states from server...");
    webSocket.sendTXT(jsonStr);
}

void setup() {
    Serial.begin(115200);

    // Set up pin
    pinMode(LIGHT_BEDROOM_PIN, OUTPUT); digitalWrite(LIGHT_BEDROOM_PIN, LOW);
    pinMode(LIGHT_BATHROOM_PIN, OUTPUT); digitalWrite(LIGHT_BATHROOM_PIN, LOW);
    pinMode(LIGHT_LIVING_ROOM_PIN, OUTPUT); digitalWrite(LIGHT_LIVING_ROOM_PIN, LOW);
    pinMode(LIGHT_KITCHEN_PIN, OUTPUT); digitalWrite(LIGHT_KITCHEN_PIN, LOW);

    pinMode(FAN_BEDROOM_EN, OUTPUT); pinMode(FAN_BATHROOM_EN, OUTPUT);
    pinMode(FAN_BEDROOM_PIN1, OUTPUT); pinMode(FAN_BEDROOM_PIN2, OUTPUT);
    pinMode(FAN_BATHROOM_PIN3, OUTPUT); pinMode(FAN_BATHROOM_PIN4, OUTPUT);

    pinMode(curtainENA, OUTPUT); pinMode(doorENA, OUTPUT);
    pinMode(curtainIN1, OUTPUT); pinMode(curtainIN2, OUTPUT);
    pinMode(doorIN1, OUTPUT); pinMode(doorIN2, OUTPUT);

    bedroomDoor.attach(DOOR_BEDROOM_PIN);
    bathroomDoor.attach(DOOR_BATHROOM_PIN);
    
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(1000);
        Serial.println("Connecting to WiFi...");
    }
    Serial.println("WiFi connected!");

    webSocket.begin(serverAddress, serverPort, "/ws/esp32/");
    webSocket.onEvent(webSocketEvent);
}

void loop() {
    webSocket.loop();
    delay(100);
}
