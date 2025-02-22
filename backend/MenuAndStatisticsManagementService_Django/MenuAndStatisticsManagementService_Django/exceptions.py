import logging
from rest_framework.views import exception_handler
from rest_framework.response import Response
from rest_framework.exceptions import NotFound, ValidationError

logger = logging.getLogger("django")  # Sử dụng logger của Django

def custom_exception_handler(exc, context):
    """
    Xử lý lỗi API theo chuẩn JSON:
    { "status", "message", "data", "errors" }
    """
    response = exception_handler(exc, context)

    if response is not None:
        if isinstance(exc, ValidationError):
            # Nếu request gửi danh sách nhiều object
            if isinstance(response.data, list):
                formatted_errors = {}
                for idx, error_dict in enumerate(response.data):
                    # Lấy menu_id nếu có
                    menu_id = context["request"].data[idx].get("menu_id", f"item_{idx+1}")
                    formatted_errors[menu_id] = error_dict
            else:
                formatted_errors = response.data

            return Response({
                "status": 400,
                "message": "Validation failed",
                "errors": formatted_errors,
                "data": None,
            }, status=400)

        if isinstance(exc, NotFound):
            return Response({
                "status": 404,
                "message": "Resource not found",
                "data": None,
                "errors": response.data,
            }, status=404)

        return Response({
            "status": response.status_code,
            "message": "An error occurred",
            "errors": response.data,
            "data": None,
        }, status=response.status_code)

    return Response({
        "status": 500,
        "message": "Internal Server Error",
        "errors": {"detail": str(exc)},
        "data": None,
    }, status=500)
