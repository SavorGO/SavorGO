from rest_framework.response import Response
from rest_framework import status

def custom_response(message="", data=None, http_status=status.HTTP_200_OK):
    return Response({
        "status": "success" if success else "error",
        "message": message,
        "data": data
    }, status=http_status)
