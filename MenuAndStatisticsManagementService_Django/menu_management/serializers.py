# menu_management/serializers.py

from rest_framework import serializers
from .models import Menu

class MenuSerializer(serializers.ModelSerializer):
    class Meta:
        model = Menu
        #fields = '__all__'
        fields = [
            '_id', 'name', 'category', 'description', 
            'original_price', 'sale_price', 'image_url', 
            'sizes', 'options', 'status', 'created_time', 'modified_time'
        ]