# Generated by Django 4.2.1 on 2025-04-15 02:49

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('api', '0015_alter_device_status'),
    ]

    operations = [
        migrations.AlterField(
            model_name='history',
            name='status',
            field=models.CharField(choices=[('on', 'On'), ('off', 'Off'), ('open', 'Open'), ('close', 'Close'), ('0', '0'), ('1', '1'), ('2', '2'), ('3', '3')], default='off', max_length=10),
        ),
    ]
