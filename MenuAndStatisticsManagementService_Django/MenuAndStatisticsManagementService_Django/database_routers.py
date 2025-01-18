# database_routers.py

class MongoDBRouter:
    def db_for_read(self, model, **hints):
        """
        Chỉ định cơ sở dữ liệu MongoDB cho các đọc dữ liệu từ MongoDB.
        """
        if model._meta.app_label == 'menu_management':  # Chỉ định ứng dụng cho MongoDB
            return 'mongodb'
        return None

    def db_for_write(self, model, **hints):
        """
        Chỉ định cơ sở dữ liệu MongoDB cho các ghi dữ liệu vào MongoDB.
        """
        if model._meta.app_label == 'menu_management':  # Chỉ định ứng dụng cho MongoDB
            return 'mongodb'
        return None

    def allow_relation(self, obj1, obj2, **hints):
        """
        Ngăn cấm quan hệ giữa các model thuộc các cơ sở dữ liệu khác nhau.
        """
        db_list = ('mongodb', 'default')  # Các cơ sở dữ liệu hợp lệ
        if obj1._meta.app_label == 'menu_management' and obj2._meta.app_label == 'menu_management':
            return True
        return None


class MariaDBRouter:
    def db_for_read(self, model, **hints):
        """
        Chỉ định cơ sở dữ liệu MariaDB cho các đọc dữ liệu từ MariaDB.
        """
        if model._meta.app_label == 'table_management':  # Chỉ định ứng dụng cho MariaDB
            return 'default'
        return None

    def db_for_write(self, model, **hints):
        """
        Chỉ định cơ sở dữ liệu MariaDB cho các ghi dữ liệu vào MariaDB.
        """
        if model._meta.app_label == 'table_management':  # Chỉ định ứng dụng cho MariaDB
            return 'default'
        return None

    def allow_relation(self, obj1, obj2, **hints):
        """
        Ngăn cấm quan hệ giữa các model thuộc các cơ sở dữ liệu khác nhau.
        """
        db_list = ('mongodb', 'default')  # Các cơ sở dữ liệu hợp lệ
        if obj1._meta.app_label == 'table_management' and obj2._meta.app_label == 'table_management':
            return True
        return None
