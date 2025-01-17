# menu_management/urls.py

from django.urls import path, include
from .views import MenuViewSet

urlpatterns = [
    #path('menus', MenuViewSet.as_view({'get': 'list', 'post': 'create', 'delete':'delete_many' })),
    path('menus', MenuViewSet.as_view({'get': 'list'})),
    #path('menus/<int:pk>', MenuViewSet.as_view({'get': 'get_by_id', 'put': 'update_by_id', 'delete': 'delete'})),
    #path('menus/search/', MenuViewSet.as_view({'get': 'search'})),  # Đảm bảo rằng endpoint này có mặt
]