from django.db import models

# Create your models here.
class Home(models.Model):
  email = models.CharField(max_length=120)
  password = models.CharField(max_length=120)

class Person(models.Model):
  name = models.CharField(max_length=120)
  home = models.ForeignKey(Home, on_delete=models.CASCADE, related_name='persons')