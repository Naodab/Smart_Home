# Generated by Django 4.2.1 on 2025-04-13 11:10

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('api', '0012_remove_device_home_location_alter_device_location'),
    ]

    operations = [
        migrations.AlterField(
            model_name='home',
            name='password',
            field=models.CharField(max_length=120),
        ),
    ]
