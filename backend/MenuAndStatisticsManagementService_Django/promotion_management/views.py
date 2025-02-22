import logging
from django.utils import timezone
from django.db.models import Q
from rest_framework import status, serializers
from rest_framework.viewsets import ViewSet
from rest_framework.response import Response
from rest_framework.exceptions import ValidationError, NotFound
from rest_framework.pagination import PageNumberPagination
from drf_spectacular.utils import extend_schema, OpenApiParameter, inline_serializer
from drf_spectacular.types import OpenApiTypes
from bson import ObjectId
from bson.errors import InvalidId
from rest_framework.exceptions import ValidationError
from .models import Promotion
from menu_management.models import Menu, Status  # Import trực tiếp model Menu
from .serializers import PromotionSerializer

logger = logging.getLogger("myapp.api")


class PromotionViewSet(ViewSet):
    serializer_class = PromotionSerializer

    @extend_schema(
        parameters=[
            OpenApiParameter(
                "keyword",
                OpenApiTypes.STR,
                OpenApiParameter.QUERY,
                description="Search promotions by keyword",
                default="",
            ),
            OpenApiParameter(
                "statusFilter",
                OpenApiTypes.STR,
                OpenApiParameter.QUERY,
                description="Filter promotions by status (all/without_deleted/available)",
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
                name="PromotionPaginatedResponse",
                fields={
                    "status": serializers.IntegerField(default=200),
                    "message": serializers.CharField(
                        default="Fetched promotions successfully."
                    ),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": inline_serializer(
                        name="PromotionListResponse",
                        fields={
                            "total_items": serializers.IntegerField(),
                            "total_pages": serializers.IntegerField(),
                            "count": serializers.IntegerField(),
                            "next": serializers.CharField(allow_null=True),
                            "previous": serializers.CharField(allow_null=True),
                            "data": PromotionSerializer(many=True),
                        },
                    ),
                },
            )
        },
    )
    def list(self, request):
        """Get list of promotions with search, sort, pagination, and logging"""
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

        promotions = Promotion.objects.all()

        if keyword:
            promotions = promotions.filter(Q(name__icontains=keyword))

        if status_filter == "available":
            promotions = promotions.filter(status=Promotion.Status.AVAILABLE)
        elif status_filter == "without_deleted":
            promotions = promotions.exclude(status=Promotion.Status.DELETED)

        promotions = promotions.order_by(ordering)

        paginator = PageNumberPagination()
        paginator.page_size = size
        paginated_promotions = paginator.paginate_queryset(promotions, request)

        serializer = PromotionSerializer(paginated_promotions, many=True)
        response_data = {
            "status": status.HTTP_200_OK,
            "message": "Fetched promotions successfully.",
            "errors": None,
            "data": {
                "total_items": promotions.count(),
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
            f"Promotions fetched: {len(serializer.data)} items (Page: {page}, Size: {size})"
        )

        return Response(response_data, status=status.HTTP_200_OK)

    @extend_schema(
        parameters=[
            OpenApiParameter(
                name="id",
                description="Promotion ID",
                required=True,
                type=OpenApiTypes.INT,
                location=OpenApiParameter.PATH,
            ),
        ],
        responses={
            200: inline_serializer(
                name="PromotionGetResponse",
                fields={
                    "status": serializers.IntegerField(default=200),
                    "message": serializers.CharField(),
                    "data": PromotionSerializer(),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                },
            ),
            404: inline_serializer(
                name="PromotionNotFoundResponse",
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
        """Get promotion details by ID, with logging and API documentation"""
        try:
            promotion = Promotion.objects.get(pk=pk)
        except Promotion.DoesNotExist:
            logger.warning(f"Promotion with ID {pk} not found.")
            raise NotFound("Promotion not found.")

        serializer = PromotionSerializer(promotion)
        response_data = {
            "status": status.HTTP_200_OK,
            "message": "Promotion retrieved successfully",
            "errors": None,
            "data": serializer.data,
        }

        logger.info(f"Promotion retrieved: ID {pk}")
        return Response(response_data, status=status.HTTP_200_OK)

    @extend_schema(
        request=PromotionSerializer(many=True),
        responses={
            201: inline_serializer(
                name="PromotionCreateResponse",
                fields={
                    "status": serializers.IntegerField(default=201),
                    "message": serializers.CharField(
                        default="Promotions created successfully."
                    ),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": serializers.ListField(child=PromotionSerializer()),
                },
            ),
            400: inline_serializer(
                name="PromotionBadRequestResponse",
                fields={
                    "status": serializers.IntegerField(default=400),
                    "message": serializers.CharField(
                        default="Bad request. Please check input data."
                    ),
                    "errors": serializers.DictField(
                        child=serializers.ListField(child=serializers.CharField())
                    ),
                    "data": serializers.DictField(default=None),
                },
            ),
        },
    )
    def create(self, request):
        """Handle POST /promotions/"""
        logger.info("Received request to create new promotions")

        # Kiểm tra request có phải danh sách không
        if not isinstance(request.data, list):
            raise ValidationError({"detail": "Request must be a list of promotions."})

        # Kiểm tra trùng menu_id
        menu_ids = []
        invalid_ids = []

        for item in request.data:
            if "menu_id" in item:
                menu_id = item["menu_id"]
                try:
                    menu_ids.append(str(ObjectId(menu_id)))  # Kiểm tra ObjectId hợp lệ
                except InvalidId:
                    invalid_ids.append(menu_id)  # Lưu lại ID không hợp lệ

        # Nếu có ID không hợp lệ, raise lỗi ValidationError
        if invalid_ids:
            raise ValidationError({"menu_id": f"Invalid ObjectId: {invalid_ids}"})
        duplicates = set(
            [menu_id for menu_id in menu_ids if menu_ids.count(menu_id) > 1]
        )

        if duplicates:
            raise ValidationError(
                {"menu_id": f"Duplicate menu IDs found: {list(duplicates)}"}
            )

        # Lấy danh sách menu hợp lệ (status != "Deleted")
        valid_menu_ids = set(
            str(menu.id)
            for menu in Menu.objects.filter(
                id__in=menu_ids, status__ne=Status.DELETED.value
            ).only("id")
        )

        logger.info(f"Valid menu IDs: {list(valid_menu_ids)}")

        # Tìm menu_id không hợp lệ
        invalid_menu_ids = set(menu_ids) - valid_menu_ids
        logger.info(f"Invalid menu IDs: {list(invalid_menu_ids)}")

        if invalid_menu_ids:
            raise ValidationError(
                {
                    "menu_id": f"Invalid menu IDs or menu is deleted: {list(invalid_menu_ids)}"
                }
            )

        # Validate dữ liệu bằng serializer
        serializer = PromotionSerializer(data=request.data, many=True)
        serializer.is_valid(raise_exception=True)

        # Lưu promotions vào database
        promotions = serializer.save()

        logger.info(f"{len(promotions)} promotions created successfully")
        return Response(
            {
                "status": status.HTTP_201_CREATED,
                "message": "Promotions created successfully.",
                "errors": None,
                "data": serializer.data,
            },
            status=status.HTTP_201_CREATED,
        )

    @extend_schema(
        parameters=[
            OpenApiParameter(
                name="id",
                description="Promotion ID",
                required=True,
                type=OpenApiTypes.INT64,
                location=OpenApiParameter.PATH,
            ),
        ],
        request=PromotionSerializer,
        responses={
            200: inline_serializer(
                name="PromotionUpdateResponse",
                fields={
                    "status": serializers.IntegerField(default=200),
                    "message": serializers.CharField(),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": PromotionSerializer(),
                },
            ),
            400: inline_serializer(
                name="PromotionUpdateBadRequestResponse",
                fields={
                    "status": serializers.IntegerField(default=400),
                    "message": serializers.CharField(),
                    "errors": serializers.DictField(
                        child=serializers.ListField(child=serializers.CharField())
                    ),
                    "data": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                },
            ),
            404: inline_serializer(
                name="PromotionUpdateNotFoundResponse",
                fields={
                    "status": serializers.IntegerField(default=404),
                    "message": serializers.CharField(),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                },
            ),
        },
    )
    def update_by_id(self, request, pk=None):
        """Update promotion information by ID, with data validation and logging."""
        logger.info(f"Received request to update promotion ID {pk}")

        try:
            promotion = Promotion.objects.get(pk=pk)
        except Promotion.DoesNotExist:
            logger.warning(f"Promotion with ID {pk} not found.")
            raise NotFound({"promotion_id": "Promotion not found"})

        # Kiểm tra nếu có menu_id, phải đảm bảo nó là ObjectId hợp lệ
        menu_id = request.data.get("menu_id")
        if menu_id:
            try:
                menu_id = str(ObjectId(menu_id))  # Kiểm tra xem menu_id có hợp lệ không
            except InvalidId:
                raise ValidationError({"menu_id": f"Invalid ObjectId: {menu_id}"})

            # Kiểm tra menu_id có tồn tại và không bị xóa
            menu = Menu.objects.filter(
                id=menu_id
            ).first()  # Lấy menu đầu tiên có id khớp
            if menu is None or menu.status == Status.DELETED.value:
                raise ValidationError(
                    {"menu_id": "Menu ID is invalid or has been deleted"}
                )

        # Áp dụng cập nhật dữ liệu
        serializer = PromotionSerializer(promotion, data=request.data, partial=True)
        serializer.is_valid(raise_exception=True)
        promotion = serializer.save(modified_time=timezone.now())

        response_data = {
            "status": 200,
            "message": "Promotion updated successfully",
            "errors": None,
            "data": serializer.data,
        }

        logger.info(f"Promotion updated successfully: ID {pk}")
        return Response(response_data, status=status.HTTP_200_OK)

    @extend_schema(
        parameters=[
            OpenApiParameter(
                name="id",
                description="Promotion ID",
                required=True,
                type=OpenApiTypes.INT64,
                location=OpenApiParameter.PATH,
            ),
        ],
        responses={
            200: inline_serializer(
                name="PromotionDeleteResponse",
                fields={
                    "status": serializers.IntegerField(),
                    "message": serializers.CharField(),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": PromotionSerializer(),  # Đảm bảo sử dụng serializer phù hợp cho Promotion
                },
            ),
            404: inline_serializer(
                name="PromotionDeleteNotFoundResponse",
                fields={
                    "status": serializers.IntegerField(default=404),
                    "message": serializers.CharField(),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                },
            ),
        },
    )
    def delete_by_id(self, request, pk=None):
        """Soft delete promotion by updating status to 'DELETED', with logging and API documentation"""
        try:
            promotion = Promotion.objects.get(pk=pk)
        except Promotion.DoesNotExist:
            logger.warning(f"Promotion with ID {pk} not found.")
            raise NotFound("Promotion not found.")

        serializer = PromotionSerializer(promotion)  # Sử dụng serializer cho Promotion
        if promotion.status == "DELETED":
            logger.info(f"Promotion with ID {pk} is already deleted.")
            return Response(
                {
                    "status": status.HTTP_200_OK,
                    "message": "Promotion is already deleted.",
                    "errors": None,
                    "data": serializer.data,
                },
                status=status.HTTP_200_OK,
            )

        # Update status to "DELETED" instead of actual deletion
        promotion.status = "DELETED"
        promotion.modified_time = timezone.now()  # Nếu có trường modified_time
        promotion.save()
        logger.info(f"Promotion with ID {pk} has been marked as DELETED.")
        return Response(
            {
                "status": status.HTTP_200_OK,
                "message": "Promotion status updated to DELETED.",
                "errors": None,
                "data": serializer.data,
            },
            status=status.HTTP_200_OK,
        )

    def delete_many(self, request):
        """
        Xóa nhiều promotion từ danh sách ID.
        """
        ids = request.data.get("ids", [])

        if not ids:
            return Response(
                {"error": "IDs list is required."}, status=status.HTTP_400_BAD_REQUEST
            )

        # Lọc các promotion cần xóa theo ID
        promotions = Promotion.objects.filter(id__in=ids)

        if not promotions.exists():
            return Response(
                {"error": "No promotions found with the provided IDs."},
                status=status.HTTP_404_NOT_FOUND,
            )

        # Cập nhật trạng thái của các promotion thành 'DELETED'
        promotions.update(status="DELETED")

        return Response(
            {
                "message": f"{promotions.count()} promotions have been marked as DELETED."
            },
            status=status.HTTP_200_OK,
        )

    def search(self, request):
        search_query = request.GET.get(
            "q", None
        )  # Nhận tham số tìm kiếm từ query string

        if search_query:
            # Tạo điều kiện tìm kiếm cơ bản
            query = Q(name__icontains=search_query) | Q(status__icontains=search_query)

            # Tìm kiếm theo query đã tạo
            promotions = Promotion.objects.filter(query)
            serializer = PromotionSerializer(promotions, many=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        else:
            return Response(
                {"detail": "No search query provided"},
                status=status.HTTP_400_BAD_REQUEST,
            )
