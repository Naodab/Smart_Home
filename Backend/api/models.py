from django.contrib.auth.hashers import make_password, check_password
from django.db import models

# Create your models here.
class Home(models.Model):
  id = models.AutoField(primary_key=True)
  email = models.CharField(max_length=120)
  password = models.CharField(max_length=120)
  address = models.CharField(max_length=120, default="123 Kieu Son Den")
  temperature = models.FloatField(default=27)
  humidity = models.FloatField(default=50)

  def save(self, *args, **kwargs):
    if not self.password.startswith('pbkdf2_sha256$'):
      self.password = make_password(self.password)
    super().save(*args, **kwargs)

  def check_password(self, raw_password):
    return check_password(raw_password, self.password)  

  def __str__(self):
    return f"Home: {self.id} - {self.email}"

class Person(models.Model):
  id = models.AutoField(primary_key=True)
  name = models.CharField(max_length=120)

  def __str__(self):
    return f"Person: {self.id} - {self.name}"

class Device(models.Model):
  DEVICE_TYPES = [
    ('door', 'Door'),
    ('fan', 'Fan'),
    ('light', 'Light'),
    ('curtain', 'Curtain'),
  ]

  id = models.AutoField(primary_key=True)
  name = models.CharField(max_length=120)
  status = models.BooleanField(default=False)
  type = models.CharField(max_length=120, choices=DEVICE_TYPES)
  home = models.ForeignKey(Home, on_delete=models.CASCADE, related_name='devices')

  def __str__(self):
    return f"Device: {self.id} - {self.name} - {self.type} - {self.home}"

class History(models.Model):
  id = models.AutoField(primary_key=True)
  device = models.ForeignKey(Device, on_delete=models.CASCADE, related_name='histories')
  status = models.BooleanField(default=False)
  time = models.DateTimeField(auto_now_add=True)
  person = models.ForeignKey(Person, on_delete=models.CASCADE, related_name='histories')

  def __str__(self):
    return f"History: {self.id} - {self.device} - {self.status} - {self.time} - {self.person}"

class HomePerson(models.Model):
  id = models.AutoField(primary_key=True)
  home = models.ForeignKey(Home, on_delete=models.CASCADE, related_name='home_persons')
  person = models.ForeignKey(Person, on_delete=models.CASCADE, related_name='person_homes')

  def __str__(self):
    return f"HomePerson: {self.id} - {self.home} - {self.person}"