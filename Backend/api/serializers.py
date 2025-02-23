from rest_framework import serializers

class SpeechSerializer(serializers.Serializer):
  file = serializers.FileField()
  email = serializers.EmailField()