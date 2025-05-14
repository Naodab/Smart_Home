from django.shortcuts import render

from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse
from django.apps import apps

import urllib
import cv2 as cv
import numpy as np
from sklearn.preprocessing import LabelEncoder
from keras_facenet import FaceNet
from mtcnn import MTCNN
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
 
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from django.http import JsonResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
import numpy as np
import urllib
from api.models import Person

import numpy as np
import urllib
import cv2
import os
from collections import defaultdict
import numpy as np
# Hàm gửi lệnh tới ESP32 qua WebSocket
def send_command_to_esp32(device, dev_id , state):
    command = {"device": device, "id": dev_id, "state": state}
    print(f"🚀 Sending command to WebSocket: {command}")  # In ra terminal Django

    channel_layer = get_channel_layer()
    async_to_sync(channel_layer.group_send)(
        "esp32_group",
        {"type": "send_command", "command": command}
    )
    return {"status": "command_sent", "command": command}

# def send_update_to_android():
#     """Hàm gửi thông báo đến Android qua WebSocket"""
#     message = {
#         "detail": "Unknown"
#     }
  
#     print(f"📡 Sending WebSocket message: {message}")  # Debug message

#     channel_layer = get_channel_layer()
#     async_to_sync(channel_layer.group_send)(
#         "android_group",  # Nhóm WebSocket cho Android
#         {"type": "send_message", "message": message}
#     )


app_config = apps.get_app_config("face_recognition_app")
face_embeddings = app_config.face_embeddings
model = app_config.model

from collections import defaultdict

names = face_embeddings['arr_1']
ids   = face_embeddings['arr_0']

# Tạo mapping name -> list of embeddings
name_to_ids = defaultdict(list)
for name, embedding in zip(names, ids):
    name_to_ids[name].append(embedding)

# Optionally, convert lists to numpy arrays
for name in name_to_ids:
    name_to_ids[name] = np.array(name_to_ids[name])

# Giữ lại encoder để giải mã nhãn số -> tên
encoder  = LabelEncoder().fit(names)


facenet = FaceNet()
# Y = face_embeddings['arr_1']
# encoder = LabelEncoder()
# encoder.fit(Y)
detector = MTCNN()

def map_name_to_id(name):
    """
    Hàm ánh xạ tên thành ID
    """
    names = {
        "DOAN": 1,
        "BINH": 7,
        "HOANG": 6,
        "HUY": 3,
    }
    return names.get(name, -1)

@csrf_exempt
def detect(request):
    if request.method != 'POST':
        return JsonResponse({"error": "Invalid request method"}, status=405)

    image = None
    if request.FILES.get("file"):
        image = _grab_image(stream=request.FILES["file"])
    elif request.POST.get("url"):
        image = _grab_image(url=request.POST["url"])
    elif request.POST.get("path"):
        image = _grab_image(path=request.POST["path"])

    if image is None:
        return JsonResponse({"error": "No suitable image in the request!"}, status=400)

    faces = detector.detect_faces(image)
    response_faces = []
    first_valid_face_response = None

    for face in faces:
        x, y, w, h = face['box']
        confidence = face['confidence']

        if confidence <= 0.9:
            continue

        face_img = image[y:y + h, x:x + w]
        face_img = cv.resize(face_img, (160, 160))
        face_img = np.expand_dims(face_img, axis=0)
        ypred = facenet.embeddings(face_img)

        if hasattr(model, "predict_proba"):
            proba = model.predict_proba(ypred)
            max_prob = np.max(proba)
            if max_prob < 0.8:
                final_name = "Unknown"
            else:
                final_name = encoder.inverse_transform(model.predict(ypred))[0]
        else:
            final_name = encoder.inverse_transform(model.predict(ypred))[0]

        final_id = -1 if final_name == "Unknown" else int(encoder.transform([final_name])[0])

        face_data = {
            "id": final_id,
            "name": final_name,
            "face_location": {"x": x, "y": y, "w": w, "h": h}
        }

        response_faces.append(face_data)

        if first_valid_face_response is None and final_id != -1:
            person = Person.objects.filter(id=map_name_to_id(final_name)).first()
            if person:
                first_valid_face_response = {
                    "id": person.id,
                    "name": person.name,
                    "face_location": {"x": x, "y": y, "w": w, "h": h}
                }

    if first_valid_face_response:
        print(first_valid_face_response)
        return JsonResponse(data=first_valid_face_response)

    return JsonResponse({"error": "Không tìm thấy khuôn mặt hợp lệ!"}, status=400)

def _grab_image(path=None, stream=None, url=None):
    image = None
    try:
        if path is not None:
            image = cv.imread(path)
        elif url is not None:
            resp = urllib.request.urlopen(url)
            data = resp.read()
            image = np.asarray(bytearray(data), dtype="uint8")
            image = cv.imdecode(image, cv.IMREAD_COLOR)
        elif stream is not None:
            data = stream.read()
            image = np.asarray(bytearray(data), dtype="uint8")
            image = cv.imdecode(image, cv.IMREAD_COLOR)
        
        if image is None:
            print("Cannot load the image!")
            return None
        
        image = cv.resize(image, (0, 0), fx=0.5, fy=0.5)
        image = cv.cvtColor(image, cv.COLOR_BGR2RGB)
        rgb_image = cv.cvtColor(image, cv.COLOR_BGR2RGB)
        return rgb_image
    except Exception as e:
        print(f"Error when processing image, with error: {e}")
        return None