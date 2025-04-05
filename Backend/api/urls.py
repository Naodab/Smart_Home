from django.urls import path

from . import views

urlpatterns = [
  # User urls
  path('speeches/upload/', views.speech_create_api_view), # /api/speeches/upload/
  path('users/login/', views.user_login_api_view), # /api/users/login/
  path('mobile/homes/<str:email>/', views.home_mobile_api_view), # /api/mobile/homes/<email>
  
  # Admin urls
  path('admins/login/', views.admin_login_api_view), # /api/admins/login/
  path('homes/', views.home_api_view), # /api/homes/
  path('homes/emails/', views.home_emails_api_view), # /api/homes/emails/
  path('homes/<str:id>/', views.home_id_api_view), # /api/homes/<id>
  path('people/', views.person_api_view), # /api/people/
  path('people/select/', views.person_select_api_view), # /api/people/select/
  path('people/<str:id>/', views.person_id_api_view), # /api/people/<id>
  path('devices/', views.device_api_view), # /api/devices/
  path('devices/<str:id>/', views.device_id_api_view), # /api/devices/<id>
  path('histories/', views.history_api_view), # /api/histories/
  path('histories/<str:id>/', views.history_id_api_view), # /api/histories/<id>

  # Common urls
  path('tokens/refresh/', views.refresh_token_api_view), # /api/tokens/refresh/
]