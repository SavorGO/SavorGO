from rest_framework import status
from rest_framework.viewsets import ViewSet
from rest_framework.response import Response
from .models import Promotion
from .serializers import PromotionSerializer
from django.utils import timezone
from django.db.models import Q

class PromotionViewSet(ViewSet):
    
    def list(self, request):
        # GET /promotions/
        promotions = Promotion.objects.all()
        serializer = PromotionSerializer(promotions, many=True)
        return Response(serializer.data)

    def get_by_id(self, request, pk=None):
        # GET /promotions/<int:pk>
        try:
            promotion = Promotion.objects.get(pk=pk)
        except Promotion.DoesNotExist:
            return Response({"error": "Promotion not found"}, status=status.HTTP_404_NOT_FOUND)

        serializer = PromotionSerializer(promotion)
        return Response(serializer.data)

    def create(self, request):
        # POST /promotions/
        try:
            data_list = request.data
            if not isinstance(data_list, list):
                return Response({'error': 'Invalid data format. Expected a list.'}, status=status.HTTP_400_BAD_REQUEST)

            promotions = []
            for data in data_list:
                # Validate 'name'
                name = data.get('name', '').strip()
                if not name:
                    return Response({'error': 'Name cannot be empty.'}, status=status.HTTP_400_BAD_REQUEST)
                elif len(name) > 255:
                    return Response({'error': 'Name cannot exceed 255 characters.'}, status=status.HTTP_400_BAD_REQUEST)

                # Validate 'discount_value'
                discount_value = data.get('discount_value', 0)
                if discount_value <= 0:
                    return Response({'error': 'Discount value must be greater than 0.'}, status=status.HTTP_400_BAD_REQUEST)

                # Validate 'discount_type'
                discount_type = data.get('discount_type', 'PERCENT')
                if discount_type not in dict(Promotion.DISCOUNT_TYPES).keys():
                    return Response({'error': 'Invalid discount type.'}, status=status.HTTP_400_BAD_REQUEST)

                # Validate 'start_date'
                start_date = data.get('start_date')
                if start_date:
                    start_date = datetime.strptime(start_date, '%Y-%m-%d').date()
                    if start_date < datetime.now().date():
                        return Response({'error': 'Start date cannot be in the past.'}, status=status.HTTP_400_BAD_REQUEST)

                # Validate 'end_date'
                end_date = data.get('end_date')
                if end_date:
                    end_date = datetime.strptime(end_date, '%Y-%m-%d').date()
                    if start_date and end_date < start_date:
                        return Response({'error': 'End date cannot be before start date.'}, status=status.HTTP_400_BAD_REQUEST)

                # Validate 'menu_id'
                menu_id = data.get('menu_id', '').strip()
                if not menu_id:
                    return Response({'error': 'Menu ID cannot be empty.'}, status=status.HTTP_400_BAD_REQUEST)

                # Create promotion object
                promotion = Promotion(
                    name=name,
                    discount_value=discount_value,
                    discount_type=discount_type,
                    menu_id=menu_id,
                    start_date=start_date,
                    end_date=end_date,
                    status=data.get('status') if data.get('status') is not None else 'AVAILABLE',  # Default to 'AVAILABLE'
                )
                promotions.append(promotion)

            # Bulk create promotions
            Promotion.objects.bulk_create(promotions)

            return Response({'message': f'{len(promotions)} promotions created successfully.'}, status=status.HTTP_201_CREATED)

        except Exception as e:
            return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)
    def update_by_id(self, request, pk=None):
        # PUT /promotions/<int:pk>
        try:
            promotion = Promotion.objects.get(pk=pk)
        except Promotion.DoesNotExist:
            return Response(status=status.HTTP_404_NOT_FOUND)

        # Lấy các trường cần cập nhật
        name = request.data.get('name', promotion.name)
        discount_value = request.data.get('discount_value', promotion.discount_value)
        discount_type = request.data.get('discount_type', promotion.discount_type)
        menu_id = request.data.get('menu_id', promotion.menu_id)
        start_date = request.data.get('start_date', promotion.start_date)
        end_date = request.data.get('end_date', promotion.end_date)
        status = request.data.get('status', promotion.status)

        # Cập nhật thông tin promotion
        promotion.name = name
        promotion.discount_value = discount_value
        promotion.discount_type = discount_type
        promotion.menu_id = menu_id
        promotion.start_date = start_date
        promotion.end_date = end_date
        promotion.status = status
        promotion.modified_time = timezone.now()

        promotion.save()

        # Trả về thông tin promotion đã cập nhật
        serializer = PromotionSerializer(promotion)
        return Response(serializer.data)

    def delete(self, request, pk=None):
        # DELETE /promotions/<int:pk>
        try:
            promotion = Promotion.objects.get(pk=pk)
        except Promotion.DoesNotExist:
            return Response({"error": "Promotion not found."}, status=status.HTTP_404_NOT_FOUND)

        # Cập nhật trạng thái promotion thành DELETED thay vì xóa khỏi cơ sở dữ liệu
        promotion.status = 'DELETED'
        promotion.save()

        return Response({"message": "Promotion status updated to DELETED."}, status=status.HTTP_200_OK)

    def delete_many(self, request):
        """
        Xóa nhiều promotion từ danh sách ID.
        """
        ids = request.data.get('ids', [])

        if not ids:
            return Response({"error": "IDs list is required."}, status=status.HTTP_400_BAD_REQUEST)

        # Lọc các promotion cần xóa theo ID
        promotions = Promotion.objects.filter(id__in=ids)

        if not promotions.exists():
            return Response({"error": "No promotions found with the provided IDs."}, status=status.HTTP_404_NOT_FOUND)

        # Cập nhật trạng thái của các promotion thành 'DELETED'
        promotions.update(status='DELETED')

        return Response({"message": f"{promotions.count()} promotions have been marked as DELETED."}, status=status.HTTP_200_OK)

    def search(self, request):
        search_query = request.GET.get('q', None)  # Nhận tham số tìm kiếm từ query string

        if search_query:
            # Tạo điều kiện tìm kiếm cơ bản
            query = Q(name__icontains=search_query) | Q(status__icontains=search_query)

            # Tìm kiếm theo query đã tạo
            promotions = Promotion.objects.filter(query)
            serializer = PromotionSerializer(promotions, many=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        else:
            return Response({"detail": "No search query provided"}, status=status.HTTP_400_BAD_REQUEST)
