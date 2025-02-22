from django.urls import path
from .views import PromotionViewSet

urlpatterns = [
    path('promotions', PromotionViewSet.as_view({'get': 'list', 'post': 'create', 'delete':'delete_many' })),
    path('promotions/<int:pk>', PromotionViewSet.as_view({'get': 'get_by_id', 'put': 'update_by_id', 'delete': 'delete_by_id'})),
]