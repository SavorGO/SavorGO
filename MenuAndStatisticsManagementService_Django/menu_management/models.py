# menu_management/models.py

from djongo import models

class Menu(models.Model):
    name = models.CharField(max_length=255)
    category = models.CharField(max_length=100)
    description = models.TextField()
    original_price = models.FloatField()
    sale_price = models.FloatField()
    image_url = models.URLField()
    sizes = models.JSONField()  # Thay vì ArrayField, dùng JSONField trực tiếp
    options = models.JSONField()  # Tương tự với options
    status = models.CharField(max_length=50)
    created_time = models.DateTimeField(auto_now_add=True)
    modified_time = models.DateTimeField(auto_now=True)
    # Chỉ định rõ ràng rằng sử dụng 'id' làm khóa chính
    _id = models.ObjectIdField(primary_key=True)  # Sử dụng ObjectId làm primary key thay vì tự động thêm 'id'
    
    def __str__(self):
        return self.name

    class Meta:
        db_table = 'menus'  # Đổi tên bảng thành 'menus'
