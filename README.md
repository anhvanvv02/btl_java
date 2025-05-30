# Cinema Management System

Hệ thống quản lý rạp chiếu phim sử dụng Java Swing và MySQL.

## Tính năng

- Quản lý nhân viên (thêm, sửa, xóa, xem)
- Quản lý phim (thêm, sửa, xóa, xem)
- Quản lý phòng chiếu (thêm, sửa, xóa, xem)
- Quản lý vé (bán vé, xem danh sách vé)
- Báo cáo (doanh thu, thống kê phim)

## Yêu cầu hệ thống

- Java Development Kit (JDK) 11 trở lên
- MySQL Server 8.0 trở lên
- Maven

## Cài đặt

1. Clone repository:
```bash
git clone https://github.com/yourusername/cinema-management.git
cd cinema-management
```

2. Tạo cơ sở dữ liệu:
- Mở MySQL Command Line Client hoặc MySQL Workbench
- Chạy script SQL trong file `src/main/resources/database.sql`

3. Cấu hình kết nối cơ sở dữ liệu:
- Mở file `src/main/java/com/cinema/util/DBConnection.java`
- Chỉnh sửa các thông tin kết nối (nếu cần):
  ```java
  private static final String URL = "jdbc:mysql://localhost:3306/cinema_db";
  private static final String USER = "root";
  private static final String PASSWORD = "";
  ```

4. Biên dịch và chạy ứng dụng:
```bash
mvn clean install
mvn exec:java -Dexec.mainClass="com.cinema.ui.LoginFrame"
```

## Sử dụng

1. Đăng nhập hệ thống:
- Tài khoản mặc định:
  - Username: admin
  - Password: admin123

2. Các chức năng:
- Quản lý nhân viên: Thêm, sửa, xóa và xem danh sách nhân viên
- Quản lý phim: Thêm, sửa, xóa và xem danh sách phim
- Quản lý phòng chiếu: Thêm, sửa, xóa và xem danh sách phòng chiếu
- Quản lý vé: Bán vé và xem danh sách vé đã bán


