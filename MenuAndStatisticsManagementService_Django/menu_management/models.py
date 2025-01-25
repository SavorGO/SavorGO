from mongoengine import Document, EmbeddedDocument, fields
from datetime import datetime

class Size(EmbeddedDocument):
    size_name = fields.StringField(max_length=50, required=False)
    price_change = fields.FloatField(required=False)

class Option(EmbeddedDocument):
    option_name = fields.StringField(max_length=100, required=False)
    price_change = fields.FloatField(required=False)

class Menu(Document):
    name = fields.StringField(max_length=255, required=True)
    category = fields.StringField(max_length=100, required=False)
    description = fields.StringField(required=False)
    original_price = fields.FloatField()
    sale_price = fields.FloatField()
    public_id = fields.StringField()
    sizes = fields.EmbeddedDocumentListField(Size)  # Danh sách các kích thước
    options = fields.EmbeddedDocumentListField(Option)  # Danh sách các tùy chọn
    status = fields.StringField(max_length=50, required = False)
    created_time = fields.DateTimeField(default=datetime.utcnow)  # Tự động gán thời gian tạo
    modified_time = fields.DateTimeField(default=datetime.utcnow)  # Tự động gán thời gian chỉnh sửa

    meta = {
        'collection': 'menus',  # Tên collection trong MongoDB
    }

    def save(self, *args, **kwargs):
        if not self.created_time:
            self.created_time = datetime.utcnow()
        self.modified_time = datetime.utcnow()  # Cập nhật thời gian chỉnh sửa
        return super(Menu, self).save(*args, **kwargs)

    def __str__(self):
        return self.name
