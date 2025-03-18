from rest_framework import serializers
from .models import Table
from django.utils import timezone
from datetime import timedelta

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

    def validate_reserved_time(self, value):
        if value is None:  # Không kiểm tra nếu reserved_time không có trong request
            return value

        now = timezone.now()
        max_future_time = now + timedelta(days=7)

        if value > max_future_time:
            raise serializers.ValidationError("Reserved time cannot be more than 7 days in the future.")
        if value < now:
            raise serializers.ValidationError("Cannot change reserved time to a past date.")
        return value