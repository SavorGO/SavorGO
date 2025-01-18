# Generated by Django 5.1.5 on 2025-01-17 10:45

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('table_management', '0002_alter_table_status'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='table',
            name='is_reserved',
        ),
        migrations.AlterField(
            model_name='table',
            name='status',
            field=models.CharField(choices=[('AVAILABLE', 'Available'), ('OUT_OF_SERVICE', 'Out of Service'), ('OCCUPIED', 'Occupied'), ('NEEDS_CLEANING', 'Needs Cleaning'), ('DELETED', 'Deleted')], default='OUT_OF_SERVICE', max_length=20),
        ),
    ]
