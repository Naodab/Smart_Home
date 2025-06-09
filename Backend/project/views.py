from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from django.http import JsonResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from project.devices.helpers import convert_to_group
import numpy as np

def map_room_to_device(room):
    room_device_mapping = {
        "Phòng khách": "living_room",
        "Phòng ngủ": "bedroom",
        "Phòng vệ sinh": "bathroom",
        "Nhà bếp": "kitchen",
    }
    return room_device_mapping.get(room, None)

def send_command_to_esp32(email, device, state, room, angle=None):
    command = {"type": "remote", "device": device, "state": state, "room": map_room_to_device(room)}
    if device == "servo" and angle is not None:
        command["angle"] = angle 
    print(f"🚀 Sending command to WebSocket: {command}")  # In ra terminal Django

    channel_layer = get_channel_layer()
    async_to_sync(channel_layer.group_send)(
        convert_to_group(home_email=email, type="esp32"),
        {"type": "send_command", "command": command}
    )
    return {"status": "command_sent", "command": command}