

from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from django.http import JsonResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
import numpy as np
import face_recognition
import urllib
import json
import cv2
import os

# Hàm gửi lệnh tới ESP32 qua WebSocket
def send_command_to_esp32(device, state, angle=None):
    command = {"device": device, "state": state}
    if device == "servo" and angle is not None:
        command["angle"] = angle 
    print(f"🚀 Sending command to WebSocket: {command}")  # In ra terminal Django

    channel_layer = get_channel_layer()
    async_to_sync(channel_layer.group_send)(
        "esp32_group",
        {"type": "send_command", "command": command}
    )
    return {"status": "command_sent", "command": command}

import os
import cv2
import face_recognition
import numpy as np
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt

# Khởi tạo danh sách mã hóa toàn cục
ENCODED_FACES = []
CLASSNAMES = []

def init_dataset():
    global ENCODED_FACES, CLASSNAMES
    path = "./dataset/"
    images = []
    CLASSNAMES = []
    myList = os.listdir(path)
    for image_name in myList:
        current_image = cv2.imread(f'{path}{image_name}')
        images.append(current_image)
        CLASSNAMES.append(os.path.splitext(image_name)[0])
    ENCODED_FACES = encodeImages(images)
    print("Dataset encoded successfully!")

def encodeImages(images):
    encodeList = []
    for img in images:
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        encode = face_recognition.face_encodings(img)[0]
        encodeList.append(encode)
    return encodeList

# Gọi init_dataset() khi server khởi động (có thể đặt trong settings.py hoặc một nơi khởi tạo)
init_dataset()

@csrf_exempt
def detect(request):
    data = {"faces": []}
    if request.method == 'POST':
        # Dùng danh sách đã encode sẵn
        encodeListKnown = ENCODED_FACES
        classnames = CLASSNAMES

        # Xử lý ảnh từ client
        if request.FILES.get("image", None) is not None:
            image = _grab_image(stream=request.FILES["image"])
        elif request.POST.get("url", None) is not None:
            image = _grab_image(url=request.POST.get("url"))
        else:
            path = request.POST.get("path", None)
            image = _grab_image(path=path)
        
        faceInCurrentImage = face_recognition.face_locations(image)
        encodeCurrentImage = face_recognition.face_encodings(image)
        for encodeFace, faceLoc in zip(encodeCurrentImage, faceInCurrentImage):
            matches = face_recognition.compare_faces(encodeListKnown, encodeFace)
            faceDis = face_recognition.face_distance(encodeListKnown, encodeFace)
            matchIndex = np.argmin(faceDis)
            if faceDis[matchIndex] < 0.5:
                name = classnames[matchIndex].upper()
            else:
                name = "Unknown"
            y1, x2, y2, x1 = faceLoc
            if faceLoc is not None:
                data["faces"].append({"name": name, "face_location": {"x1": x1, "y1": y1, "x2": x2, "y2": y2}})
        
        # Gửi lệnh tới ESP32
        for face in data["faces"]:
            if face["name"] == "BINH":
                send_command_to_esp32(device="led", state="on")
                send_command_to_esp32(device="servo", state="on", angle=90)
                send_command_to_esp32(device="buzzer", state="on")
                print("✅ Detected 'BINH' - Commands sent to ESP32!")
            elif face["name"] == "Unknown":
                send_command_to_esp32(device="led", state="off")
                send_command_to_esp32(device="servo", state="off", angle=0)
                send_command_to_esp32(device="buzzer", state="off")
                print("❌ Unknown face - Turning off devices.")

        print(data)
        return JsonResponse(data)
    return JsonResponse({"error": "Invalid request"}, status=400)

# Giữ nguyên các hàm khác (_grab_image, send_command_to_esp32)

# Hàm hỗ trợ đọc ảnh
def _grab_image(path=None, stream=None, url=None):
    if path is not None:
        image = cv2.imread(path)
        image = cv2.resize(image, (0, 0), fx=0.25, fy=0.25)
    else:
        if url is not None:
            resp = urllib.request.urlopen(url)
            data = resp.read()
        elif stream is not None:
            data = stream.read()
        image = np.asarray(bytearray(data), dtype="uint8")
        image = cv2.imdecode(image, cv2.IMREAD_COLOR)
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    return image
