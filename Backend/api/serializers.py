from django.db.models import Q
from rest_framework import serializers
from api.models import Device, History, Home, Person, Location

class SpeechSerializer(serializers.Serializer):
    file = serializers.FileField()
    email = serializers.CharField()

class SpeechRemoteSerializer(serializers.Serializer):
    file = serializers.FileField()
    email = serializers.CharField()
    person_id = serializers.IntegerField()

class LoginSerializer(serializers.Serializer):
    email = serializers.CharField()
    password = serializers.CharField()

class DeviceSerializer(serializers.ModelSerializer):
    location = serializers.SerializerMethodField()
    histories = serializers.SerializerMethodField()

    def get_location(self, obj):
        if obj.location is None:
            return None
        return {
            "id": obj.location.id,
            "name": obj.location.name,
            "home": {
                "id": obj.location.home.id,
                "email": obj.location.home.email,
                "address": obj.location.home.address,
                "temperature": obj.location.home.temperature,
                "humidity": obj.location.home.humidity
            }
        }

    def get_histories(self, obj):
        histories = obj.histories.all()
        return [{
            "id": h.id,
            "new_status": h.status,
            "timestamp": h.time,
            "person": {
                "id": h.person.id,
                "name": h.person.name
            }
        } for h in histories]

    class Meta:
        model = Device
        fields = ['id', 'name', 'location', 'status', 'type', 'histories']

class DeviceCreateSerializer(serializers.ModelSerializer):
    def create(self, validated_data):
        request_data = self.context["request"].data
        print(request_data)

        home_data = request_data.get('home', None)
        home_email = home_data.get('email')
        if not home_email:
            raise serializers.ValidationError({"home": "Home email is required"})
        
        try:
            home = Home.objects.get(email=home_email)
        except Home.DoesNotExist:
            raise serializers.ValidationError({"home": f"Home with email {home_email} does not exist"})

        location_data = request_data.get('location', None)
        print(location_data)
        location_name = location_data.get('name')
        location = None
        if location_name:
            location, _ = Location.objects.get_or_create(name=location_name, home=home)

        device = Device.objects.create(
            location=location,
            **validated_data
        )
        return device

    class Meta:
        model = Device
        fields = ['id', 'name', 'status', 'type']


class DeviceUpdateSerializer(serializers.ModelSerializer):
    def update(self, instance, validated_data):
        request_data = self.context["request"].data

        home_data = request_data.get('home', {})
        home_email = home_data.get('email')
        if home_email:
            try:
                home = Home.objects.get(email=home_email)
                instance.home = home
            except Home.DoesNotExist:
                raise serializers.ValidationError({"home": f"Home with email {home_email} does not exist"})

        location_data = request_data.get('location', {})
        location_name = location_data.get('name')
        if location_name and instance.home:
            location, _ = Location.objects.get_or_create(name=location_name, home=instance.home)
            instance.location = location

        for attr, value in validated_data.items():
            setattr(instance, attr, value)

        instance.save()
        return instance

    class Meta:
        model = Device
        fields = ['id', 'name', 'status', 'type']


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
            } for d in devices
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
                } for d in hl.devices.all()
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
    
class HistoryUserSerializer(serializers.ModelSerializer):
    person = serializers.SerializerMethodField()

    def get_person(self, obj):
        if obj.person is None:
            return None
        return {
            "id": obj.person.id,
            "name": obj.person.name
        }

    class Meta:
        model = History
        fields = ['id', 'person', 'status', 'time']

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
    
    def create(self, validated_data):
        email = validated_data['email']
        address = validated_data.get('address', '')

        user = Home.objects.create_user(email=email, address=address)
        return user