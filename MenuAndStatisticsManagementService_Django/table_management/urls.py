from django.urls import path
from .views import TableViewSet

urlpatterns = [
    path('tables', TableViewSet.as_view({'get': 'list', 'post': 'create', 'delete':'delete_many' })),
    path('tables/<int:pk>', TableViewSet.as_view({'get': 'get_by_id', 'put': 'update_by_id', 'delete': 'delete'})),
    path('tables/search/', TableViewSet.as_view({'get': 'search'})),  # Đảm bảo rằng endpoint này có mặt
]
