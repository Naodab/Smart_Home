from django.db.models import Q
from rest_framework import serializers
from api.models import Device, History, Home, Person, Location

class SpeechSerializer(serializers.Serializer):
    file = serializers.FileField()
    email = serializers.CharField()

class LoginSerializer(serializers.Serializer):
    email = serializers.CharField()
    password = serializers.CharField()

class DeviceSerializer(serializers.ModelSerializer):
    home = serializers.SerializerMethodField()
    histories = serializers.SerializerMethodField()

    def get_home(self, obj):
        return {
            "id": obj.home.id,
            "email": obj.home.email,
            "address": obj.home.address,
            "temperature": obj.home.temperature,
            "humidity": obj.home.humidity
        }

    def get_histories(self, obj):
        histories = obj.histories.all()
        return [{
            "id": h.id,
            "person_id": h.person.id,
            "person_name": h.person.name,
            "new_status": h.status,
            "timestamp": h.time
        } for h in histories]

    class Meta:
        model = Device
        fields = ['id', 'name', 'location', 'status', 'type', 'home', 'histories']

class DeviceCreateSerializer(serializers.ModelSerializer):
    def create(self, validated_data):
        home_data = self.context["request"].data.get('home', None)
        home_email = home_data.get('email') if home_data else None
        
        if not home_email:
            raise serializers.ValidationError({"home": "Home email is required"})
        try:
            home = Home.objects.get(email=home_email)
        except Home.DoesNotExist:
            raise serializers.ValidationError({"home": f"Home with email {home_email} does not exist"})

        device = Device.objects.create(home=home, **validated_data)
        return device

    class Meta:
        model = Device
        fields = ['id', 'name', 'location', 'status', 'type']

class DeviceUpdateSerializer(serializers.ModelSerializer):
    def update(self, instance, validated_data):
        home_data = self.context["request"].data.get('home', None)

        if home_data:
            home_email = home_data.get("email")
            if not home_email:
                raise serializers.ValidationError({"home": "Home email is required"})
            try:
                home = Home.objects.get(email=home_email)
            except Home.DoesNotExist:
                raise serializers.ValidationError({"home": f"Home with email {home_email} does not exist"})
            
            instance.home = home

        for attr, value in validated_data.items():
            setattr(instance, attr, value)

        instance.save()
        return instance

    class Meta:
        model = Device
        fields = ['id', 'name', 'location', 'status', 'type']

class LocationSerializer(serializers.ModelSerializer):
    home = serializers.SerializerMethodField()
    devices = serializers.SerializerMethodField()
    def get_devices(self, obj):
        devices = obj.devices.all()
        return [
            {
                "id": d.id,
                "name": d.name,
                "status": d.status,
                "type": d.type
            } for d in devices if d.home == obj.home and d.location == obj
        ]
    
    def get_home(self, obj):
        return {
            "id": obj.home.id,
            "email": obj.home.email,
            "address": obj.home.address,
            "temperature": obj.home.temperature,
            "humidity": obj.home.humidity
        }

    class Meta:
        model = Device
        fields = ['id', 'name', 'home', 'devices']

class LocationSaveSerializer(serializers.ModelSerializer):
    def create(self, validated_data):
        home_data = self.context["request"].data.get('home', None)
        home_email = home_data.get("email") if home_data else None
        if not home_email:  
            raise serializers.ValidationError({"home": "Home email is required"})
        try:
            home = Home.objects.get(email=home_email)
        except Home.DoesNotExist:
            raise serializers.ValidationError({"home": f"Home with email {home_email} does not exist"})
        location = Location.objects.create(home=home, **validated_data)
        return location
    
    def update(self, instance, validated_data):
        home_data = self.context["request"].data.get('home', None)
        home_email = home_data.get("email") if home_data else None
        if not home_email:  
            raise serializers.ValidationError({"home": "Home email is required"})
        try:
            home = Home.objects.get(email=home_email)
        except Home.DoesNotExist:
            raise serializers.ValidationError({"home": f"Home with email {home_email} does not exist"})
        instance.home = home
        for attr, value in validated_data.items():
            setattr(instance, attr, value)
        instance.save()
        return instance

    class Meta:
        model = Device
        fields = ['id', 'name']

class HomeSerializer(serializers.ModelSerializer):
    persons = serializers.SerializerMethodField()
    locations = serializers.SerializerMethodField()
    class Meta:
        model = Home
        fields = ['id', 'email', 'address', 'persons', 'locations', 'temperature', 'humidity']

    def get_persons(self, obj):
        persons = obj.people.all()
        return [{
            "id": hp.id,
            "name": hp.name
        } for hp in persons]
    
    def get_locations(self, obj):
        locations = obj.locations.all()
        return [{
            "id": hl.id,
            "name": hl.name
        } for hl in locations]

class HomeMobileSerializer(serializers.ModelSerializer):
    locations = serializers.SerializerMethodField()

    def get_locations(self, obj):
        locations = obj.locations.all()
        return [{
            "id": hl.id,
            "name": hl.name,
            "devices": [
                {
                    "id": d.id,
                    "name": d.name,
                    "status": d.status,
                    "type": d.type
                } for d in hl.devices.all() if d.home == obj
            ]
        } for hl in locations]

    class Meta:
        model = Home
        fields = ['id', 'email', 'address', 'temperature', 'humidity', 'locations']

class PersonSaveSerializer(serializers.ModelSerializer):
    def create(self, validated_data):
        home_data = self.context["request"].data.get('home', None)
        home_email = home_data.get("email") if home_data else None
        if not home_email:  
            raise serializers.ValidationError({"home": "Home email is required"})
        try:
            home = Home.objects.get(email=home_email)
        except Home.DoesNotExist:
            raise serializers.ValidationError({"home": f"Home with email {home_email} does not exist"})
        person = Person.objects.create(home=home, **validated_data)
        return person

    def update(self, instance, validated_data):
        home_data = self.context["request"].data.get('home', None)
        home_email = home_data.get("email") if home_data else None
        if not home_email:
            raise serializers.ValidationError({"home": "Home email is required"})
        try:
            home = Home.objects.get(email=home_email)
        except Home.DoesNotExist:
            raise serializers.ValidationError({"home": f"Home with email {home_email} does not exist"})
        instance.home = home
        for attr, value in validated_data.items():
            setattr(instance, attr, value)
        instance.save()
        return instance

    class Meta:
        model = Person
        fields = ['id', 'name']

class PersonSerializer(serializers.ModelSerializer):
    home = serializers.SerializerMethodField()
    histories = serializers.SerializerMethodField()

    class Meta:
        model = Person
        fields = ['id', 'name', 'home', 'histories']

    def get_home(self, obj):
        return {
            "id": obj.home.id,
            "email": obj.home.email,
            "address": obj.home.address,
            "temperature": obj.home.temperature,
            "humidity": obj.home.humidity
        }

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

class RegisterSerializer(serializers.ModelSerializer):
    class Meta:
        model = Home
        fields = ['email', 'address']

    def validate_email(self, value):
        if Home.objects.filter(email=value).exists():
            raise serializers.ValidationError("Email already exists")
        return value