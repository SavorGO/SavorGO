from django.db import models

class Table(models.Model):
    class Status(models.TextChoices):
        AVAILABLE = 'AVAILABLE', 'Available'
        OUT_OF_SERVICE = 'OUT_OF_SERVICE', 'Out of Service'
        OCCUPIED = 'OCCUPIED', 'Occupied'
        NEEDS_CLEANING = 'NEEDS_CLEANING', 'Needs Cleaning'
        DELETED = 'DELETED', 'Deleted'

    name = models.CharField(max_length=255)
    status = models.CharField(
        max_length=20,
        choices=Status.choices,
        default=Status.OUT_OF_SERVICE  # Mặc định OUT_OF_SERVICE
    )
    reserved_time = models.DateTimeField(null=True, blank=True)
    created_time = models.DateTimeField(auto_now_add=True)
    modified_time = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'tables'  # Chỉ định tên bảng là 'tables'

    def __str__(self):
        return self.name
