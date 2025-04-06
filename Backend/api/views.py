from rest_framework import status
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.response import Response
from rest_framework.parsers import MultiPartParser, FormParser
from rest_framework.views import APIView

from django.core.files.storage import default_storage
from rest_framework_simplejwt.tokens import RefreshToken

from api.permissions import IsAdmin
from api.tokens import get_tokens_for_user

from .serializers import DeviceCreateSerializer, \
                          DeviceSerializer, \
                          DeviceUpdateSerializer, \
                          PersonSaveSerializer, \
                          PersonSerializer, \
                          SpeechSerializer, \
                          LoginSerializer, \
                          RegisterSerializer, \
                          HomeSerializer

from AI_Module.speech_recognition.speech_to_text import transfer_audio_to_text
from AI_Module.speaker_recognition.test import identify_speaker

from django.shortcuts import get_object_or_404
from api.models import Device, Home, Person

# MOBILE API
# ONLY FOR USER
# /api/speeches/upload/
class SpeechCreateAPIView(APIView):
  permission_classes = [AllowAny]
  
  parser_classes = (MultiPartParser, FormParser)
  def post(self, request, *args, **kwargs):
    serializer = SpeechSerializer(data=request.data)
    if serializer.is_valid():
        file = serializer.validated_data['file']
        email = serializer.validated_data['email']

        print(email) 
        print(file)

        if default_storage.exists(file.name):
          default_storage.delete(file.name)
        
        # Lưu tệp âm thanh
        file_name = default_storage.save(file.name, file)
        file_url = default_storage.url(file_name)

        transfer_audio_to_text()

        result = identify_speaker()
        print(result)

        return Response({"message": "File uploaded successfully", "file_url": file_url, "email": email})
    return Response(serializer.errors, status=400)
  
speech_create_api_view = SpeechCreateAPIView.as_view()

# /api/users/login/
class UserLoginAPIView(APIView):
  permission_classes = [AllowAny]

  def post(self, request, *args, **kwargs):
    serializer = LoginSerializer(data=request.data)
    if serializer.is_valid():
      email = serializer.validated_data['email']
      password = serializer.validated_data['password']

      home = get_object_or_404(Home, email=email)
      if home.is_staff:
        return Response({"message": "Permission denied, user required"}, status=403)
      if home.check_password(password):
        tokens = get_tokens_for_user(home)
        return Response({"message": "Login successful", "email": email, "tokens": tokens})
      return Response({"message": "Invalid credentials"}, status=401)
    return Response(serializer.errors, status=400)
  
user_login_api_view = UserLoginAPIView.as_view()

# /api/mobile/homes/<email>/
class HomeMobileAPIView(APIView):
  def get(self, request, email, *args, **kwargs):
    home = get_object_or_404(Home, email=email)
    serializer = HomeMobileSerializer(home)
    return Response(serializer.data, status=status.HTTP_200_OK)

  def put(self, request, email, *args, **kwargs):
    home = get_object_or_404(Home, email=email)
    serializer = HomeMobileSerializer(home, data=request.data)
    if serializer.is_valid():
      serializer.save()
      return Response(serializer.data, status=status.HTTP_200_OK)
    return Response(serializer.errors, status=400)

home_mobile_api_view = HomeMobileAPIView.as_view()

# BACKEND API
# ONLY FOR ADMIN
class AdminLoginAPIView(APIView):
  permission_classes = [AllowAny]
  
  def post(self, request,  *args, **kwargs):
    serializer = LoginSerializer(data=request.data)
    if serializer.is_valid():
      email = serializer.validated_data['email']
      password = serializer.validated_data['password']

      home = get_object_or_404(Home, email=email)
      if not home.is_staff:
        return Response({"message": "Permission denied, admin required"}, status=403)
      if home.check_password(password):
        tokens = get_tokens_for_user(home)
        return Response({"message": "Login successful", "email": email, "tokens": tokens})
      return Response({"message": "Invalid credentials"}, status=401)
    return Response(serializer.errors, status=400)
admin_login_api_view = AdminLoginAPIView.as_view()

# /api/homes/
class HomeAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]

  def post(self, request, *args, **kwargs):
    serializer = RegisterSerializer(data=request.data)
    if serializer.is_valid():
      home = serializer.save()
      return Response({"message": "Home registered successfully", "email": home.email, "address": home.address})
    return Response(serializer.errors, status=400)
  
  def get(self, request, *args, **kwargs):
    homes = Home.objects.all()
    homes = [home for home in homes if home.is_staff == False]
    serializer = HomeSerializer(homes, many=True)
    return Response(serializer.data)
home_api_view = HomeAPIView.as_view()

# /api/homes/<id>/
class HomeIdAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]

  def get(self, request, id, *args, **kwargs):
    home = get_object_or_404(Home, id=id)
    serializer = HomeSerializer(home)
    return Response(serializer.data)
  
  def put(self, request, id, *args, **kwargs):
    home = get_object_or_404(Home, id=id)
    serializer = HomeSerializer(home, data=request.data)
    if serializer.is_valid():
      serializer.save()
      return Response({"message": "Home updated successfully", "email": home.email, "address": home.address})
    return Response(serializer.errors, status=400)
  
  def delete(self, request, id, *args, **kwargs):
    home = get_object_or_404(Home, id=id)
    home.delete()
    return Response({"message": "Home deleted successfully"})
