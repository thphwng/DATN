**DATN - Automation Testing Suite for POS System**
        
        Dự án này tập trung vào việc xây dựng bộ kiểm thử tự động cho hệ thống CRM. Mục tiêu chính là đảm bảo chất lượng phần mềm (Software Quality Assurance) thông qua các kịch bản kiểm thử tự động hóa.

🛠 Công nghệ sử dụng
Ngôn ngữ lập trình: Java.

Framework kiểm thử: TestNG.

Thư viện tự động hóa: Selenium WebDriver. tích hợp Healenium

Quản lý dự án: Maven (đối với file pom.xml).

Công cụ phát triển: IntelliJ IDEA.

🚀 Tính năng chính

Kiểm thử đăng nhập: Tự động hóa quy trình đăng nhập vào hệ thống POS.

Kiểm thử quản lý bán hàng: Kiểm thử quy trình bán hàng, thêm sản phẩm, thay đổi số lượng hàng hóa à  tính tiền thừa khách trả.

Kiểm thử quản lý hàng hóa: Tạo dữ liệu hàng hóa hàng loạt bằng các script tự động.

Kiểm thử quản lý khách hàng: Tạo dữ liệu để thực hiện thêm/sửa/xóa thông tin khách hàng

Kiểm thử quản lý đơn hàng: Tạo dữ liệu để thực hiện thêm/sửa/xóa thông tin đơn hàng



📋 Hướng dẫn cài đặt
Cài đặt môi trường:

Cài đặt JDK 17.

Cài đặt trình duyệt Chrome và cập nhật lên phiên bản mới nhất.

Clone dự án:

Bash
git clone https://github.com/thphwng/DATN.git
Cấu hình WebDriver:

Đảm bảo phiên bản Selenium trong pom.xml tương thích với phiên bản Chrome hiện tại để tránh lỗi CDP version mismatch.

🏃 Cách chạy kiểm thử
Bạn có thể chạy các kịch bản kiểm thử trực tiếp từ IntelliJ IDEA bằng cách:

Chuột phải vào tệp test (ví dụ: GoodsTest.java) và chọn Run.

Hoặc chạy thông qua tệp cấu hình testng.xml.
