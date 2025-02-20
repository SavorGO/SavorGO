import logging
from rest_framework.views import exception_handler
from rest_framework.response import Response
from rest_framework.exceptions import NotFound

logger = logging.getLogger("django")  # Sử dụng logger của Django

def custom_exception_handler(exc, context):
    """
    Xử lý lỗi API trong Django REST Framework theo chuẩn JSON:
    { "status", "message", "data", "errors" }
    """
    response = exception_handler(exc, context)

    if response is not None:
        # Kiểm tra nếu lỗi là 404 - Not Found
        if isinstance(exc, NotFound):
            logger.warning(f"Not Found: {context['view'].__class__.__name__} - {context['kwargs']}")
            return Response({
                "status": 404,
                "message": "Resource not found",
                "data": None,
                "errors": response.data
            }, status=404)

        # Ghi log lỗi chung
        logger.error(f"Exception occurred: {exc}", exc_info=True)

        return Response({
            "status": response.status_code,
            "message": "Validation failed" if response.status_code == 400 else "An error occurred",
            "data": None,
            "errors": response.data
        }, status=response.status_code)

    # Nếu không có response, log lỗi này là lỗi nghiêm trọng
    logger.critical(f"Unhandled exception: {exc}", exc_info=True)
    return Response({
        "status": 500,
        "message": "Internal Server Error",
        "data": None,
        "errors": str(exc)
    }, status=500)
