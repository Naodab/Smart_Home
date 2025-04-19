from django.urls import re_path
from project.devices.consumers import ESP32Consumer
<<<<<<< HEAD
from project.devices.consumersandroid import AndroidConsumer
=======
# from project.devices.consumersandroid import AndroidConsumer
>>>>>>> 3bd420abf20509311a4ed0f027df0adfe3152016

websocket_urlpatterns = [
  re_path(r"ws/esp32/$", ESP32Consumer.as_asgi()),
  # re_path(r'ws/android/$', ESP32Consumer.as_asgi()), 
<<<<<<< HEAD
  re_path(r'ws/android/', AndroidConsumer.as_asgi()),

=======
  # re_path(r'ws/android/', AndroidConsumer.as_asgi()),
>>>>>>> 3bd420abf20509311a4ed0f027df0adfe3152016
]
