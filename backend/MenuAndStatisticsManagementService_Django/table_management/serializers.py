from rest_framework import serializers
from .models import Table

class TableSerializer(serializers.ModelSerializer):
    class Meta:
        model = Table
        fields = ['id', 'name', 'status', 'reserved_time', 'created_time', 'modified_time']
    def validate_name(self, value):
        if not value.strip():
            raise serializers.ValidationError("Name cannot be empty.")
        if len(value) > 255:
            raise serializers.ValidationError("Name cannot exceed 255 characters.")
        return value
