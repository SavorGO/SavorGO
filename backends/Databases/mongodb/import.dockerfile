# Sử dụng image MongoDB chính thức
FROM mongo:6.0

# Sao chép tệp JSON vào container
COPY menus.json /docker-entrypoint-initdb.d/

# Nhập dữ liệu vào MongoDB
CMD ["bash", "-c", "sleep 10 && mongoimport --host mongodb --port 27017 --db SavorGO --collection menus --file /docker-entrypoint-initdb.d/menus.json --jsonArray --username admin --password admin --authenticationDatabase admin"]
