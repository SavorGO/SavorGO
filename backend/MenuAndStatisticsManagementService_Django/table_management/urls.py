from django.urls import path
from .views import TableViewSet

urlpatterns = [
    path('tables', TableViewSet.as_view({'get': 'list', 'post': 'create', 'delete':'delete_many' })),
    path('tables/<int:pk>', TableViewSet.as_view({'get': 'get_by_id', 'put': 'update_by_id', 'delete': 'delete'})),
]
