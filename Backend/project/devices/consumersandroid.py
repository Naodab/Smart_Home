# import json
# from channels.generic.websocket import AsyncWebsocketConsumer
# class AndroidConsumer(AsyncWebsocketConsumer):
#     async def connect(self):
#         await self.channel_layer.group_add("android_group", self.channel_name)
#         await self.accept()
#         print("Android WebSocket connected!")

#     async def disconnect(self, close_code):
#         await self.channel_layer.group_discard("android_group", self.channel_name)
#         print("Android WebSocket disconnected!")
        
#     # cái này khi nhận từ android thì sử dụng api retrofit
#     async def receive(self, text_data):
#         print(f"Received from Android: {text_data}")

#     async def android_response(self, event):
#         """Nhận phản hồi từ ESP32 và gửi về Android"""
#         await self.send(text_data=json.dumps({
#             "device": event["device"],
#             "state": event["state"],
#             "status": event["status"]
#         }))



#     async def send_message(self, event):
#         """Gửi tin nhắn đến Android"""
#         message = event["message"]
#         print(f"📡 Sending to Android: {message}")
#         await self.send(text_data=json.dumps(message))

#     # async def send_android_update(self, event):
#     #     """Nhận phản hồi từ server và gửi về Android"""
#     #     await self.send(text_data=json.dumps({
#     #         "device": event["device"],
#     #         "state": event["state"],
#     #         "status": event["status"]
#     #     }))
#     #     print(f"📡 Sent update to Android: {event}")


