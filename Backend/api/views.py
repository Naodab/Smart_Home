from rest_framework import status
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.response import Response
from rest_framework.parsers import MultiPartParser, FormParser
from rest_framework.views import APIView

from django.core.files.storage import default_storage
from rest_framework_simplejwt.tokens import RefreshToken

from api.permissions import IsAdmin
from api.tokens import get_tokens_for_user

from project.views import send_command_to_esp32

from .serializers import DeviceCreateSerializer, \
                          DeviceSerializer, \
                          DeviceUpdateSerializer, \
                          HomeMobileSerializer, \
                          PersonSaveSerializer, \
                          PersonSerializer, \
                          SpeechSerializer, \
                          LoginSerializer, \
                          RegisterSerializer, \
                          HomeSerializer, \
                          HistorySerializer, \
                          LocationSaveSerializer, \
                          LocationSerializer, \
                          HistoryUserSerializer, \
                          SpeechRemoteSerializer

from api.models import History, BlacklistedToken, Location

from AI_Module.speech_recognition.speech_to_text import transfer_audio_to_text
from AI_Module.speaker_recognition.test import identify_speaker
from AI_Module.speaker_recognition.verify import verify

from django.shortcuts import get_object_or_404
from api.models import Device, Home, Person
from rest_framework_simplejwt.authentication import JWTAuthentication
from rest_framework_simplejwt.exceptions import InvalidToken, TokenError
from datetime import datetime, timezone

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

        return Response({
          "message": "File uploaded successfully",
          "file_url": file_url,
          "email": email,
          "person_id": 1,
          "person_name": "Nguyen Ho Ba Doan",
        })
    return Response(serializer.errors, status=400)
  
speech_create_api_view = SpeechCreateAPIView.as_view()

# /api/speeches/remote/
class SpeechRemoteAPIView(APIView):
  permission_classes = [AllowAny]

  parser_classes = (MultiPartParser, FormParser)
  def post(self, request, *args, **kwargs):
    print(request.data)
    serializer = SpeechRemoteSerializer(data=request.data)
    if serializer.is_valid():
        file = serializer.validated_data['file']
        email = serializer.validated_data['email']
        person_id = serializer.validated_data['person_id']

        print(email)
        print(file)
        print(person_id)

        return Response({
          "id": 10,
          "name": "Quạt",
          "status": "0",
        })
    return Response(serializer.errors, status=400)
speech_remote_api_view  = SpeechRemoteAPIView.as_view()

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
        return Response({
          "message": "Login successful",
          "email": email,
          "tokens": tokens,
          "address": home.address,
          "id": home.id
        })
      return Response({"message": "Invalid credentials"}, status=401)
    return Response(serializer.errors, status=400)
user_login_api_view = UserLoginAPIView.as_view()

# /api/users/homes/<email>/
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

# /api/users/devices/<id>/
class DeviceUsersAPIView(APIView):
  permission_classes = [IsAuthenticated]

  def get(self, request, id, *args, **kwargs):
    device = get_object_or_404(Device, id=id)
    serializer = DeviceSerializer(device)
    device = serializer.data
    if request.user.id != device['home']['id']:
      return Response({"message": "Permission denied"}, status=403)
    return Response(device, status=status.HTTP_200_OK)
device_mobile_api_view = DeviceUsersAPIView.as_view()

# /api/users/devices/<id>/
class DeviceUsersIdAPIView(APIView):

  def get(self, request, id, *args, **kwargs):
    device = get_object_or_404(Device, id=id)
    serializer = DeviceSerializer(device)
    device = serializer.data
    if request.user.id != device['home']['id']:
      return Response({"message": "Permission denied"}, status=403)
    return Response(device, status=status.HTTP_200_OK)

  def post(self, request, id, *args, **kwargs):
    device = get_object_or_404(Device, id=id)
    serializer = DeviceSerializer(device, data=request.data)
    if serializer.is_valid():
      serializer.save()
      if request.user.id != device.home.id:
        return Response({"message": "Permission denied"}, status=403)
      return Response({"message": "Device updated successfully", "name": device.name})
    return Response(serializer.errors, status=400)

  def put(self, request, id, *args, **kwargs):
    print(request.data)
    device = get_object_or_404(Device, id=id)

    new_status = request.data.get('status')
    person_id = request.data.get('personId')
    device_id = request.data.get('id')

    send_command_to_esp32(device=device.type, state=new_status, room=device.location.name)

    # if request.user.id != device.location.home.id:
    #     return Response({"message": "Permission denied"}, status=403)

    if new_status not in dict(Device.DEVICE_STATUSES):
        return Response({"message": "Invalid status"}, status=400)

    person = get_object_or_404(Person, id=person_id)
    if not person:
        return Response({"message": "Person not found"}, status=404)

    device.status = new_status
    device.save()

    History.objects.create(
        device=device,
        status=new_status,
        person=person
    )

    return Response({"success": True}, status=status.HTTP_200_OK)
