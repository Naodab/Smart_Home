from django.urls import re_path
from project.devices.consumers import ESP32Consumer
# from project.devices.consumersandroid import AndroidConsumer

websocket_urlpatterns = [
  re_path(r"ws/esp32/$", ESP32Consumer.as_asgi()),
  # re_path(r'ws/android/$', ESP32Consumer.as_asgi()), 
  # re_path(r'ws/android/', AndroidConsumer.as_asgi()),
]
