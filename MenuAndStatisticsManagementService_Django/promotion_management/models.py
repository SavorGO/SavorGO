from django.db import models

class Promotion(models.Model):
    DISCOUNT_TYPES = [
        ('PERCENT', 'Percent'),
        ('FLAT', 'Flat'),
    ]

    STATUS_CHOICES = [
        ('AVAILABLE', 'Available'),
        ('ENDED', 'Ended'),
        ('DELETED', 'Deleted'),
    ]
    
    menu_id = models.CharField(max_length=255)
    name = models.CharField(max_length=255)
    discount_value = models.BigIntegerField()
    discount_type = models.CharField(max_length=7, choices=DISCOUNT_TYPES)
    start_date = models.DateField(null=True, blank=True)  # Allow null
    end_date = models.DateField(null=True, blank=True)    # Allow null
    status = models.CharField(max_length=9, choices=STATUS_CHOICES)
    created_time = models.DateTimeField(auto_now_add=True)
    modified_time = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'promotions'  # Specify the table name as 'promotions'

    def __str__(self):
        return self.name