home_id_api_view = HomeIdAPIView.as_view()

# /api/homes/emails/
class HomeEmailsAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]

  def get(self, request, *args, **kwargs):
    homes = Home.objects.values('id', 'email')
    return Response(homes)
home_emails_api_view = HomeEmailsAPIView.as_view()

# /api/people/
class PersonAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]
  
  def post(self, request, *args, **kwargs):
    serializer = PersonSaveSerializer(data=request.data, context={"request": request})
    if serializer.is_valid():
      person = serializer.save()
      return Response({"message": "Person registered successfully", "name": person.name})
    return Response(serializer.errors, status=400)
  
  def get(self, request, *args, **kwargs):
    persons = Person.objects.all()
    serializer = PersonSerializer(persons, many=True)
    return Response(serializer.data)
person_api_view = PersonAPIView.as_view()

# /api/people/select/
class PersonSelectAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]
  
  def get(self, request, *args, **kwargs):
    persons = Person.objects.values('id', 'name')
    return Response(persons)
person_select_api_view = PersonSelectAPIView.as_view()

# /api/people/<id>/
class PersonIdAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]
  
  def get(self, request, id, *args, **kwargs):
    person = get_object_or_404(Person, id=id)
    serializer = PersonSerializer(person)
    return Response(serializer.data)
  
  def put(self, request, id, *args, **kwargs):
    person = get_object_or_404(Person, id=id)
    serializer = PersonSaveSerializer(person, data=request.data, context={"request": request})
    if serializer.is_valid():
      serializer.save()
      return Response({"message": "Person updated successfully", "name": person.name})
    return Response(serializer.errors, status=400)
  
  def delete(self, request, id, *args, **kwargs):
    person = get_object_or_404(Person, id=id)
    person.delete()
    return Response({"message": "Person deleted successfully"})
person_id_api_view = PersonIdAPIView.as_view()

# /api/devices/
class DeviceAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]
  
  def post(self, request, *args, **kwargs):
    print(request.data)
    serializer = DeviceCreateSerializer(data=request.data, context={"request": request})
    if serializer.is_valid():
      device = serializer.save()
      return Response(DeviceSerializer(device).data, status=status.HTTP_201_CREATED)
    return Response(serializer.errors, status=400)
  
  def get(self, request, *args, **kwargs):
    devices = Device.objects.all()
    serializer = DeviceSerializer(devices, many=True)
    return Response(serializer.data)

device_api_view = DeviceAPIView.as_view()

# /api/devices/<id>/
class DeviceIdAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]
  
  def get(self, request, id, *args, **kwargs):
    device = get_object_or_404(Device, id=id)
    serializer = DeviceSerializer(device)
    return Response(serializer.data)
  
  def put(self, request, id, *args, **kwargs):
    print(id, request.data)
    device = get_object_or_404(Device, id=id)
    serializer = DeviceUpdateSerializer(device, data=request.data, context={"request": request})
    if serializer.is_valid():
      serializer.save()
      return Response({"message": "Device updated successfully", "name": device.name})
    return Response(serializer.errors, status=400)
  
  def delete(self, request, id, *args, **kwargs):
    device = get_object_or_404(Device, id=id)
    device.delete()
    return Response({"message": "Device deleted successfully"})

device_id_api_view = DeviceIdAPIView.as_view()

class HistoryAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]
  
  def post(self, request, *args, **kwargs):
    serializer = HistoryCreateSerializer(data=request.data, context={"request": request})
    if serializer.is_valid():
      history = serializer.save()
      return Response(HistorySerializer(history).data, status=status.HTTP_201_CREATED)
    return Response(serializer.errors, status=400)
  
  def get(self, request, *args, **kwargs):
    histories = History.objects.all()
    serializer = HistorySerializer(histories, many=True)
    return Response(serializer.data)

history_api_view = HistoryAPIView.as_view()

# /api/histories/<id>/
class HistoryIdAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]
  
  def get(self, request, id, *args, **kwargs):
    history = get_object_or_404(History, id=id)
    serializer = HistorySerializer(history)
    return Response(serializer.data)

history_id_api_view = HistoryIdAPIView.as_view()


# COMMON API
# api/tokens/refresh/
class RefreshTokenAPIView(APIView):
  permission_classes  = [AllowAny]

  def post(self, request):
    try:
      refresh = request.data['refresh']
      if not refresh:
        return Response({'error': 'Refresh token is required'}, status=status.HTTP_400_BAD_REQUEST)
      token = RefreshToken(token=refresh)
      user_id = token.payload.get('user_id')
      try:
        user = Home.objects.get(id=user_id)
      except Home.DoesNotExist:
        return Response({'error': 'User not found'}, status=status.HTTP_404_NOT_FOUND)
      tokens = get_tokens_for_user(user)
      return Response({"message": "Token refreshed successfully", "tokens": tokens})
    except Exception as e:
      return Response({"message": str(e)}, status=400)
refresh_token_api_view = RefreshTokenAPIView.as_view()
