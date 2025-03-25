from django.urls import path

from . import views

urlpatterns = [
  # Mobile urls
  path('speeches/upload/', views.speech_create_api_view), # /api/speeches/upload/
  path('homes/login/', views.login_api_view), # /api/homes/login/
  
  # Backend urls
  path('homes/', views.home_api_view), # /api/homes/
  path('homes/<str:id>/', views.home_id_api_view), # /api/homes/<id>
  path('people/', views.person_api_view), # /api/people/
  path('people/<str:id>/', views.person_id_api_view), # /api/people/<id>
]