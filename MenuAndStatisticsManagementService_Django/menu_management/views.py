from rest_framework import status
from rest_framework.response import Response
from rest_framework.viewsets import ViewSet
from .models import Menu
from .serializers import MenuSerializer  # Giả sử bạn đã tạo serializer cho Menu

from rest_framework import viewsets
from .models import Menu
from .serializers import MenuSerializer

class MenuViewSet(viewsets.ModelViewSet):
    queryset = Menu.objects.using('mongodb').all()  # Lấy dữ liệu từ MongoDB
    serializer_class = MenuSerializer