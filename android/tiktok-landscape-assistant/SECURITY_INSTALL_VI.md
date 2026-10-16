# Google Play Protect chặn APK: nguyên nhân và phương án xử lý

## Ảnh lỗi thực tế (báo cáo của người dùng)
Ngày 2026-10-17: khi mở APK Full tải từ GitHub / trình quản lý tệp trên Android, Play Protect báo **"Đã chặn ứng dụng để bảo vệ thiết bị của bạn"** và **"Ứng dụng này có thể yêu cầu quyền truy cập vào dữ liệu nhạy cảm ... đánh cắp danh tính hoặc gian lận tài chính"**.

Đây là **điều kiện chặn cài đặt** từ Google Play Protect, không phải chứng cứ APK chứa mã độc, cũng không phải bằng chứng chắc chắn APK an toàn. CI Android build PASS không chứng minh cài đặt thành công.

Theo tài liệu Google, ứng dụng được sideload từ trình duyệt / quản lý tệp và có khai báo quyền **Accessibility** có thể bị tự động chặn ở một số thị trường, trong đó có Việt Nam. Source Full khai báo `AccessibilityService` để đọc điều khiển và thực hiện gesture; đây là nguyên nhân phù hợp trực tiếp với cảnh báo.

Tài liệu chính thức:
- https://developers.google.com/android/play-protect/warning-dev-guidance
- https://support.google.com/googleplay/android-developer/answer/10964491
- https://support.google.com/googleplay/android-developer/answer/16558241

## Hai bản build khác nhau

1. **LandscapeBrightnessLite-v0.2.0-alpha.apk** — chỉ có nút Quick Settings bật/tắt độ sáng 100%, tự lưu/khôi phục mức sáng khi tắt. Không có Accessibility Service, không đọc TikTok, không có Auto Next, không tự nhận biết landscape. Xin quyền **Modify system settings** bằng giao diện Android chính thức. Có thể cài thử theo quy trình sideload chuẩn; **chưa kiểm chứng Play Protect trên điện thoại thực**.
2. **TikTokLandscapeAssistant-Full-v0.3.0-alpha.apk** — giữ Auto Next thử nghiệm, yêu cầu Accessibility. Thêm khai báo minh bạch trong app, affirmative consent, lọc sự kiện TikTok. **Vẫn có thể bị Play Protect chặn khi sideload**. Không đánh tráo bản Lite thành bản Auto Next.

## Cài bản Lite (trên Android nếu Play Protect cho phép)
1. Tải từ Release GitHub chính thức của repository, chọn đúng `LandscapeBrightnessLite-v0.2.0-alpha.apk`; đối chiếu `SHA256SUMS.txt` nếu có thể.
2. Mở APK và làm theo lời nhắc cài từ nguồn đã chọn. Nếu hệ thống chặn, dừng tại đó, chụp thông báo và kiểm tra đúng APK. **Không tắt Play Protect, không cố đổi định dạng hoặc xóa quyền bảo vệ bằng thủ thuật.**
3. Mở **Landscape Brightness Lite**. Chọn cấp **Modify system settings**, cho phép thay đổi cài đặt độ sáng.
4. Vuốt thanh Quick Settings xuống hai lần, **Edit/Chỉnh sửa**, kéo tile **Fullscreen Brightness** vào.
5. Khi bắt đầu xem TikTok ngang: chạm tile để bật sáng 100%. Khi xem xong: chạm lại để khôi phục độ sáng ban đầu. Đây là điều khiển thủ công; app không tự biết lúc nào TikTok mở/đóng.

## Cách có bản Full hợp lệ
- Hoàn thiện disclosure / privacy policy / Accessibility API declaration, chuẩn bị ứng dụng để Google Play Console xét duyệt (thử nghiệm qua track phù hợp sau khi đủ điều kiện). Nếu Play Protect đánh giá nhầm, sử dụng **official Play Protect appeal**, kèm APK SHA256 và giải thích cơ chế, thay vì né cảnh báo.
- Việc được build, ký release, hay có GitHub Release **không đồng nghĩa** được Google Play chấp thuận hoặc không bị Play Protect chặn.
- Người dùng tự quyết định các bước cấp quyền nhạy cảm; không yêu cầu gửi mật khẩu, OTP hoặc dữ liệu TikTok.
- Tính năng chuyển đúng tập tiếp theo và dừng tập cuối **chưa kiểm thử end-to-end** trên TikTok thực tế.

## v0.3.0 pending validation
Full now has a manual Next Episode Quick Settings tile, episode counter guard and local diagnostic snapshot. These source features do **not** change Google's sensitive-permission sideload classification. Refer to E2E_TEST_VI.md and PLAY_CONSOLE_HANDOFF_VI.md.
