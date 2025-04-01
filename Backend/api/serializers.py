from rest_framework import serializers
from api.models import Device, History, Home, Person, HomePerson

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
            "address": obj.home.address
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
        fields = ['id', 'name', 'status', 'type', 'home', 'histories']

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
        fields = ['id', 'name', 'status', 'type']

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
        fields = ['id', 'name', 'status', 'type']

class HomeSerializer(serializers.ModelSerializer):
    persons = serializers.SerializerMethodField()
    devices = DeviceSerializer(many=True, read_only=True)

    class Meta:
        model = Home
        fields = ['id', 'email', 'address', 'persons', 'devices']

    def get_persons(self, obj):
        persons = obj.home_persons.all()
        return [{
            "id": hp.person.id, 
            "name": hp.person.name
        } for hp in persons]

class HomeMobileSerializer(serializers.ModelSerializer):
    devices = DeviceSerializer(many=True, read_only=True)

    class Meta:
        model = Home
        fields = ['id', 'email', 'address', 'temperature', 'humidity', 'devices']

class PersonSerializer(serializers.ModelSerializer):
    homes = serializers.SerializerMethodField()
    histories = serializers.SerializerMethodField()

    class Meta:
        model = Person
        fields = ['id', 'name', 'homes', 'histories']

    def get_homes(self, obj):
        homes = obj.person_homes.all()
        return [{
            "id": hp.home.id, 
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

class HomePersonAddSerializer(serializers.Serializer):
    home_id = serializers.IntegerField()
    person_ids = serializers.ListField(child=serializers.IntegerField(), allow_empty=False)

    def validate_home_id(self, value):
        if not Home.objects.filter(id=value).exists():
            raise serializers.ValidationError(f"Home with id {value} does not exist")
        return value

    def validate_person_ids(self, value):
        persons = Person.objects.filter(id__in=value)
        if persons.count() != len(value):
            existing_ids = set(persons.values_list("id", flat=True))
            invalid_ids = [pid for pid in value if pid not in existing_ids]
            raise serializers.ValidationError({"person_ids": f"Some person IDs do not exist: {invalid_ids}"})
        return value

    def create(self, validated_data):
        home_id = validated_data["home_id"]
        person_ids = validated_data["person_ids"]

        home = Home.objects.get(id=home_id)

        existing_person_ids = set(HomePerson.objects.filter(home=home).values_list("person_id", flat=True))

        new_home_persons = [
            HomePerson(home=home, person=Person.objects.get(id=pid))
            for pid in person_ids if pid not in existing_person_ids
        ]

        if new_home_persons:
            HomePerson.objects.bulk_create(new_home_persons)

        return home

    def delete(self, validated_data):
        home_id = validated_data["home_id"]
        person_ids = validated_data["person_ids"]

        home = Home.objects.get(id=home_id)

        deleted_count, _ = HomePerson.objects.filter(home=home, person_id__in=person_ids).delete()

        if deleted_count == 0:
            raise serializers.ValidationError({"detail": "No matching HomePerson records found to delete."})

        return home