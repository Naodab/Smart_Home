import json
from channels.generic.websocket import AsyncWebsocketConsumer
from api.models import Device, Home, Location
from channels.db import database_sync_to_async
from asgiref.sync import sync_to_async
from .helpers import convert_to_group

@database_sync_to_async
def get_home(home_id):
    try:
        return Home.objects.get(id=home_id)
    except Home.DoesNotExist:
        return None

class AndroidConsumers(AsyncWebsocketConsumer):
    async def connect(self):
        await self.accept()

    async def disconnect(self, close_code):
        await self.close()
        print("Android WebSocket disconnected!")

    async def receive(self, text_data):
        data = json.loads(text_data)
        command = data.get("command")

        if command == "init_request":
            home_id = data.get("home_id")
            home = await get_home(home_id)
            if not home:
                await self.send(text_data=json.dumps({
                    "type": "init_response",
                    "success": False
                }))
                return
            
            group_name = convert_to_group(home_email=home.email, type="android")
            await self.channel_layer.group_add(group_name, self.channel_name)
            print(f" Android WebSocket added to group: {group_name}")
            await self.send(text_data=json.dumps({
                "command": "init_response",
                "success": True
            }))

    async def send_temp_humid(self, event):
        await self.send(text_data=json.dumps({
            "command": "temp_humid",
            "temperature": event["temperature"],
            "humidity": event["humidity"]
        }))