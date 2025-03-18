FROM mariadb:10.6

# Sao chép file SQL để tự động chạy khi container khởi động
COPY MariaDB.sql /docker-entrypoint-initdb.d/

# Thiết lập quyền truy cập
RUN chmod 644 /docker-entrypoint-initdb.d/MariaDB.sql