from drf_spectacular.utils import extend_schema, OpenApiParameter, inline_serializer
from drf_spectacular.types import OpenApiTypes
from rest_framework.exceptions import ValidationError
from drf_spectacular.types import OpenApiTypes
from rest_framework.viewsets import ViewSet
from rest_framework.response import Response
from rest_framework import status
from mongoengine.queryset.visitor import Q
from .serializers import MenuSerializer
from bson import ObjectId, errors
import logging
from rest_framework import serializers
from .models import Menu, Size, Option, Status
from rest_framework.pagination import PageNumberPagination
from rest_framework.exceptions import NotFound

logger = logging.getLogger("myapp.api")


class MenuViewSet(ViewSet):
    serializer_class = MenuSerializer

    @extend_schema(
        parameters=[
            OpenApiParameter(
                "keyword",
                OpenApiTypes.STR,
                OpenApiParameter.QUERY,
                description="Search by keyword",
                default="",
            ),
            OpenApiParameter(
                "statusFilter",
                OpenApiTypes.STR,
                OpenApiParameter.QUERY,
                description="Filter menus by status (all/without_deleted/available)",
                default="without_deleted",
            ),
            OpenApiParameter(
                "sortBy",
                OpenApiTypes.STR,
                OpenApiParameter.QUERY,
                description="Sort field",
                default="id",
            ),
            OpenApiParameter(
                "sortDirection",
                OpenApiTypes.STR,
                OpenApiParameter.QUERY,
                description="Sort direction (asc/desc)",
                default="asc",
            ),
            OpenApiParameter(
                "page",
                OpenApiTypes.INT,
                OpenApiParameter.QUERY,
                description="Page number",
                default=1,
            ),
            OpenApiParameter(
                "size",
                OpenApiTypes.INT,
                OpenApiParameter.QUERY,
                description="Items per page",
                default=10,
            ),
        ],
        responses={
            200: inline_serializer(
                name="MenuPaginatedResponse",
                fields={
                    "status": serializers.IntegerField(default=200),
                    "message": serializers.CharField(
                        default="Fetched menus successfully."
                    ),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": inline_serializer(
                        name="MenuListResponse",
                        fields={
                            "total_items": serializers.IntegerField(),
                            "total_pages": serializers.IntegerField(),
                            "count": serializers.IntegerField(),
                            "next": serializers.CharField(allow_null=True),
                            "previous": serializers.CharField(allow_null=True),
                            "data": MenuSerializer(many=True),
                        },
                    ),
                },
            )
        },
    )
    def list(self, request):
        """Get list of menus with search, sort, pagination and logging"""
        keyword = request.query_params.get("keyword", "").strip()
        status_filter = request.query_params.get(
            "statusFilter", "without_deleted"
        ).lower()
        sort_by = request.query_params.get("sortBy", "id")
        sort_direction = request.query_params.get("sortDirection", "asc").lower()
        page = request.query_params.get("page", 1)
        size = request.query_params.get("size", 10)

        if sort_direction not in ["asc", "desc"]:
            sort_direction = "asc"
        ordering = f"-{sort_by}" if sort_direction == "desc" else sort_by

        menus = Menu.objects.all()

        if keyword:
            menus = menus.filter(Q(name__icontains=keyword))
        if status_filter == "available":
            menus = menus.filter(status=Status.AVAILABLE.value)
        elif status_filter == "without_deleted":
            menus = menus.filter(status__ne=Status.DELETED.value)

        menus = menus.order_by(ordering)

        paginator = PageNumberPagination()
        paginator.page_size = size
        paginated_menus = paginator.paginate_queryset(menus, request)

        serializer = MenuSerializer(paginated_menus, many=True)
        response_data = {
            "status": status.HTTP_200_OK,
            "message": "Fetched menus successfully.",
            "errors": None,
            "data": {
                "total_items": menus.count(),
                "total_pages": (
                    paginator.page.paginator.num_pages if paginator.page else 1
                ),
                "count": paginator.page.paginator.count if paginator.page else 0,
                "next": paginator.get_next_link(),
                "previous": paginator.get_previous_link(),
                "data": serializer.data,
            },
        }

        logger.info(
            f"Menus fetched: {len(serializer.data)} items (Page: {page}, Size: {size})"
        )

        return Response(response_data, status=status.HTTP_200_OK)

    @extend_schema(
        parameters=[
            OpenApiParameter(
                name="id",
                description="Menu ID",
                required=True,
                type=OpenApiTypes.STR,
                location=OpenApiParameter.PATH,
            ),
        ],
        responses={
            200: inline_serializer(
                name="MenuGetResponse",
                fields={
                    "status": serializers.IntegerField(default=200),
                    "message": serializers.CharField(),
                    "data": MenuSerializer(),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                },
            ),
            404: inline_serializer(
                name="MenuNotFoundResponse",
                fields={
                    "status": serializers.IntegerField(default=404),
                    "message": serializers.CharField(),
                    "data": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                },
            ),
        },
    )
    def get_by_id(self, request, pk=None):
        """Get menu details by ID, with logging and API documentation"""
        try:
            menu = Menu.objects.get(pk=pk)
        except Menu.DoesNotExist:
            logger.warning(f"Menu with ID {pk} not found.")
            raise NotFound("Menu not found.")

        serializer = MenuSerializer(menu)
        response_data = {
            "status": status.HTTP_200_OK,
            "message": "Menu retrieved successfully",
            "errors": None,
            "data": serializer.data,
        }

        logger.info(f"Menu retrieved: ID {pk}")
        return Response(response_data, status=status.HTTP_200_OK)

    @extend_schema(
        request=MenuSerializer,
        responses={
            201: inline_serializer(
                name="MenuCreateResponse",
                fields={
                    "status": serializers.IntegerField(default=201),
                    "message": serializers.CharField(
                        default="Menu created successfully."
                    ),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": MenuSerializer(),
                },
            ),
            400: inline_serializer(
                name="MenuBadRequestResponse",
                fields={
                    "status": serializers.IntegerField(default=400),
                    "message": serializers.CharField(
                        default="Bad request. Please check input data."
                    ),
                    "errors": serializers.DictField(
                        child=serializers.ListField(child=serializers.CharField())
                    ),
                    "data": serializers.DictField(
                        default=None,
                    ),
                },
            ),
        },
    )
    def create(self, request):
        """Handle POST /menus/"""
        logger.info("Received request to create a new menu")
        serializer = MenuSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        menu = serializer.save()

        logger.info(f"Menu {menu.id} created successfully")
        return Response(
            {
                "status": status.HTTP_201_CREATED,
                "message": "Menu created successfully.",
                "errors": None,
                "data": serializer.data,
            },
            status=status.HTTP_201_CREATED,
        )

    def update_by_id(self, request, pk=None):
        # PUT /menus/<pk>
        try:
            menu = Menu.objects.get(pk=pk)
        except Menu.DoesNotExist:
            return Response(
                {"error": "Menu not found"}, status=status.HTTP_404_NOT_FOUND
            )

        try:
            # Update các trường từ request
            serializer = MenuSerializer(menu, data=request.data, partial=True)
            if serializer.is_valid():
                menu = serializer.save()
                return Response(MenuSerializer(menu).data)
            else:
                return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        except Exception as e:
            # Ghi lại lỗi và trả về thông báo lỗi
            print(
                f"An error occurred while updating the menu: {str(e)}"
            )  # Hoặc sử dụng logging
            return Response(
                {"error": "An error occurred while updating the menu."},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR,
            )

    def delete(self, request, pk=None):
        # DELETE /menus/<pk>
        try:
            menu = Menu.objects.get(pk=pk)
        except Menu.DoesNotExist:
            return Response(
                {"error": "Menu not found"}, status=status.HTTP_404_NOT_FOUND
            )

        # Đánh dấu trạng thái là 'DELETED' thay vì xóa thực sự
        menu.status = "DELETED"
        menu.save()
        return Response(
            {"message": "Menu status updated to DELETED"}, status=status.HTTP_200_OK
        )

    def delete_many(self, request):
        # DELETE /menus/
        ids = request.data.get("ids", [])
        if not ids:
            return Response(
                {"error": "IDs list is required."}, status=status.HTTP_400_BAD_REQUEST
            )

        menus = Menu.objects.filter(id__in=ids)
        if not menus:
            return Response(
                {"error": "No menus found with the provided IDs."},
                status=status.HTTP_404_NOT_FOUND,
            )

        menus.update(status="DELETED")
        return Response(
            {"message": f"{menus.count()} menus have been marked as DELETED."},
            status=status.HTTP_200_OK,
        )

    def search(self, request):
        # Nhận query string (ví dụ: ?q=keyword)
        search_query = request.GET.get("q", None)
        if not search_query:
            return Response(
                {"error": "No search query provided"},
                status=status.HTTP_400_BAD_REQUEST,
            )

        # Khởi tạo query trống
        query = Q()

        # Xử lý tìm kiếm theo ObjectId (nếu hợp lệ)
        try:
            obj_id = ObjectId(search_query)
            query |= Q(id=obj_id)
        except errors.InvalidId:
            pass  # Bỏ qua nếu không phải ObjectId hợp lệ

        # Tìm kiếm trong các trường khác
        query |= (
            Q(name__icontains=search_query)
            | Q(category__icontains=search_query)
            | Q(description__icontains=search_query)
            | Q(status__icontains=search_query)
        )

        # Thực hiện tìm kiếm
        menus = Menu.objects.filter(query)
        serializer = MenuSerializer(menus, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)
