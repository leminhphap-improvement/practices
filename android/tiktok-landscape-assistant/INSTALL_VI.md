# Cài đặt và sử dụng bản thử nghiệm Android

> Trạng thái: **APK alpha đã build qua GitHub Actions**; khả năng auto chuyển đúng tập trên TikTok thật **chưa được xác minh**. APK debug chỉ dành cho thử nghiệm cá nhân, không phải ứng dụng chính thức của TikTok.

## A. Cài APK từ GitHub (khuyến nghị, không cần Android Studio)

1. Vào **Releases** của repository → chọn **Pre-release** mới nhất `v0.1.0-alpha.N` nếu đã phát hành. Trong **Assets**, tải `TikTokLandscapeAssistant-v0.1.0-alpha.apk` và `SHA256SUMS.txt`. Không tải `Source code.zip` để cài.
2. Nếu chưa thấy pre-release, mở **Actions → TikTok Landscape Android APK → một run Success đúng branch → Artifacts → tiktok-landscape-assistant-debug-apk**. Tải ZIP artifact và **giải nén để lấy APK**, không cài ZIP.
3. Tùy chọn: đặt APK và `SHA256SUMS.txt` cạnh nhau trên máy tính, chạy `sha256sum -c SHA256SUMS.txt` để xác minh checksum.
4. Mở APK trên Android, cấp **Install unknown apps** khi hệ thống yêu cầu rồi cài đặt. APK được ký bằng debug key của runner; bản build khác có thể có chữ ký khác nên cần gỡ bản cũ trước khi cài (thiết lập có thể bị xóa).

## A2. Build bằng Android Studio (nếu muốn tự build)

1. Tải source ZIP hoặc clone thư mục `android/tiktok-landscape-assistant` từ branch `feat/add-fullscreen-tiktok-auto-scroll-video`.
2. Cài Android Studio, Android SDK Platform 35, SDK Build Tools và JDK 17. Mở **đúng thư mục** `tiktok-landscape-assistant` có file `settings.gradle`.
3. Cho phép Android Studio tải Gradle/Android Gradle Plugin lần đầu (cần Internet trên máy build). Nếu Android Studio yêu cầu Gradle distribution: chọn Gradle 8.7 hoặc bản mới hơn tương thích AGP 8.6.1.
4. Chọn **Build > Build Bundle(s) / APK(s) > Build APK(s)**. Chỉ khi build thành công mới có `app/build/outputs/apk/debug/app-debug.apk`.
5. Chuyển APK sang Android, mở APK để cài đặt. Android có thể yêu cầu cấp `Install unknown apps` cho trình quản lý tệp. Chỉ cài APK tự build từ source tin cậy của mình.

## B. Cấu hình Android (một lần)

1. Mở **TikTok Landscape Assistant**.
2. Chạm **Cấp quyền Accessibility** > tìm **TikTok Landscape Assistant** > bật quyền và xác nhận. Android 13+ có thể chặn quyền của APK sideload: mở **Thông tin ứng dụng > menu ba chấm > Allow restricted settings** (nếu xuất hiện), rồi quay lại Accessibility.
3. Trở lại app, chạm **Cho phép thay đổi độ sáng** > bật quyền.
4. Đảm bảo **Max brightness khi xem ngang** bật. Bật **Auto Next**.
5. Vuốt Quick Settings xuống hai lần > **Chỉnh sửa** (biểu tượng bút chì) > kéo ô **TikTok Auto Next** vào thanh điều khiển. Chạm để bật/tắt.

## C. Sử dụng và kiểm thử

1. Mở TikTok, phát video/movie ngang toàn màn hình. Khi đó app thử tăng sáng 100%, không hiện nút nổi che nội dung.
2. Khi thoát landscape, app cố khôi phục đúng cả chế độ sáng tự động/thủ công và mức sáng cũ.
3. Với **Timer = 0** và **Vuốt fallback = OFF**, auto-next chỉ hoạt động nếu TikTok expose tiến trình/nút Next cho Accessibility. Điều này CHƯA được xác minh.
4. Nếu auto-next không hoạt động, có thể thiết lập Timer (số giây) và tự bật fallback swipe, nhưng **dễ chuyển sai video**; thử với nội dung không quan trọng. Không thể tự nhận diện tập cuối khi TikTok không cung cấp thông tin đó.
5. Nếu độ sáng chưa khôi phục sau crash hoặc tước quyền: mở app, cấp lại quyền Modify settings nếu cần, bấm **Khôi phục độ sáng ban đầu**.

## D. Vấn đề cần xác minh trước khi phát hành APK ổn định

- Có build và cài đặt được APK trên thiết bị cụ thể không?
- Player TikTok landscape có expose `RangeInfo` của thanh tiến trình không?
- TikTok có nút Next episode được Accessibility nhìn thấy không?
- Thao tác vuốt theo đúng hướng có chuyển sang *tập tiếp theo của series* không?
- Khi hết series có tránh chuyển sang video khác không?
- Android có thực sự khôi phục brightness sau mọi đường thoát không?

App không dùng root, hook, injection hay Internet permission. Không cam kết TikTok sẽ cho phép tự động hóa hoặc không đánh dấu hành vi bên thứ ba.
