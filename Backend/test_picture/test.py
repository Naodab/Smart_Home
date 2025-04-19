import requests
import cv2
import os

url = "http://10.10.29.198:8000/upload/"
payload = {"path": "./test_picture/test_binh1.jpg"}
r = requests.post(url, data=payload).json()
print("Kết quả được trả về từ API:", r)