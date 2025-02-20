import logging
from rest_framework.views import exception_handler
from rest_framework.response import Response

logger = logging.getLogger("django")  # Sử dụng logger của Django

def custom_exception_handler(exc, context):
    """
    Xử lý lỗi API trong Django REST Framework theo chuẩn JSON:
    { "status", "message", "data", "errors" }
    """
    response = exception_handler(exc, context)

    if response is not None:
        # Ghi log lỗi
        logger.error(f"Exception occurred: {exc}", exc_info=True)

        return Response({
            "status": response.status_code,
            "message": "Validation failed" if response.status_code == 400 else "An error occurred",
            "data": None,
            "errors": response.data
        }, status=response.status_code)

    # Nếu không có response, log lỗi này là lỗi nghiêm trọng
    logger.critical(f"Unhandled exception: {exc}", exc_info=True)
    return response
