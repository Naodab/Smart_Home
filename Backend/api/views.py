from rest_framework import generics, mixins, authentication, permissions
from rest_framework.response import Response
from rest_framework.parsers import MultiPartParser, FormParser
from rest_framework.views import APIView

from django.core.files.storage import default_storage

from .serializers import SpeechSerializer

from AI_Module.speech_recognition.speech_to_text import transfer_audio_to_text

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
        
        # Xử lý tệp âm thanh và email tại đây
        # Ví dụ: Lưu thông tin vào cơ sở dữ liệu hoặc gọi một dịch vụ khác

        transfer_audio_to_text()
        
        return Response({"message": "File uploaded successfully", "file_url": file_url, "email": email})
    return Response(serializer.errors, status=400)
  
speech_create_api_view = SpeechCreateAPIView.as_view()