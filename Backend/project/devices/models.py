from django.db import models

class DeviceStatus(models.Model):
    device = models.CharField(max_length=50)  # "led", "buzzer", "servo"
    state = models.CharField(max_length=50)   # "on", "off", hoặc "angle" cho servo
    timestamp = models.DateTimeField(auto_now_add=True)
