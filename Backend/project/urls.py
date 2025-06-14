"""
URL configuration for project project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path, include

from django.conf import settings
from django.conf.urls.static import static
from django.urls import path
from face_recognition_app.views import send_command_to_esp32
from face_recognition_app import views
urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/', include('api.urls')),
    path('face_detection/detect/', views.detect, name='detect'),
    # path("upload/", views.detect, name="upload"),
    # path('api/control/<str:device>/<str:state>/', send_command_to_esp32, name='control_esp32'),
    # path("api/control/<str:device>/<str:state>/<int:angle>/", send_command_to_esp32)  # Hỗ trợ servo
]
if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)


if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)