from django.urls import path
from .views import MenuViewSet

urlpatterns = [
    path('menus', MenuViewSet.as_view({'get': 'list','post':'create','delete':'delete_many' })),
    path('menus/<str:pk>', MenuViewSet.as_view({'get': 'get_by_id', 'put': 'update_by_id', 'delete': 'delete'})),
    path('menus/search/', MenuViewSet.as_view({'get': 'search'})),  # Đảm bảo rằng endpoint này có mặt
]