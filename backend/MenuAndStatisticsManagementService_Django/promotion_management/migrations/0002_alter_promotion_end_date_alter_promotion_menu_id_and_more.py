# Generated by Django 4.2.18 on 2025-02-19 03:28

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('promotion_management', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='promotion',
            name='end_date',
            field=models.DateField(blank=True, null=True),
        ),
        migrations.AlterField(
            model_name='promotion',
            name='menu_id',
            field=models.CharField(max_length=255),
        ),
        migrations.AlterField(
            model_name='promotion',
            name='start_date',
            field=models.DateField(blank=True, null=True),
        ),
        migrations.AlterModelTable(
            name='promotion',
            table='promotions',
        ),
    ]
