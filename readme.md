# Hướng Dẫn Cài Đặt & Chạy Dự Án

## 1️⃣ Tạo và Kích Hoạt Môi Trường Ảo (Virtual Environment)
Dự án sử dụng môi trường ảo để quản lý dependencies một cách độc lập. Hãy làm theo các bước dưới đây để thiết lập:

### 🔹 Bước 1: Tạo môi trường ảo
Chạy lệnh sau trong thư mục gốc của dự án:
```sh
python -m venv venv
```

### 🔹 Bước 2: Kích hoạt môi trường ảo
- **Windows**
  ```sh
  venv\Scripts\activate
  ```
- **macOS & Linux**
  ```sh
  source venv/bin/activate
  ```

Sau khi kích hoạt, terminal của bạn sẽ hiển thị `(venv)` ở đầu dòng lệnh.

---

## 2️⃣ Cài Đặt Dependencies
Sau khi kích hoạt môi trường ảo, cài đặt tất cả dependencies bằng lệnh:
```sh
pip install -r requirements.txt
```
Lệnh này sẽ cài đặt tất cả thư viện cần thiết cho Django và cơ sở dữ liệu.

---

## 3️⃣ Chạy Server Django
Sau khi hoàn tất cài đặt, bạn có thể chạy server bằng lệnh:
```sh
python MenuAndStatisticsManagementService_Django/manage.py runserver
```

Server sẽ chạy tại địa chỉ: [http://127.0.0.1:8000](http://127.0.0.1:8000)

---

## 4️⃣ Hình Ảnh Minh Họa
![Hướng dẫn thiết lập môi trường ảo](https://github.com/user-attachments/assets/3fbe14b6-a08d-4922-8900-f3f93a527c16)

---

## 5️⃣ Ghi Chú
- Mỗi khi mở lại dự án, hãy **kích hoạt môi trường ảo** trước khi chạy server.
- Nếu có lỗi thiếu thư viện, hãy kiểm tra lại tệp `requirements.txt` và chạy `pip install -r requirements.txt`.
- Để thoát khỏi môi trường ảo, chạy lệnh:
  ```sh
  deactivate
  ```

