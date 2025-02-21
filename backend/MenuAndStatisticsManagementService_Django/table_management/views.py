from drf_spectacular.utils import extend_schema, OpenApiParameter, inline_serializer
from rest_framework.exceptions import ValidationError
from drf_spectacular.types import OpenApiTypes
from rest_framework import serializers
from rest_framework import status
from rest_framework.viewsets import ViewSet
from rest_framework.response import Response
from rest_framework.exceptions import NotFound
from .models import Table
from .serializers import TableSerializer
from datetime import datetime, timedelta
from django.utils import timezone
from django.db.models import Q
import logging
from rest_framework.pagination import PageNumberPagination

logger = logging.getLogger("myapp.api")


class TableViewSet(ViewSet):
    serializer_class = TableSerializer

    @extend_schema(
        parameters=[
            OpenApiParameter(
                "keyword", OpenApiTypes.STR, OpenApiParameter.QUERY, 
                description="Search by name", default=""
            ),
            OpenApiParameter(
                "statusFilter", OpenApiTypes.STR, OpenApiParameter.QUERY,
                description="Filter tables by status (all/without_deleted/available)", default="without_deleted"
            ),
            OpenApiParameter(
                "sortBy", OpenApiTypes.STR, OpenApiParameter.QUERY, 
                description="Sort field", default="id"
            ),
            OpenApiParameter(
                "sortDirection", OpenApiTypes.STR, OpenApiParameter.QUERY, 
                description="Sort direction (asc/desc)", default="asc"
            ),
            OpenApiParameter(
                "page", OpenApiTypes.INT, OpenApiParameter.QUERY, 
                description="Page number", default=1
            ),
            OpenApiParameter(
                "size", OpenApiTypes.INT, OpenApiParameter.QUERY, 
                description="Items per page", default=10
            ),
        ],
        responses={
            200: inline_serializer(
                name="TablePaginatedResponse",
                fields={
                    "status": serializers.IntegerField(default=200),
                    "message": serializers.CharField(
                        default="Fetched tables successfully."
                    ),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": inline_serializer(
                        name="TableListResponse",
                        fields={
                            "total_items": serializers.IntegerField(),
                            "total_pages": serializers.IntegerField(),
                            "count": serializers.IntegerField(),
                            "next": serializers.CharField(allow_null=True),
                            "previous": serializers.CharField(allow_null=True),
                            "data": TableSerializer(many=True),
                        },
                    ),
                },
            )
        },
    )
    def list(self, request):
        """Get list of tables with search, sort, pagination and logging"""
        keyword = request.query_params.get("keyword", "").strip()
        status_filter = request.query_params.get("statusFilter", "without_deleted").lower()
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
        if status_filter == "available":
            tables = tables.filter(status=Table.Status.AVAILABLE)
        elif status_filter == "without_deleted":
            tables = tables.exclude(status=Table.Status.DELETED)
        tables = tables.order_by(ordering)

        paginator = PageNumberPagination()
        paginator.page_size = size
        paginated_tables = paginator.paginate_queryset(tables, request)

        serializer = TableSerializer(paginated_tables, many=True)

        # Trả về đúng format mong muốn
        response_data = {
            "status": status.HTTP_200_OK,
            "message": "Fetched tables successfully.",
            "errors": None,
            "data": {
                "total_items": tables.count(),
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
            f"Tables fetched: {len(serializer.data)} items (Page: {page}, Size: {size})"
        )

        # Thay vì dùng paginator.get_paginated_response(), ta trả về Response trực tiếp
        return Response(response_data, status=status.HTTP_200_OK)

    @extend_schema(
        parameters=[
            OpenApiParameter(
                name="id",
                description="Table ID",
                required=True,
                type=OpenApiTypes.INT,
                location=OpenApiParameter.PATH,
            ),
        ],
        responses={
            200: inline_serializer(
                name="TableGetResponse",
                fields={
                    "status": serializers.IntegerField(default=200),
                    "message": serializers.CharField(),
                    "data": TableSerializer(),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                },
            ),
            404: inline_serializer(
                name="TableNotFoundResponse",
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
        """Get table details by ID, with logging and API documentation"""
        try:
            table = Table.objects.get(pk=pk)
        except Table.DoesNotExist:
            logger.warning(f"Table with ID {pk} not found.")
            raise NotFound("Table not found.")

        serializer = TableSerializer(table)
        response_data = {
            "status": status.HTTP_200_OK,
            "message": "Table retrieved successfully",
            "errors": None,
            "data": serializer.data,
        }

        logger.info(f"Table retrieved: ID {pk}")
        return Response(response_data, status=status.HTTP_200_OK)

    @extend_schema(
        request=TableSerializer,
        responses={
            201: inline_serializer(
                name="TableCreateResponse",
                fields={
                    "status": serializers.IntegerField(default=201),
                    "message": serializers.CharField(
                        default="Table created successfully."
                    ),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": TableSerializer(),
                },
            ),
            400: inline_serializer(
                name="TableBadRequestResponse",
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
        """Handle POST /tables/"""
        logger.info("Received request to create a new table")
        serializer = TableSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        table = serializer.save()

        logger.info(f"Table {table.id} created successfully")
        return Response(
            {
                "status": status.HTTP_201_CREATED,
                "message": "Table created successfully.",
                "errors": None,
                "data": serializer.data,
            },
            status=status.HTTP_201_CREATED,
        )

    @extend_schema(
        parameters=[
            OpenApiParameter(
                name="id",
                description="Table ID",
                required=True,
                type=OpenApiTypes.INT,
                location=OpenApiParameter.PATH,
            ),
        ],
        request=TableSerializer,
        responses={
            200: inline_serializer(
                name="TableUpdateResponse",
                fields={
                    "status": serializers.IntegerField(default=200),
                    "message": serializers.CharField(),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": TableSerializer(),
                },
            ),
            400: inline_serializer(
                name="TableUpdateBadRequestResponse",
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
                name="TableUpdateNotFoundResponse",
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
        """Update table information by ID, with data validation and logging."""
        try:
            table = Table.objects.get(pk=pk)
        except Table.DoesNotExist:
            logger.warning(f"Table with ID {pk} not found.")
            raise NotFound("Table not found")

        serializer = TableSerializer(table, data=request.data, partial=True)
        serializer.is_valid(raise_exception=True)
        table = serializer.save(modified_time=timezone.now())

        response_data = {
            "status": 200,
            "message": "Table updated successfully",
            "errors": None,
            "data": serializer.data,
        }

        logger.info(f"Table updated: ID {pk}")
        return Response(response_data, status=status.HTTP_200_OK)

    @extend_schema(
        parameters=[
            OpenApiParameter(
                name="id",
                description="Table ID",
                required=True,
                type=OpenApiTypes.INT,
                location=OpenApiParameter.PATH,
            ),
        ],
        responses={
            200: inline_serializer(
                name="TableDeleteResponse",
                fields={
                    "status": serializers.IntegerField(),
                    "message": serializers.CharField(),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                    "data": TableSerializer(),

                },
            ),
            404: inline_serializer(
                name="TableDeleteNotFoundResponse",
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
        """Soft delete table by updating status to 'DELETED', with logging and API documentation"""
        try:
            table = Table.objects.get(pk=pk)
        except Table.DoesNotExist:
            logger.warning(f"Table with ID {pk} not found.")
            raise NotFound("Table not found.")
        serializer = TableSerializer(table)
        if table.status == "DELETED":
            logger.info(f"Table with ID {pk} is already deleted.")
            return Response(
                {
                    "status": status.HTTP_200_OK,
                    "message": "Table is already deleted.",
                    "errors": None,
                    "data": serializer.data,
                },
                status=status.HTTP_200_OK,
            )

        # Update status to "DELETED" instead of actual deletion
        table.status = "DELETED"
        table.modified_time = timezone.now()
        table.save()
        logger.info(f"Table with ID {pk} has been marked as DELETED.")
        return Response(
            {
                "status": status.HTTP_200_OK,
                "message": "Table is already deleted.",
                "errors": None,
                "data": serializer.data,
            },
            status=status.HTTP_200_OK,
        )

    @extend_schema(
        parameters=[
            OpenApiParameter(
                name="ids",
                description="List of table IDs to delete, comma-separated. Ex: ?ids=1,2,3",
                required=True,
                type=OpenApiTypes.STR,
                location=OpenApiParameter.QUERY,
            )
        ],
        responses={
            200: inline_serializer(
                name="TableBulkDeleteResponse",
                fields={
                    "status": serializers.IntegerField(default=200),
                    "message": serializers.CharField(),
                    "data": TableSerializer(many=True),
                    "errors": serializers.DictField(
                        child=serializers.CharField(),
                        required=False,
                        allow_null=True,
                        default=None,
                    ),
                },
            ),
            400: inline_serializer(
                name="TableBulkDeleteBadRequestResponse",
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
        Delete multiple tables by updating status to 'DELETED'.
        Adds not found IDs to error dictionary.
        """
        ids_param = request.query_params.get("ids")
        if not ids_param:
            raise ValidationError({"ids": ["IDs list is required as a query parameter."]})
        try:
            ids = list(map(int, ids_param.split(",")))
        except ValueError:
            raise ValidationError({"ids": ["Invalid ID format. IDs must be integers."]})

        existing_tables = Table.objects.filter(id__in=ids)
        existing_ids = set(existing_tables.values_list("id", flat=True))
        not_found_ids = set(ids) - existing_ids

        response_data = {"status": status.HTTP_200_OK, "message": "Tables processed.", "errors": {}, "data": []}


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
            response_data["errors"] = {id_: "Table not found." for id_ in not_found_ids}
        return Response(response_data, status=status.HTTP_200_OK)