from drf_spectacular.utils import extend_schema, OpenApiParameter, inline_serializer
from drf_spectacular.types import OpenApiTypes  # Thêm dòng này
from rest_framework import serializers
from rest_framework import status
from rest_framework.viewsets import ViewSet
from rest_framework.response import Response
from rest_framework.exceptions import NotFound
from .models import Table
from .serializers import TableSerializer
from datetime import datetime, timedelta
from .serializers import TableSerializer  # Giả sử bạn đã có một serializer cho Table
from django.utils import timezone  # Để lấy thời gian hiện tại
from django.db.models import Q
import logging
from rest_framework.pagination import PageNumberPagination
logger = logging.getLogger("myapp.api")  # Logger chỉ dành cho API

class TableViewSet(ViewSet):
    serializer_class = TableSerializer
    @extend_schema(
    parameters=[
        OpenApiParameter("keyword", OpenApiTypes.STR, OpenApiParameter.QUERY, description="Search by name"),
        OpenApiParameter("sortBy", OpenApiTypes.STR, OpenApiParameter.QUERY, description="Sort field (default: id)"),
        OpenApiParameter("sortDirection", OpenApiTypes.STR, OpenApiParameter.QUERY, description="Sort direction (asc/desc)"),
        OpenApiParameter("page", OpenApiTypes.INT, OpenApiParameter.QUERY, description="Page number"),
        OpenApiParameter("size", OpenApiTypes.INT, OpenApiParameter.QUERY, description="Items per page"),
    ],
    )
    def list(self, request):
        """Lấy danh sách bàn với tìm kiếm, sắp xếp, phân trang và ghi log"""
        keyword = request.query_params.get("keyword", "").strip()
        sort_by = request.query_params.get("sortBy", "id")
        sort_direction = request.query_params.get("sortDirection", "asc").lower()
        page = request.query_params.get("page", 1)
        size = request.query_params.get("size", 10)

        if sort_direction not in ["asc", "desc"]:
            sort_direction = "asc"
        ordering = f"-{sort_by}" if sort_direction == "desc" else sort_by

        tables = Table.objects.all()
        if keyword:
            tables = tables.filter(Q(name__icontains=keyword))

        tables = tables.order_by(ordering)

        paginator = PageNumberPagination()
        paginator.page_size = size
        paginated_tables = paginator.paginate_queryset(tables, request)

        serializer = TableSerializer(paginated_tables, many=True)
        response_data = {
            "status": status.HTTP_200_OK,
            "message": "Fetched tables successfully.",
            "data": serializer.data,
            "total_items": tables.count(),
            "total_pages": paginator.page.paginator.num_pages if tables.count() > 0 else 1,
        }

        logger.info(f"Tables fetched: {len(serializer.data)} items (Page: {page}, Size: {size})")

        return paginator.get_paginated_response(response_data)

    @extend_schema(
    parameters=[
        OpenApiParameter(name="id", description="Table ID", required=True, type=OpenApiTypes.INT, location=OpenApiParameter.PATH),
    ],
)
    def get_by_id(self, request, pk=None):
        """Lấy thông tin chi tiết của một bàn dựa trên ID, có log và tài liệu API"""
        try:
            table = Table.objects.get(pk=pk)
        except Table.DoesNotExist:
            logger.warning(f"Table with ID {pk} not found.")
            raise NotFound("Table not found.")

        serializer = TableSerializer(table)
        response_data = {
            "status": status.HTTP_200_OK,
            "message": "Table retrieved successfully",
            "data": serializer.data,
        }

        logger.info(f"Table retrieved: ID {pk}")
        return Response(response_data, status=status.HTTP_200_OK)


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


    @extend_schema(
    parameters=[
        OpenApiParameter(name="id", description="Table ID", required=True, type=OpenApiTypes.INT, location=OpenApiParameter.PATH),
    ],
    request=TableSerializer,
    responses={
        200: TableSerializer,
        400: OpenApiTypes.OBJECT,
        404: OpenApiTypes.OBJECT,
    },
)
    def update_by_id(self, request, pk=None):
        """Cập nhật thông tin của một bàn dựa trên ID, có kiểm tra dữ liệu và ghi log."""
        try:
            table = Table.objects.get(pk=pk)
        except Table.DoesNotExist:
            logger.warning(f"Table with ID {pk} not found.")
            raise NotFound("Table not found")

        serializer = TableSerializer(table, data=request.data, partial=True)  # Cho phép cập nhật từng phần
        serializer.is_valid(raise_exception=True)
        table = serializer.save(modified_time=timezone.now())  # Cập nhật thời gian chỉnh sửa

        response_data = {
            "status": 200,
            "message": "Table updated successfully",
            "data": serializer.data
        }

        logger.info(f"Table updated: ID {pk}")
        return Response(response_data, status=status.HTTP_200_OK)

    @extend_schema(
    parameters=[
        OpenApiParameter(name="id", description="Table ID", required=True, type=OpenApiTypes.INT, location=OpenApiParameter.PATH),
    ],
    )
    def delete_by_id(self, request, pk=None):
        """Xóa mềm bàn bằng cách cập nhật trạng thái thành 'DELETED', có log và tài liệu API"""
        try:
            table = Table.objects.get(pk=pk)
        except Table.DoesNotExist:
            return Response(status=status.HTTP_404_NOT_FOUND)
        serializer = TableSerializer(table)
        if table.status == "DELETED":
            logger.info(f"Table with ID {pk} is already deleted.")
            return Response(
                {"status": status.HTTP_200_OK, "message": "Table is already deleted.", "data": serializer.data},
                status=status.HTTP_200_OK
            )

        # Cập nhật trạng thái thành "DELETED" thay vì xóa
        table.status = "DELETED"
        table.modified_time = timezone.now()
        table.save()
        logger.info(f"Table with ID {pk} has been marked as DELETED.")

        return Response({"message": "Table status updated to DELETED."}, status=status.HTTP_200_OK)
    @extend_schema(
    parameters=[
        OpenApiParameter(
            name="ids",
            description="Danh sách ID của các bàn cần xóa, cách nhau bởi dấu phẩy. VD: ?ids=1,2,3",
            required=True,
            type=OpenApiTypes.STR,
            location=OpenApiParameter.QUERY
        )
    ],
    responses={200: TableSerializer(many=True)}
)
    def delete_many(self, request):
        """
        Xóa nhiều bàn bằng cách cập nhật trạng thái thành 'DELETED'.
        Nếu ID không tìm thấy, thêm vào danh sách lỗi.
        """
        ids_param = request.query_params.get("ids")

        if not ids_param:
            return Response({"error": "IDs list is required as query parameter."}, status=status.HTTP_400_BAD_REQUEST)

        # Chuyển đổi danh sách ID từ chuỗi sang danh sách số nguyên
        try:
            ids = list(map(int, ids_param.split(",")))
        except ValueError:
            return Response({"error": "Invalid ID format. IDs must be integers."}, status=status.HTTP_400_BAD_REQUEST)

        existing_tables = Table.objects.filter(id__in=ids)
        existing_ids = set(existing_tables.values_list("id", flat=True))
        not_found_ids = set(ids) - existing_ids

        response_data = {"status": status.HTTP_200_OK, "message": "Tables processed.", "errors": []}

        for table in existing_tables:
            if table.status == "DELETED":
                logger.info(f"Table with ID {table.id} is already deleted.")
            else:
                table.status = "DELETED"
                table.modified_time = timezone.now()
                table.save()
                logger.info(f"Table with ID {table.id} has been marked as DELETED.")

        response_data["data"] = TableSerializer(existing_tables, many=True).data

        if not_found_ids:
            response_data["errors"] = [{"id": id_, "message": "Table not found."} for id_ in not_found_ids]

        return Response(response_data, status=status.HTTP_200_OK)