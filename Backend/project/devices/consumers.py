import json
from channels.generic.websocket import AsyncWebsocketConsumer
from api.models import Device, Home, Location
from channels.db import database_sync_to_async
from asgiref.sync import sync_to_async

def map_room_to_device(room):
    room_device_mapping = {
        "Phòng khách": "living_room",
        "Phòng ngủ": "bedroom",
        "Phòng vệ sinh": "bathroom",
        "Nhà bếp": "kitchen",
    }
    return room_device_mapping.get(room, None)

@database_sync_to_async
def get_home(home_id):
    return Home.objects.get(id=home_id)

# Lưu trạng thái trước đó để tránh gửi trùng lặp
class ESP32Consumer(AsyncWebsocketConsumer):
  async def connect(self):
    # await self.channel_layer.group_add("esp32_group", self.channel_name)
    await self.accept()
    print(" ESP32 WebSocket connected!")

  async def disconnect(self, close_code):
    await self.channel_layer.group_discard("esp32_group", self.channel_name)


    print(" ESP32 WebSocket disconnected!")

  async def receive(self, text_data):
    data = json.loads(text_data)
    command = data.get("command")

    if command == "init_request":
      home_id = data.get("home_id")
      home_email = data.get("home_email")
      response_data = {}
      home = await get_home(home_id)
      locations = await sync_to_async(list)(Location.objects.filter(home=home))

      if home_email:
        group_name = f"esp32_{home_email.replace('@', '_at_').replace('.', '_dot_')}"
        await self.channel_layer.group_add(group_name, self.channel_name)
        print(f" ESP32 WebSocket added to group: {group_name}")

      for location in locations:
          devices = await sync_to_async(list)(location.devices.all())
          for device in devices:
              key = f"{device.type}_{map_room_to_device(location.name)}"
              response_data[key] = device.status

      await self.send(text_data=json.dumps({
          "command": "init_response",
          "data": response_data
      }))

    else:
      device = data.get("device")
      state = data.get("state")
      status = data.get("status")

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
