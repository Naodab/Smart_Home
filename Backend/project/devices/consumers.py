import json
from channels.generic.websocket import AsyncWebsocketConsumer
# Lưu trạng thái trước đó để tránh gửi trùng lặp
class ESP32Consumer(AsyncWebsocketConsumer):
  async def connect(self):
    await self.channel_layer.group_add("esp32_group", self.channel_name)
    await self.accept()
    print(" ESP32 WebSocket connected!")

  async def disconnect(self, close_code):
    await self.channel_layer.group_discard("esp32_group", self.channel_name)


    print(" ESP32 WebSocket disconnected!")

  async def receive(self, text_data):
    data = json.loads(text_data)
    device = data.get("device")
    state = data.get("state")
    status = data.get("status")

    if status:
      print(f" ESP32 Response: {device} -> {state}, Status: {status}")
      
      # Gửi phản hồi đến Android WebSocket
      await self.channel_layer.group_send(
          "android_group",
          {
              "type": "android_response",
              "device": device,
              "state": state,
              "status": status
          }
      )
      

  async def send_command(self, event):
    command = event["command"]
    await self.send(text_data=json.dumps(command))



  async def android_response(self, event):
    """Gửi phản hồi từ ESP32 đến Android"""
    await self.send(text_data=json.dumps({
        "device": event["device"],
        "state": event["state"],
        "status": event["status"]
    }))
