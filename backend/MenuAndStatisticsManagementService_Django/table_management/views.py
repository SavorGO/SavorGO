from rest_framework import status
from rest_framework.viewsets import ViewSet
from rest_framework.response import Response
from .models import Table
from .serializers import TableSerializer
from datetime import datetime, timedelta
from .serializers import TableSerializer  # Giả sử bạn đã có một serializer cho Table
from django.utils import timezone  # Để lấy thời gian hiện tại
from django.db.models import Q
import logging
logger = logging.getLogger("myapp.api")  # Logger chỉ dành cho API

class TableViewSet(ViewSet):
    serializer_class = TableSerializer
    def list(self, request):
        # GET /tables/
        tables = Table.objects.all()
        serializer = TableSerializer(tables, many=True)
        return Response(serializer.data)

    def get_by_id(self, request, pk=None):
        # GET /tables/<int:pk>
        try:
            table = Table.objects.get(pk=pk)
        except Table.DoesNotExist:
            return Response({"error": "Table not found"}, status=status.HTTP_404_NOT_FOUND)

        # Serialize dữ liệu và trả về kết quả
        serializer = TableSerializer(table)
        return Response(serializer.data)

    def create(self, request):
        """ Xử lý POST /tables/ """
        logger.info("Received request to create a new table")  # Log khi nhận request
        serializer = TableSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)  # Middleware sẽ log lỗi nếu có
        table = serializer.save()

        logger.info(f"Table {table.id} created successfully")  # Log khi tạo thành công
        return Response({
            "message": "Table created successfully.",
            "data": {"table_id": table.id}
        }, status=status.HTTP_201_CREATED)


    def update_by_id(self, request, pk=None):
        # PUT /tables/<int:pk>
        try:
            table = Table.objects.get(pk=pk)
        except Table.DoesNotExist:
            return Response(status=status.HTTP_404_NOT_FOUND)

        # Lấy reservedTime từ request
        reserved_time = request.data.get('reservedTime')

        # Kiểm tra nếu reservedTime có sự thay đổi
        if reserved_time:
            # Chuyển đổi reservedTime từ chuỗi ISO 8601 thành datetime
            reserved_time = datetime.fromisoformat(reserved_time)

            # Nếu reservedTime khác với giá trị hiện tại và sau 7 ngày, trả về lỗi
            if reserved_time != table.reservedTime:
                if reserved_time > datetime.now() + timedelta(days=7):
                    return Response(
                        {"detail": "Reserved time cannot be more than 7 days in the future."},
                        status=status.HTTP_400_BAD_REQUEST
                    )

                # Nếu reservedTime trước thời gian hiện tại, trả về lỗi
                if reserved_time < datetime.now():
                    return Response(
                        {"detail": "Cannot change reserve time before now."},
                        status=status.HTTP_400_BAD_REQUEST
                    )

            # Định dạng lại reservedTime (nếu cần)
            reserved_time = reserved_time.isoformat()

            # Cập nhật reservedTime vào table
            table.reservedTime = reserved_time

        # Cập nhật modifiedTime
        table.modifiedTime = datetime.now().isoformat()

        # Cập nhật các trường khác từ request
        table.name = request.data.get('name', table.name)
        table.status = request.data.get('status', table.status)
        # Lưu bảng sau khi cập nhật
        table.save()

        # Trả về dữ liệu đã cập nhật
        serializer = TableSerializer(table)
        return Response(serializer.data)

    def delete(self, request, pk=None):
        # DELETE /tables/<int:pk>
        try:
            table = Table.objects.get(pk=pk)
        except Table.DoesNotExist:
            return Response({"error": "Table not found."}, status=status.HTTP_404_NOT_FOUND)

        # Cập nhật trạng thái của bảng thành DELETED thay vì xóa khỏi cơ sở dữ liệu
        table.status = 'DELETED'
        table.save()

        return Response({"message": "Table status updated to DELETED."}, status=status.HTTP_200_OK)
    def delete_many(self, request):
        """
        Xóa nhiều bảng từ danh sách ID.
        """
        # Lấy danh sách các ID từ request
        ids = request.data.get('ids', [])

        if not ids:
            return Response({"error": "IDs list is required."}, status=status.HTTP_400_BAD_REQUEST)

        # Lọc các bảng cần xóa theo ID
        tables = Table.objects.filter(id__in=ids)

        if not tables.exists():
            return Response({"error": "No tables found with the provided IDs."}, status=status.HTTP_404_NOT_FOUND)

        # Cập nhật trạng thái của các bảng thành 'DELETED'
        tables.update(status='DELETED')

        return Response({"message": f"{tables.count()} tables have been marked as DELETED."}, status=status.HTTP_200_OK)
    def search(self, request):
        search_query = request.GET.get('q', None)  # Nhận tham số tìm kiếm từ query string (ví dụ: ?q=a)

        # Lấy thời gian hiện tại
        now = timezone.now()

        if search_query:
            # Tạo điều kiện tìm kiếm cơ bản
            query = Q(name__icontains=search_query) | Q(status__icontains=search_query) | Q(reserved_time__icontains=search_query)

            # Kiểm tra xem reserved_time có rỗng không và so sánh reserved_time với thời gian hiện tại
            if search_query.lower() in "true":
                # Tạo điều kiện so sánh nếu search_query là "true"
                reserved_time_comparison = Q(reserved_time__gt=now)
                query |= reserved_time_comparison
            elif search_query.lower() in "false":
                # Tạo điều kiện nếu search_query là "false"
                reserved_time_comparison = Q(reserved_time__isnull=True)  # Nếu reserved_time rỗng
                query |= reserved_time_comparison

            # Tìm kiếm theo query đã tạo
            tables = Table.objects.filter(query)
            serializer = TableSerializer(tables, many=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        else:
            return Response({"detail": "No search query provided"}, status=status.HTTP_400_BAD_REQUEST)
