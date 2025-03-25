from rest_framework import serializers
from api.models import Device, History, Home, Person, HomePerson

class SpeechSerializer(serializers.Serializer):
    file = serializers.FileField()
    email = serializers.CharField()

class LoginSerializer(serializers.Serializer):
    email = serializers.CharField()
    password = serializers.CharField()

class DeviceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Device
        fields = ['id', 'name', 'status']

class HomeSerializer(serializers.ModelSerializer):
    persons = serializers.SerializerMethodField()
    devices = DeviceSerializer(many=True, read_only=True)

    class Meta:
        model = Home
        fields = ['id', 'email', 'address', 'persons', 'devices']

    def get_persons(self, obj):
        persons = obj.home_persons.all()
        return [{
            "person_id": hp.person.id, 
            "name": hp.person.name
        } for hp in persons]

class PersonSerializer(serializers.ModelSerializer):
    homes = serializers.SerializerMethodField()
    histories = serializers.SerializerMethodField()

    class Meta:
        model = Person
        fields = ['id', 'name', 'homes', 'histories']

    def get_homes(self, obj):
        homes = obj.person_homes.all()
        return [{
            "home_id": hp.home.id, 
            "email": hp.home.email, 
            "address": hp.home.address
        } for hp in homes]

    def get_histories(self, obj):
        histories = obj.histories.all()
        return [{
            "id": h.id,
            "device_id": h.device.id,
            "device_name": h.device.name,
            "status": h.status,
            "time": h.time
        } for h in histories]

class HistorySerializer(serializers.ModelSerializer):
    device = serializers.PrimaryKeyRelatedField(queryset=Device.objects.all())
    person = serializers.PrimaryKeyRelatedField(queryset=Person.objects.all())

    class Meta:
        model = History
        fields = ['id', 'device', 'status', 'time', 'person']

class HomePersonSerializer(serializers.ModelSerializer):
    home = serializers.PrimaryKeyRelatedField(queryset=Home.objects.all())
    person = serializers.PrimaryKeyRelatedField(queryset=Person.objects.all())

    class Meta:
        model = HomePerson
        fields = ['home', 'person']

class RegisterSerializer(serializers.ModelSerializer):
    class Meta:
        model = Home
        fields = ['email', 'address']

    def validate_email(self, value):
        if Home.objects.filter(email=value).exists():
            raise serializers.ValidationError("Email already exists")
        return value