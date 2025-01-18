from rest_framework.viewsets import ViewSet
from rest_framework.response import Response
from rest_framework import status
from .models import Menu
from .serializers import MenuSerializer

class MenuViewSet(ViewSet):
    def list(self, request):
        menus = Menu.objects()  # Sử dụng MongoEngine để lấy dữ liệu
        serializer = MenuSerializer(menus, many=True)
        return Response(serializer.data)

    def create(self, request):
        # Tạo serializer từ dữ liệu request
        serializer = MenuSerializer(data=request.data)
        
        # Kiểm tra tính hợp lệ của dữ liệu
        if serializer.is_valid():
            # Lưu đối tượng mới vào MongoDB
            menu = serializer.save()
            # Trả về response với mã trạng thái CREATED (201) và dữ liệu đối tượng mới
            return Response(MenuSerializer(menu).data, status=status.HTTP_201_CREATED)
        else:
            # Nếu dữ liệu không hợp lệ, trả về lỗi 400 với thông báo lỗi
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
