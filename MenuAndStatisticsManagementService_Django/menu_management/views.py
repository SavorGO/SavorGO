from rest_framework.viewsets import ViewSet
from rest_framework.response import Response
from rest_framework import status
from mongoengine.queryset.visitor import Q
from datetime import datetime
from .models import Menu
from .serializers import MenuSerializer


class MenuViewSet(ViewSet):
    def list(self, request):
        # GET /menus/
        menus = Menu.objects.all()
        serializer = MenuSerializer(menus, many=True)
        return Response(serializer.data)

    def get_by_id(self, request, pk=None):
        # GET /menus/<pk>
        try:
            menu = Menu.objects.get(pk=pk)
        except Menu.DoesNotExist:
            return Response({"error": "Menu not found"}, status=status.HTTP_404_NOT_FOUND)

        serializer = MenuSerializer(menu)
        return Response(serializer.data)

    def create(self, request):
        # POST /menus/
        serializer = MenuSerializer(data=request.data)
        if serializer.is_valid():
            menu = serializer.save()
            return Response(MenuSerializer(menu).data, status=status.HTTP_201_CREATED)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def update_by_id(self, request, pk=None):
        # PUT /menus/<pk>
        try:
            menu = Menu.objects.get(pk=pk)
        except Menu.DoesNotExist:
            return Response({"error": "Menu not found"}, status=status.HTTP_404_NOT_FOUND)

        # Update các trường từ request
        serializer = MenuSerializer(menu, data=request.data, partial=True)
        if serializer.is_valid():
            menu = serializer.save()
            return Response(MenuSerializer(menu).data)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk=None):
        # DELETE /menus/<pk>
        try:
            menu = Menu.objects.get(pk=pk)
        except Menu.DoesNotExist:
            return Response({"error": "Menu not found"}, status=status.HTTP_404_NOT_FOUND)

        # Đánh dấu trạng thái là 'DELETED' thay vì xóa thực sự
        menu.status = "DELETED"
        menu.save()
        return Response({"message": "Menu status updated to DELETED"}, status=status.HTTP_200_OK)

    def delete_many(self, request):
        # DELETE /menus/
        ids = request.data.get('ids', [])
        if not ids:
            return Response({"error": "IDs list is required."}, status=status.HTTP_400_BAD_REQUEST)

        menus = Menu.objects.filter(id__in=ids)
        if not menus:
            return Response({"error": "No menus found with the provided IDs."}, status=status.HTTP_404_NOT_FOUND)

        menus.update(status="DELETED")
        return Response({"message": f"{menus.count()} menus have been marked as DELETED."}, status=status.HTTP_200_OK)

    def search(self, request):
        # GET /menus/search?q=query
        search_query = request.GET.get('q', None)
        if not search_query:
            return Response({"error": "No search query provided"}, status=status.HTTP_400_BAD_REQUEST)

        query = Q(name__icontains=search_query) | Q(category__icontains=search_query) | Q(description__icontains=search_query) | Q(status__icontains=search_query)

        # Tìm kiếm trong cơ sở dữ liệu
        menus = Menu.objects.filter(query)
        serializer = MenuSerializer(menus, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)