device_users_id_api_view = DeviceUsersIdAPIView.as_view()

# /api/users/histories/<device_id>/
class HistoryUsersAPIView(APIView):
  permission_classes = [IsAuthenticated]

  def get(self, request, device_id, *args, **kwargs):
    device = get_object_or_404(Device, id=device_id)
    if request.user.id != device.location.home.id:
      return Response({"message": "Permission denied"}, status=403)
    serializer = HistoryUserSerializer(device.histories, many=True)
    histories = serializer.data
    return Response(histories, status=status.HTTP_200_OK)
history_users_api_view = HistoryUsersAPIView.as_view()

# ADMIN API
# ONLY FOR ADMIN
class AdminLoginAPIView(APIView):
  permission_classes = [AllowAny]

  def post(self, request,  *args, **kwargs):
    serializer = LoginSerializer(data=request.data)
    if serializer.is_valid():
      email = serializer.validated_data['email']
      password = serializer.validated_data['password']

      print(email)
      print(password)

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
    homes = Home.objects.all()
    homes = [{"id": home.id, "email": home.email} for home in homes if home.is_staff == False]
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

# /api/locations/
class LocationAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]

  def post(self, request, *args, **kwargs):
    serializer = LocationSaveSerializer(data=request.data, context={"request": request})
    if serializer.is_valid():
      location = serializer.save()
      return Response({"message": "Location registered successfully", "name": location.name})
    return Response(serializer.errors, status=400)

  def get(self, request, *args, **kwargs):
    locations = Location.objects.all()
    serializer = LocationSerializer(locations, many=True)
    return Response(serializer.data)

location_api_view = LocationAPIView.as_view()

# /api/locations/<id>/
class LocationIdAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]
  def get(self, request, id, *args, **kwargs):
    location = get_object_or_404(Location, id=id)
    serializer = LocationSerializer(location)
    return Response(serializer.data)

  def put(self, request, id, *args, **kwargs):
    location = get_object_or_404(Location, id=id)
    serializer = LocationSaveSerializer(location, data=request.data, context={"request": request})
    if serializer.is_valid():
      serializer.save()
      return Response({"message": "Location updated successfully", "name": location.name})
    return Response(serializer.errors, status=400)

  def delete(self, request, id, *args, **kwargs):
    location = get_object_or_404(Location, id=id)
    location.delete()
    return Response({"message": "Location deleted successfully"})
location_id_api_view = LocationIdAPIView.as_view()

# /api/homes/<email>/locations/
class LocationInHomeAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]

  def get(self, request, email, *args, **kwargs):
    home = get_object_or_404(Home, email=email)
    locations = home.locations.all()
    serializer = LocationSerializer(locations, many=True)
    return Response(serializer.data)

location_in_home_api_view = LocationInHomeAPIView.as_view()
# /api/devices/
class DeviceAPIView(APIView):
  permission_classes = [IsAuthenticated, IsAdmin]

  def post(self, request, *args, **kwargs):
    serializer = DeviceCreateSerializer(data=request.data, context={"request": request})
    if serializer.is_valid():
      print(serializer.validated_data)
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
    serializer = HistorySerializer(data=request.data, context={"request": request})
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

# api/logout/
class LogoutView(APIView):
  permission_classes = [IsAuthenticated]

  def post(self, request):
    auth = JWTAuthentication()
    try:
      header = auth.get_header(request)
      raw_token = auth.get_raw_token(header)
      validated_token = auth.get_validated_token(raw_token)

      jti = validated_token.get("jti")
      if not jti:
          return Response({"detail": "Token does not contain jti"}, status=status.HTTP_400_BAD_REQUEST)

      exp_timestamp = validated_token.get("exp")
      exp_datetime = datetime.fromtimestamp(exp_timestamp, tz=timezone.utc)
      BlacklistedToken.objects.create(jti=jti, exp=exp_datetime)

      return Response({"detail": "Logged out successfully"}, status=status.HTTP_200_OK)

    except TokenError as e:
      return Response({"detail": "Invalid token"}, status=status.HTTP_400_BAD_REQUEST)
    except Exception as e:
      return Response({"detail": str(e)}, status=status.HTTP_400_BAD_REQUEST)
logout_view = LogoutView.as_view()

# api/change-password/
class ChangePasswordAPIView(APIView):
  permission_classes = [AllowAny]

  def post(self, request):
    user = request.user
    old_password = request.data.get('oldPass')
    new_password = request.data.get('newPass')

    if not user.check_password(old_password):
      return Response({"error": "Old password is incorrect", "success": False}, status=status.HTTP_400_BAD_REQUEST)

    user.set_password(new_password)
    user.save()

    return Response({"message": "Password changed successfully", "success": True}, status=status.HTTP_200_OK)
change_password_api_view = ChangePasswordAPIView.as_view()