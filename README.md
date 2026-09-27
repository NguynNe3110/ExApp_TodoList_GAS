# Toduzu - Todo List Android App

Toduzu là ứng dụng quản lý việc cần làm (Todo) chạy trên Android, ưu tiên **offline-first** với khả năng đồng bộ dữ liệu lên cloud khi bạn cần.

## Tính năng chính

- Quản lý công việc theo **thư mục (category)**.
- Trạng thái công việc: **Đang làm / Hoàn thành** (và đánh dấu lỗi nếu cần).
- Tìm kiếm công việc theo tiêu đề.
- Thêm, đổi tên, xóa thư mục.
- Lưu dữ liệu cục bộ bằng Room để dùng ngay cả khi không có mạng.
- Đồng bộ cloud qua Firestore:
  - Đăng ký tài khoản đồng bộ.
  - Sao lưu dữ liệu local lên cloud.
  - Khôi phục dữ liệu từ cloud về máy.
- Hỗ trợ email liên kết để gửi link khôi phục mật khẩu Firebase.

## Công nghệ sử dụng

- Kotlin + Jetpack Compose (Material 3)
- Android Architecture Components (ViewModel, Navigation)
- Room (local database)
- Retrofit + Moshi
- Firebase Auth + Firestore REST API
- Gradle Kotlin DSL

## Cấu trúc dự án

- `app/`: Ứng dụng Android chính.
- `baselineprofile/`: Module hỗ trợ baseline profile cho hiệu năng khởi động.

## Yêu cầu môi trường

- Android Studio (phiên bản mới)
- Android SDK theo cấu hình dự án (minSdk 24)
- JDK tương thích với Android Gradle Plugin trong repo

## Chạy dự án local

1. Mở project bằng Android Studio.
2. Sync Gradle.
3. Tạo file `.env` từ `.env.example` (nếu bạn dùng cấu hình secrets local).
4. Đảm bảo cấu hình Firebase phù hợp (ví dụ `app/google-services.json`).
5. Chạy app trên emulator hoặc thiết bị thật.

## Lệnh Gradle thường dùng

```bash
# Build debug APK
./gradlew :app:assembleDebug

# Chạy unit test
./gradlew :app:testDebugUnitTest
```

## Luồng đồng bộ cloud (gợi ý)

1. Vào màn hình đồng bộ từ trang chính.
2. Nhập username + password (và email liên kết nếu muốn khôi phục mật khẩu).
3. Chọn một trong các thao tác:
   - **Đăng ký** tài khoản đồng bộ mới
   - **Sao lưu** dữ liệu hiện tại lên cloud
   - **Khôi phục** dữ liệu từ cloud về local

---
