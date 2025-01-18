from rest_framework import serializers
from menu_management.models import Menu, Size, Option
import datetime

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
    description = serializers.CharField(required=False)
    original_price = serializers.FloatField()
    sale_price = serializers.FloatField()
    image_url = serializers.URLField(required=False)
    sizes = SizeSerializer(many=True, required=False, default=[])  # Đặt giá trị mặc định là một danh sách rỗng
    options = OptionSerializer(many=True, required=False, default=[])  # Đặt giá trị mặc định là một danh sách rỗng
    status = serializers.CharField(max_length=50)
    created_time = serializers.DateTimeField(read_only=True)  # Read-only
    modified_time = serializers.DateTimeField(read_only=True)  # Read-only

    class Meta:
        model = Menu
        fields = '__all__'

    # Tự định nghĩa phương thức create
    def create(self, validated_data):
        sizes_data = validated_data.pop('sizes', [])  # Nếu không có 'sizes', gán giá trị mặc định là danh sách rỗng
        options_data = validated_data.pop('options', [])  # Nếu không có 'options', gán giá trị mặc định là danh sách rỗng

        # Tạo menu mới mà không cần cung cấp id
        menu = Menu(**validated_data)  # Không dùng Menu.objects.create()

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

        # Lưu menu với các sizes và options đã thêm
        menu.save()  # MongoEngine sẽ tự động gán giá trị cho id khi lưu

        return menu

