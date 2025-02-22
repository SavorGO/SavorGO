from rest_framework import serializers
from .models import Promotion
from datetime import datetime

class PromotionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Promotion
        fields = '__all__'

    def validate_name(self, value):
        """Validate name length and empty values."""
        value = value.strip()
        if not value:
            raise serializers.ValidationError("Name cannot be empty.")
        if len(value) > 255:
            raise serializers.ValidationError("Name cannot exceed 255 characters.")
        return value

    def validate_discount_type(self, value):
        """Ensure discount_type is valid."""
        if value not in Promotion.DiscountType.values:
            raise serializers.ValidationError(f'"{value}" is not a valid choice.')
        return value


    def validate_status(self, value):
        """Ensure status is one of the allowed choices."""
        if value not in Promotion.Status.values:
            raise serializers.ValidationError("Invalid status.")
        return value

    def validate_start_date(self, value):
        """Ensure start_date is not in the past."""
        if value and value < datetime.now().date():
            raise serializers.ValidationError("Start date cannot be in the past.")
        return value

    def validate(self, data):
        """Cross-field validation for start_date, end_date, and discount_value based on discount_type."""
        start_date = data.get("start_date")
        end_date = data.get("end_date")
        discount_value = data.get("discount_value")
        discount_type = data.get("discount_type")

        if start_date and end_date and end_date < start_date:
            raise serializers.ValidationError({"end_date": "End date cannot be before start date."})

        if not data.get("menu_id"):
            raise serializers.ValidationError({"menu_id": "Menu ID cannot be empty."})

        if discount_type not in Promotion.DiscountType.values:
            raise serializers.ValidationError({"discount_type": "Invalid discount type."})

        if discount_type == "PERCENTAGE" and (discount_value < 0 or discount_value > 100):
            raise serializers.ValidationError({"discount_value": "Percentage discount must be between 0 and 100."})

        if discount_type == "FIXED_AMOUNT" and (discount_value < 0 or discount_value > 9223372036854775807):
            raise serializers.ValidationError({"discount_value": "Fixed amount discount is too large."})

        return data

