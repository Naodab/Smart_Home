from rest_framework import generics, mixins, authentication, permissions
from rest_framework.response import Response
from rest_framework.parsers import MultiPartParser, FormParser
from rest_framework.views import APIView

from django.core.files.storage import default_storage

from .serializers import PersonSerializer, SpeechSerializer, LoginSerializer, RegisterSerializer, HomeSerializer

from AI_Module.speech_recognition.speech_to_text import transfer_audio_to_text
from AI_Module.speaker_recognition.test import identify_speaker

from django.shortcuts import get_object_or_404
from api.models import Home, Person

# /api/speeches/upload/
class SpeechCreateAPIView(APIView):
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

# /api/homes/login/
class LoginAPIView(APIView):
  def post(self, request, *args, **kwargs):
    serializer = LoginSerializer(data=request.data)
    if serializer.is_valid():
      email = serializer.validated_data['email']
      password = serializer.validated_data['password']

      home = get_object_or_404(Home, email=email)
      if home.check_password(password):
        return Response({"message": "Login successful", "email": email, "password": password})
      return Response({"message": "Invalid credentials"}, status=401)
    return Response(serializer.errors, status=400)
  
login_api_view = LoginAPIView.as_view()

# /api/homes/
class HomeAPIView(APIView):
  def post(self, request, *args, **kwargs):
    serializer = RegisterSerializer(data=request.data)
    if serializer.is_valid():
      home = serializer.save()
      return Response({"message": "Home registered successfully", "email": home.email, "address": home.address})
    return Response(serializer.errors, status=400)
  
  def get(self, request, *args, **kwargs):
    homes = Home.objects.all()
    serializer = HomeSerializer(homes, many=True)
    return Response(serializer.data)
home_api_view = HomeAPIView.as_view()

# /api/homes/<id>/
class HomeIdAPIView(APIView):
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

# /api/people/
class PersonAPIView(APIView):
  def post(self, request, *args, **kwargs):
    serializer = PersonSerializer(data=request.data)
    if serializer.is_valid():
      person = serializer.save()
      return Response({"message": "Person registered successfully", "name": person.name})
    return Response(serializer.errors, status=400)
  
  def get(self, request, *args, **kwargs):
    persons = Person.objects.all()
    serializer = PersonSerializer(persons, many=True)
    return Response(serializer.data)
person_api_view = PersonAPIView.as_view()

# /api/people/<id>/
class PersonIdAPIView(APIView):
  def get(self, request, id, *args, **kwargs):
    person = get_object_or_404(Person, id=id)
    serializer = PersonSerializer(person)
    return Response(serializer.data)
  
  def put(self, request, id, *args, **kwargs):
    person = get_object_or_404(Person, id=id)
    serializer = PersonSerializer(person, data=request.data)
    if serializer.is_valid():
      serializer.save()
      return Response({"message": "Person updated successfully", "name": person.name})
    return Response(serializer.errors, status=400)
  
  def delete(self, request, id, *args, **kwargs):
    person = get_object_or_404(Person, id=id)
    person.delete()
    return Response({"message": "Person deleted successfully"})
person_id_api_view = PersonIdAPIView.as_view()
