from django.core.management.base import BaseCommand
from django.contrib.auth import get_user_model

User = get_user_model()

class Command(BaseCommand):
    help = 'Create a superuser with email authentication'

    def add_arguments(self, parser):
        parser.add_argument('--email', required=True)
        parser.add_argument('--password', required=True)
        parser.add_argument('--address', default="123 Kieu Son Den")

    def handle(self, *args, **options):
        email = options['email']
        password = options['password']
        address = options['address']
        
        try:
            user = User.objects.create_superuser(
                email=email,
                password=password,
                address=address
            )
            self.stdout.write(self.style.SUCCESS(f'Successfully created superuser with email: {user.email}'))
        except Exception as e:
            self.stdout.write(self.style.ERROR(f'Failed to create superuser: {e}'))