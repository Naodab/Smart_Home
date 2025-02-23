from django.urls import path

from . import views

urlpatterns = [
  path('upload/', views.speech_create_api_view), # /api/speechs/
]