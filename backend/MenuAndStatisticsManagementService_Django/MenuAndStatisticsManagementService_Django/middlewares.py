import logging
import traceback
from django.http import JsonResponse
from django.core.exceptions import ObjectDoesNotExist
from django.utils.deprecation import MiddlewareMixin
from django.utils.timezone import now

logger = logging.getLogger("django")  # Sử dụng logger của Django

class GlobalExceptionMiddleware(MiddlewareMixin):
    def process_exception(self, request, exception):
        """
        Middleware bắt toàn bộ lỗi trong hệ thống, bao gồm lỗi ngoài API.
        """
        error_message = str(exception)
        error_traceback = traceback.format_exc()

        # Lấy thông tin về vị trí lỗi
        tb_lines = traceback.extract_tb(exception.__traceback__)
        last_call = tb_lines[-1] if tb_lines else (None, None, None, None)
        file_name, line_number, function_name, text = last_call

        status_code = 500  # Mặc định lỗi server
        response_data = {
            "status": status_code,
            "message": error_message,
            "data": None,
            "errors": None,
            "file": file_name,
            "line": line_number,
        }

        if isinstance(exception, ObjectDoesNotExist):
            status_code = 404
            response_data["status"] = status_code
            response_data["message"] = "Resource not found"

        # Ghi log lỗi vào file
        logger.error(f"Error: {error_message}\nTraceback:\n{error_traceback}\nFile: {file_name}, Line: {line_number}")

        return JsonResponse(response_data, status=status_code)

class AppendTimestampMiddleware:
    """
    Middleware tự động thêm timestamp vào mọi response JSON.
    """
    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        response = self.get_response(request)

        # Kiểm tra nếu response có dạng JSON
        if hasattr(response, "data") and isinstance(response.data, dict):
            response.data["timestamp"] = now().isoformat()
            response._is_rendered = False  # Đánh dấu để DRF render lại response
            response.render()

        return response
