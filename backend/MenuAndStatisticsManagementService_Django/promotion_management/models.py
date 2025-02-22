from django.db import models

class Promotion(models.Model):
    class DiscountType(models.TextChoices):
        PERCENT = 'PERCENT', 'Percent'
        FLAT = 'FLAT', 'Flat'

    class Status(models.TextChoices):
        AVAILABLE = 'AVAILABLE', 'Available'
        ENDED = 'ENDED', 'Ended'
        DELETED = 'DELETED', 'Deleted'

    menu_id = models.CharField(max_length=255)
    name = models.CharField(max_length=255)
    discount_value = models.BigIntegerField()
    discount_type = models.CharField(
        max_length=7,
        choices=DiscountType.choices
    )
    start_date = models.DateField(null=True, blank=True)
    end_date = models.DateField(null=True, blank=True)
    status = models.CharField(
        max_length=9,
        choices=Status.choices,
        default=Status.AVAILABLE  # Mặc định là AVAILABLE
    )
    created_time = models.DateTimeField(auto_now_add=True)
    modified_time = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'promotions'  # Chỉ định tên bảng là 'promotions'

    def __str__(self):
        return self.name
