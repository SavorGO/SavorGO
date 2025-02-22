from rest_framework import serializers
from menu_management.models import Menu, Size, Option, Status
from promotion_management.models import Promotion
from datetime import date
from django.db.models import Q
import logging
logger = logging.getLogger("django")  # Sử dụng logger của Django

# Serializer cho Size (EmbeddedDocument)
class SizeSerializer(serializers.Serializer):
    size_name = serializers.CharField(max_length=50, required=False)
    price_change = serializers.FloatField(required=False)

# Serializer cho Option (EmbeddedDocument)
class OptionSerializer(serializers.Serializer):
    option_name = serializers.CharField(max_length=100, required=False)
    price_change = serializers.FloatField(required=False)

# Serializer cho Menu (Main Document)
class MenuSerializer(serializers.Serializer):
    id = serializers.CharField(read_only=True)  # Thêm trường id vào để lấy giá trị từ MongoDB
    name = serializers.CharField(max_length=255)
    category = serializers.CharField(max_length=100, required=False)
    description = serializers.CharField(required=False, allow_blank=True)
    original_price = serializers.FloatField()
    sale_price = serializers.FloatField()
    discounted_price = serializers.SerializerMethodField()
    public_id = serializers.CharField(required=False)
    sizes = SizeSerializer(many=True, required=False, default=[])  # Đặt giá trị mặc định là một danh sách rỗng
    options = OptionSerializer(many=True, required=False, default=[])  # Đặt giá trị mặc định là một danh sách rỗng
    status = serializers.CharField(max_length=50, required=False, allow_null=True, default="Discontinued")
    created_time = serializers.DateTimeField(read_only=True)  # Read-only
    modified_time = serializers.DateTimeField(read_only=True)  # Read-only

    class Meta:
        model = Menu
        fields = [
            "id", "name", "category", "description",
            "original_price", "sale_price", "discounted_price",
            "public_id", "sizes", "options", "status",
            "created_time", "modified_time"
        ]

    def get_discounted_price(self, obj):
        """Tính giá sau khi áp dụng khuyến mãi tối ưu nhất."""
        menu_id = obj.id
        sale_price = obj.sale_price
        original_price = obj.original_price  # Đảm bảo giá không giảm quá original_price

        # Lấy danh sách khuyến mãi hợp lệ từ MariaDB
        today = date.today()
        promotions = Promotion.objects.filter(
            Q(menu_id=menu_id),
            Q(status="AVAILABLE"),
            (Q(start_date__isnull=True) | Q(start_date__lte=today)),
            (Q(end_date__isnull=True) | Q(end_date__gte=today))
        )
        
        print(promotions.query)

        if not promotions.exists():
            return sale_price  # Không có khuyến mãi, giữ nguyên giá bán

        # Tìm khuyến mãi giảm giá nhiều nhất
        best_discounted_price = sale_price  # Khởi tạo với sale_price
        for promo in promotions:
            if promo.discount_type == "PERCENTAGE":
                discounted_price = sale_price * (1 - promo.discount_value / 100)
            elif promo.discount_type == "FIXED_AMOUNT":
                discounted_price = sale_price - promo.discount_value
            else:
                continue  # Bỏ qua khuyến mãi không hợp lệ

            # Lấy giá giảm nhiều nhất
            best_discounted_price = min(best_discounted_price, discounted_price)

        # Đảm bảo giá không thấp hơn original_price
        return max(best_discounted_price, original_price)



    # Tự định nghĩa phương thức create
    # Tự định nghĩa phương thức create
    def create(self, validated_data):
        sizes_data = validated_data.pop('sizes', [])  
        options_data = validated_data.pop('options', [])  

        # Chuyển đổi status và category thành chữ in hoa
        status = validated_data.get('status', 'DISCONTINUED').upper()  # Sử dụng DISCONTINUED in hoa
        validated_data['status'] = status
        validated_data['category'] = validated_data.get('category', '').upper()

        # Kiểm tra xem status có hợp lệ không
        if status not in [s.value for s in Status]:
            raise serializers.ValidationError({"status": "Invalid status value."})

        # Tạo menu mới mà không cần cung cấp id
        menu = Menu(**validated_data)  

        # Chỉ thêm sizes vào menu nếu không rỗng
        if sizes_data:
            for size_data in sizes_data:
                size = Size(**size_data)
                menu.sizes.append(size)

        # Chỉ thêm options vào menu nếu không rỗng
        if options_data:
            for option_data in options_data:
                option = Option(**option_data)
                menu.options.append(option)

        menu.save()  
        return menu


    def update(self, instance, validated_data):
        # Cập nhật các trường trong instance
        instance.name = validated_data.get('name', instance.name)
        instance.category = validated_data.get('category', instance.category).upper()  # Chuyển đổi thành chữ in hoa
        instance.description = validated_data.get('description', instance.description)
        instance.original_price = validated_data.get('original_price', instance.original_price)
        instance.sale_price = validated_data.get('sale_price', instance.sale_price)
        instance.public_id = validated_data.get('public_id', instance.public_id)
        instance.status = validated_data.get('status', instance.status).upper()  # Chuyển đổi thành chữ in hoa

        # Cập nhật sizes nếu có
        sizes_data = validated_data.get('sizes', [])
        if sizes_data:
            instance.sizes = [Size(**size_data) for size_data in sizes_data]

        # Cập nhật options nếu có
        options_data = validated_data.get('options', [])
        if options_data:
            instance.options = [Option(**option_data) for option_data in options_data]

        # Lưu đối tượng đã cập nhật
        instance.save()
        return instance
