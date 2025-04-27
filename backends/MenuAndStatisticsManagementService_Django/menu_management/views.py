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
from django.utils import timezone
import os
import pandas as pd
from django.http import JsonResponse
from menu_management.models import Menu  # Cập nhật đúng model bạn dùng
from pandasai import SmartDataframe
from pandasai.llm import LlamaCpp
from rest_framework.decorators import api_view
from rest_framework import status



logger = logging.getLogger("myapp.api")


class MenuViewSet(ViewSet):
    serializer_class = MenuSerializer
    @api_view(["GET"])
    def ai_analysis_local(request):
        """
        Phân tích dữ liệu Menu bằng mô hình AI nội bộ (Mistral/GGUF).
        Gửi câu hỏi tự nhiên qua query param ?q=
        """

        question = request.GET.get("q", "Món nào giá cao nhất?")

        # Lấy dữ liệu từ model Django
        qs = Menu.objects.all().values()
        df = pd.DataFrame(list(qs))

        if df.empty:
            return JsonResponse(
                {"error": "Không có dữ liệu menu."},
                status=status.HTTP_404_NOT_FOUND
            )

        # Đường dẫn mô hình GGUF
        model_path = os.path.join("ai_models", "mistral.gguf")

        if not os.path.exists(model_path):
            return JsonResponse(
                {"error": f"Mô hình không tồn tại tại {model_path}"},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

        # Khởi tạo mô hình AI nội bộ
        llm = LlamaCpp(
            model_path=model_path,
            max_tokens=256,
            temperature=0.7,
            model_kwargs={"n_ctx": 2048},
            verbose=False,
        )

        # Khởi tạo dataframe thông minh
        sdf = SmartDataframe(df, config={"llm": llm})

        try:
            result = sdf.chat(question)
        except Exception as e:
            return JsonResponse(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

        return JsonResponse({
            "question": question,
            "answer": result
        })

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
        request=MenuSerializer,
        responses={
            200: inline_serializer(
                name="MenuUpdateResponse",
                fields={
                    "status": serializers.IntegerField(default=200),
                    "message": serializers.CharField(),
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
                name="MenuUpdateBadRequestResponse",
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
                name="MenuUpdateNotFoundResponse",
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
        """Update menu information by ID, with data validation and logging."""
        try:
            menu = Menu.objects.get(pk=pk)
        except Menu.DoesNotExist:
            logger.warning(f"Menu with ID {pk} not found.")
            raise NotFound("Menu not found")


        serializer = MenuSerializer(menu, data=request.data, partial=True)
        serializer.is_valid(raise_exception=True)
        menu = serializer.save()
        response_data = {
                    "status": 200,
                    "message": "Menu updated successfully",
                    "errors": None,
                    "data": serializer.data,
        }
        logger.info(f"Menu updated: ID {pk}")
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
                    name="MenuDeleteResponse",
                    fields={
                        "status": serializers.IntegerField(),
                        "message": serializers.CharField(),
                        "errors": serializers.DictField(
                            child=serializers.CharField(),
                            required=False,
                            allow_null=True,
                            default=None,
                        ),
                        "data": MenuSerializer(),

                    },
                ),
                404: inline_serializer(
                    name="MenuDeleteNotFoundResponse",
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
        """Soft delete menu by updating status to 'DELETED', with logging and API documentation"""
        try:
            menu = Menu.objects.get(pk=pk)
        except Menu.DoesNotExist:
            logger.warning(f"Menu with ID {pk} not found.")
            raise NotFound("Menu not found.")
        serializer = MenuSerializer(menu)
        if menu.status == "DELETED":
                    logger.info(f"Menu with ID {pk} is already deleted.")
                    return Response(
                        {
                            "status": status.HTTP_200_OK,
                            "message": "Menu is already deleted.",
                            "errors": None,
                            "data": serializer.data,
                        },
                        status=status.HTTP_200_OK,
            )

        # Update status to "DELETED" instead of actual deletion
        menu.status = "Deleted"
        menu.modified_time = timezone.now()
        menu.save()
        logger.info(f"Menu with ID {pk} has been marked as DELETED.")
        return Response(
            {
                "status": status.HTTP_200_OK,
                "message": "Menu is already deleted.",
                "errors": None,
                "data": serializer.data,
            },
            status=status.HTTP_200_OK,
        )

    @extend_schema(
        parameters=[
            OpenApiParameter(
                name="ids",
                description="List of menu IDs to delete, comma-separated. Ex: ?ids=1,2,3",
                required=True,
                type=OpenApiTypes.STR,
                location=OpenApiParameter.QUERY,
            )
        ],
        responses={
            200: inline_serializer(
                name="MenuBulkDeleteResponse",
                fields={
                    "status": serializers.IntegerField(default=200),
                    "message": serializers.CharField(),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": MenuSerializer(many=True),
                },
            ),
            400: inline_serializer(
                name="MenuBulkDeleteBadRequestResponse",
                fields={
                    "status": serializers.IntegerField(default=400),
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
    def delete_many(self, request):
        """
        Delete multiple menus by updating status to 'DELETED'.
        Adds not found IDs to error dictionary.
        """
        ids_param = request.query_params.get("ids")
        if not ids_param:
            raise ValidationError({"ids": ["IDs list is required as a query parameter."]})
        ids = ids_param.split(",")

        existing_menus = Menu.objects.filter(id__in=ids)
        existing_ids = set(str(menu.id) for menu in existing_menus)

        not_found_ids = set(ids) - existing_ids
        
        response_data = {"status": status.HTTP_200_OK, "message": "Menus processed.", "errors": {}, "data": []}
        for menu in existing_menus:
            if menu.status == "Deleted":
                logger.info(f"Menu with ID {menu.id} is already deleted.")
            else:
                menu.status = "Deleted"
                menu.modified_time = timezone.now()
                menu.save()
                logger.info(f"Menu with ID {menu.id} has been marked as DELETED.")

        response_data["data"] = MenuSerializer(existing_menus, many=True).data

        if not_found_ids:
            response_data["errors"] = {id_: "Menu not found." for id_ in not_found_ids}
        return Response(response_data, status=status.HTTP_200_OK)