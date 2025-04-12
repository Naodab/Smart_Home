from django.contrib.auth.hashers import make_password, check_password
from django.db import models
from django.contrib.auth.models import AbstractUser, BaseUserManager
from project.settings import DEFAULT_PASSWORD

class HomeManager(BaseUserManager):
  def create_user(self, email, password=DEFAULT_PASSWORD, **extra_fields):
    if not email:
        raise ValueError('The Email field must be set')
    email = self.normalize_email(email)
    user = self.model(email=email, **extra_fields)
    user.set_password(password)
    user.save(using=self._db)
    return user

  def create_superuser(self, email, password=None, **extra_fields):
    extra_fields.setdefault('is_staff', True)
    extra_fields.setdefault('is_superuser', True)
    
    if extra_fields.get('is_staff') is not True:
        raise ValueError('Superuser must have is_staff=True.')
    if extra_fields.get('is_superuser') is not True:
        raise ValueError('Superuser must have is_superuser=True.')
    
    return self.create_user(email, password, **extra_fields)

class Home(AbstractUser):
  username = None
  id = models.AutoField(primary_key=True)
  email = models.EmailField(max_length=120, unique=True)
  password = models.CharField(max_length=120, default=DEFAULT_PASSWORD)
  address = models.CharField(max_length=120, default="123 Kieu Son Den")
  temperature = models.FloatField(default=27)
  humidity = models.FloatField(default=50)

  USERNAME_FIELD = 'email'
  REQUIRED_FIELDS = []

  objects = HomeManager()

  def __str__(self):
    return f"Home: {self.id} - {self.email}"

class Person(models.Model):
  id = models.AutoField(primary_key=True)
  name = models.CharField(max_length=120)
  home = models.ForeignKey(Home, on_delete=models.CASCADE, related_name='people', null=True, blank=True)

  def __str__(self):
    return f"Person: {self.id} - {self.name}"

class Location(models.Model):
  id = models.AutoField(primary_key=True)
  name = models.CharField(max_length=120)
  home = models.ForeignKey(Home, on_delete=models.CASCADE, related_name='locations', null=True, blank=True)

  def __str__(self):
    return f"Location: {self.id} - {self.name} - {self.home}"

class Device(models.Model):
  DEVICE_TYPES = [
    ('door', 'Door'),
    ('fan', 'Fan'),
    ('light', 'Light'),
    ('curtain', 'Curtain'),
  ]

  DEVICE_STATUSES = [
    ('on', 'On'),
    ('off', 'Off'),
    ('locked', 'Locked'),
    ('unlocked', 'Unlocked'),
    ('0', '0'),
    ('1', '1'),
    ('2', '2'),
    ('3', '3')
  ]

  id = models.AutoField(primary_key=True)
  name = models.CharField(max_length=120)
  location = models.ForeignKey(Location, on_delete=models.CASCADE, related_name='devices', null=True, blank=True)
  status = models.CharField(max_length=10, choices=DEVICE_STATUSES, default="off")
  type = models.CharField(max_length=120, choices=DEVICE_TYPES, default="door")

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
  
class BlacklistedToken(models.Model):
  jti = models.CharField(max_length=255, unique=True)
  exp = models.DateTimeField()
  created_at = models.DateTimeField(auto_now_add=True)

  def __str__(self):
    return f"BlacklistedToken: {self.jti} - {self.created_at}"