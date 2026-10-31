# BẢNG PHÂN TÍCH CHỨC NĂNG VÀ KỸ THUẬT TRIỂN KHAI
## ĐỀ TÀI 43: ĐIỂM DANH LỚP HỌC QUA MẠNG NỘI BỘ
**Học phần:** Lập trình Java nâng cao (AJP201)  
**Nhóm thực hiện:** 2 sinh viên (Thành viên 1 & Thành viên 2)  
**Tài liệu căn cứ:** `main.pdf` (Mục 1.2, 1.3, 1.5, Bảng 1.1 và Đề 43) và Slide bài giảng Chương 1 đến Chương 6 (`chuong-01-slide.pdf` – `chuong-06-slide.pdf`).

---

### Quy ước và Phân công vai trò

- **Thứ tự thực hiện:** Sắp xếp tuần tự từ nền tảng mô hình dữ liệu → nghiệp vụ điểm danh → lập lịch hẹn giờ → mạng TCP Client/Server đa luồng → xử lý báo cáo Streams & lưu tệp CSV → giao diện điều khiển → các tính năng mở rộng.
- **Quy ước nguồn tính năng:**
  - **Phần lõi:** Các thành phần hạt nhân bắt buộc của bài toán điểm danh mạng (Mô hình dữ liệu, Đọc CSV danh sách lớp, Quản lý phiên, Hẹn giờ đóng phiên, Chống trùng ConcurrentHashMap, ServerSocket TCP đa luồng, Client TCP).
  - **Phải làm:** Các chức năng hoàn thiện hệ thống theo rubric chấm (Lọc danh sách vắng bằng Streams, Thống kê buổi, Xuất file CSV, Bảng điều khiển Console giảng viên, Đóng gói JAR).
  - **Mở rộng:** Các tính năng nâng cao theo yêu cầu đề 43 (Chống gửi hộ theo IP, Thống kê chuyên cần cả kỳ cảnh báo vắng ≥ 20%).
- **Phân công trách nhiệm:**
  - **Thành viên 1:** Phụ trách Tầng Mô hình dữ liệu (Model), Tầng Lưu trữ (CSV), Tầng Nghiệp vụ điểm danh (Service, ScheduledTimer), và Xử lý Thống kê / Báo cáo Streams.
  - **Thành viên 2:** Phụ trách Tầng Mạng TCP (Server, ClientHandler, Student Client), Thiết kế Giao thức truyền thông (Protocol), Giao diện Console Giảng viên, và Cơ chế Chống gian lận IP.

---

### Bảng danh sách chức năng chi tiết

| Mã | Chức năng | Nguồn (Phần lõi / Phải làm / Mở rộng) | Trường hợp biên | Kỹ thuật Java (chương) | Phụ thuộc | Người phụ trách |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **F01** | Khai báo mô hình dữ liệu (`SinhVien`, `PhienDiemDanh`, `LuotDiemDanh`, enum trạng thái) và hệ thống ngoại lệ tự định nghĩa | Phần lõi | - Dữ liệu rỗng (`null`, chuỗi trắng `""`).<br>- Thời lượng phiên điểm danh $\le 0$ phút.<br>- Mã sinh viên chứa khoảng trắng hoặc ký tự không hợp lệ.<br>- Hai đối tượng cùng mã nhưng khác địa chỉ nhớ (cần đảm bảo hợp đồng equals/hashCode). | `record` (Ch6), `enum` (Ch6), `custom exception` (Ch6), `equals và hashCode` (Ch3) | Không | Thành viên 1 |
| **F02** | Đọc danh sách lớp từ tệp CSV và nạp vào bộ nhớ | Phần lõi | - Tệp CSV không tồn tại (`FileNotFoundException`).<br>- Tệp CSV rỗng (0 bytes).<br>- Dòng CSV sai định dạng (thiếu cột, thừa dấu phẩy, chứa khoảng trắng thừa).<br>- Trùng lặp mã sinh viên trong file CSV nguồn.<br>- Lỗi font chữ tiếng Việt UTF-8 có dấu. | `try-with-resources` (Ch6), `BufferedReader` (Ch5), `Map` / `HashMap` (Ch3), `Set` / `HashSet` (Ch3), `method reference` (Ch2), `custom exception` (Ch6) | F01 | Thành viên 1 |
| **F03** | Quản lý trạng thái và vòng đời phiên điểm danh (Mở phiên, đóng phiên thủ công, tra cứu phiên) | Phần lõi | - Mở phiên mới khi phiên trước đó chưa đóng.<br>- Đóng phiên khi không có phiên nào đang mở.<br>- Mã phiên bị trùng với phiên đã có trong lịch sử.<br>- Thời lượng mở phiên không hợp lệ ($\le 0$ hoặc quá dài).<br>- Nhiều yêu cầu tra cứu trạng thái phiên cùng lúc. | `java.time` (Ch6), `volatile` (Ch4), `synchronized` (Ch4), `enum` (Ch6), `custom exception` (Ch6) | F01 | Thành viên 1 |
| **F04** | Luồng hẹn giờ tự động đóng phiên khi hết hạn | Phần lõi | - Giảng viên chủ động đóng phiên trước khi hết giờ (cần hủy tác vụ hẹn giờ qua `ScheduledFuture.cancel` để tránh đóng 2 lần).<br>- Luồng hẹn giờ gặp ngoại lệ bất ngờ không làm gián đoạn hệ thống.<br>- Yêu cầu điểm danh gửi đến đúng tích tắc phiên vừa chuyển trạng thái đóng.<br>- Đóng ứng dụng khi luồng hẹn giờ đang chạy (phải `shutdown` và giải phóng an toàn). | `ScheduledExecutorService` (Ch4), `ScheduledFuture` (Ch4), `Runnable` (Ch4), `Lambda` (Ch2), `try-with-resources` (Ch6) | F03 | Thành viên 1 |
| **F05** | Tiếp nhận điểm danh và chống trùng mã sinh viên an toàn luồng | Phần lõi | - Hàng chục yêu cầu điểm danh gửi tới cùng mili-giây (race condition).<br>- Một sinh viên gửi nhiều lần liên tiếp (double click/spam request).<br>- Sinh viên không có tên trong danh sách lớp nạp từ CSV.<br>- Sinh viên gửi điểm danh khi phiên đã đóng hoặc hết hạn.<br>- Sinh viên gửi sai mã phiên điểm danh hiện tại. | `ConcurrentHashMap` (Ch3, Ch4), `Set` (Ch3), `synchronized` (Ch4), `equals và hashCode` (Ch3), `custom exception` (Ch6) | F01, F02, F03 | Thành viên 1 |
| **F06** | Xây dựng giao thức mạng văn bản (Request/Response Protocol) và Bộ phân tích cú pháp lệnh | Phần lõi | - Lệnh gửi lên rỗng hoặc toàn dấu cách.<br>- Tên lệnh không hợp lệ hoặc không có trong bảng lệnh.<br>- Thiếu tham số bắt buộc (ví dụ gửi lệnh mà thiếu `maSV` hoặc `maPhien`).<br>- Tham số chứa ký tự xuống dòng `\r`, `\n` làm gãy giao thức.<br>- Dữ liệu vượt quá kích thước đệm nhận. | `switch expression` (Ch6), `text block` (Ch6), `Arrays` (Ch1), `custom exception` (Ch6) | F01 | Thành viên 2 |
| **F07** | Máy chủ TCP Socket phục vụ đa máy khách đồng thời qua Bể luồng (Multi-client TCP Server) | Phần lõi | - Cổng mạng (Port) bị tiến trình khác chiếm dụng (`BindException`).<br>- Số lượng client kết nối đồng thời vượt quy mô luồng (bể luồng phải xếp hàng tác vụ chờ).<br>- Máy khách ngắt kết nối đột ngột (tắt ứng dụng, mất mạng) khi đang truyền tin.<br>- Rò rỉ tài nguyên cổng/socket nếu xảy ra ngoại lệ IO. | `ServerSocket` (Ch5), `Socket` (Ch5), `ExecutorService` (Ch4, Ch5), `Executors.newFixedThreadPool` (Ch4, Ch5), `ClientHandler` implements `Runnable` (Ch4, Ch5), `BufferedReader` (Ch5), `PrintWriter` (Ch5), `try-with-resources` (Ch6) | F05, F06 | Thành viên 2 |
| **F08** | Ứng dụng Máy khách TCP phía Sinh viên gửi điểm danh (Student TCP Client) | Phần lõi | - Không kết nối được đến máy chủ (sai IP, sai cổng, máy chủ chưa bật).<br>- Mất kết nối trong lúc chờ phản hồi (timeout).<br>- Sinh viên nhập sai định dạng hoặc để trống mã sinh viên, mã phiên.<br>- Máy chủ trả về thông điệp lỗi hoặc đóng kết nối bất ngờ. | `Socket` (Ch5), `BufferedReader` (Ch5), `PrintWriter` (Ch5), `try-with-resources` (Ch6), `custom exception` (Ch6) | F06, F07 | Thành viên 2 |
| **F09** | Tổng hợp danh sách sinh viên vắng mặt buổi học bằng Streams API | Phải làm | - Cả lớp có mặt 100% (danh sách vắng rỗng).<br>- Cả lớp vắng mặt 100% (không có ai điểm danh thành công).<br>- Danh sách lớp ban đầu rỗng (0 sinh viên).<br>- Có sinh viên đã điểm danh nhưng bị xóa khỏi danh sách lớp gốc. | `Streams API` (Ch2), `filter` (Ch2), `map` (Ch2), `sorted` (Ch2), `collect` (Ch2), `Collectors.toList` (Ch2), `Predicate` (Ch2), `Lambda` (Ch2) | F02, F05 | Thành viên 1 |
| **F10** | Thống kê số lượng và tính tỷ lệ chuyên cần theo buổi học bằng Streams | Phải làm | - Sĩ số lớp bằng 0 (tránh lỗi chia cho 0 `ArithmeticException` khi tính tỷ lệ %).<br>- Tỷ lệ điểm danh đạt đúng 0.0% hoặc 100.0%.<br>- Làm tròn số thập phân tỷ lệ phần trăm hiển thị.<br>- Thống kê khi phiên đang diễn ra so với khi phiên đã kết thúc. | `Streams API` (Ch2), `count` (Ch2), `text block` (Ch6), `Optional` (Ch6) | F09 | Thành viên 1 |
| **F11** | Xuất bảng kết quả điểm danh của buổi học ra tệp CSV | Phải làm | - Tệp đang bị mở hoặc khóa bởi ứng dụng khác (ví dụ Excel) gây lỗi ghi tệp.<br>- Không có quyền ghi vào thư mục chỉ định (`AccessDeniedException`).<br>- Thư mục lưu trữ chưa tồn tại (cần tạo tự động).<br>- Dữ liệu họ tên sinh viên chứa ký tự đặc biệt hoặc dấu phẩy cần bao bọc nháy kép. | `try-with-resources` (Ch6), `PrintWriter` (Ch5), `java.time` (Ch6), `custom exception` (Ch6) | F09, F10 | Thành viên 1 |
| **F12** | Bảng điều khiển Console tương tác phía Giảng viên (Teacher CLI Console) | Phải làm | - Giảng viên nhập lệnh rỗng hoặc ký tự lạ.<br>- Nhập sai kiểu dữ liệu thời lượng (nhập chữ thay vì số phút).<br>- Đóng máy chủ khi phiên điểm danh đang chạy (cần cảnh báo xác nhận).<br>- Xử lý phím ngắt tiến trình (Ctrl+C) đảm bảo đóng cổng mạng an toàn. | `BufferedReader` (Ch5), `switch expression` (Ch6), `enum` (Ch6), `try-with-resources` (Ch6) | F03, F04, F07, F11 | Thành viên 2 |
| **F13** | Đóng gói chương trình thành tệp thực thi độc lập (JAR) kèm kịch bản chạy | Phải làm | - Thiếu tệp cấu hình `MANIFEST.MF` trỏ đúng lớp chính (`Main-Class`).<br>- Không tìm thấy đường dẫn tệp dữ liệu CSV tương đối khi thực thi ở các thư mục khác nhau.<br>- Môi trường thực thi khác phiên bản JDK nguồn. | `jar` (Ch6) | F07, F08, F12 | Cả nhóm |
| **F14** | Chống gửi hộ đơn giản qua địa chỉ IP mạng (Một IP chỉ được điểm danh một mã SV) | Mở rộng | - Cùng 1 sinh viên gửi lại từ cùng 1 IP (phải thông báo "Đã điểm danh" chứ không báo "Gian lận IP").<br>- 2 sinh viên khác nhau gửi từ cùng 1 IP (từ chối sinh viên sau, ghi cảnh báo nghi vấn gửi hộ).<br>- Địa chỉ IPv4 (`127.0.0.1`) và IPv6 loopback (`0:0:0:0:0:0:0:1` / `::1`) khi chạy thử nghiệm trên cùng một máy.<br>- Môi trường mạng NAT trường học dùng chung IP công cộng (cần ghi log cảnh báo chi tiết). | `ConcurrentHashMap` (Ch3, Ch4), `Socket.getInetAddress` (Ch5), `synchronized` (Ch4), `custom exception` (Ch6) | F05, F07 | Thành viên 2 |
| **F15** | Thống kê chuyên cần cả kỳ theo nhiều buổi và Cảnh báo sinh viên vắng $\ge 20\%$ (chuyên cần $< 80\%$) | Mở rộng | - Thư mục chưa có dữ liệu buổi điểm danh nào hoặc chỉ có 1 buổi.<br>- Số buổi khảo sát ít dẫn đến tỷ lệ phần trăm nhảy vọt khi vắng 1 buổi.<br>- Sinh viên chuyển lớp giữa kỳ có số buổi theo học ít hơn cả lớp.<br>- Sinh viên vắng đúng ngưỡng $20.0\%$ (trường hợp biên chạm mốc cảnh báo).<br>- Định dạng bảng xuất đẹp mắt khi danh sách cảnh báo rỗng. | `Streams API` (Ch2), `filter` (Ch2), `map` (Ch2), `sorted` (Ch2), `collect` (Ch2), `Collectors.groupingBy` (Ch2), `Collectors.counting` (Ch2), `Comparator.comparing` (Ch3), `thenComparing` (Ch3), `try-with-resources` (Ch6), `Optional` (Ch6) | F02, F11 | Thành viên 1 |

---

### Bảng tra cứu kỹ thuật Java theo Slide bài giảng

| Kỹ thuật sử dụng | Chương trong giáo trình / slide | Vị trí minh họa trong bài giảng | Mục đích áp dụng trong Đề 43 |
| :--- | :--- | :--- | :--- |
| `record` | Chương 6 (Ch6) | Bảng 1.1 `main.pdf`, Slide Ch3 trang 25 | Định nghĩa các thực thể dữ liệu bất biến: `SinhVien`, `PhienDiemDanh`, `LuotDiemDanh` ngắn gọn, tự sinh `equals`/`hashCode`. |
| `enum` | Chương 6 (Ch6) | Bảng 1.1 `main.pdf`, Slide Ch4 trang 12 | Biểu diễn tập trạng thái hữu hạn an toàn kiểu: `TrangThaiPhien` (`DANG_MO`, `DA_DONG`), `TrangThaiDiemDanh` (`CO_MAT`, `VANG`). |
| `switch expression` | Chương 6 (Ch6) | Slide Ch1 trang 6, Bảng 1.1 `main.pdf` | Phân nhánh xử lý các lệnh giao thức mạng và menu điều khiển console ngắn gọn, trả về kết quả trực tiếp. |
| `custom exception` | Chương 6 (Ch6) | Bảng 1.1 `main.pdf` | Phản ánh chính xác các lỗi nghiệp vụ: `PhienHetHanException`, `DaDiemDanhException`, `SinhVienKhongTonTaiException`, `IPDaDiemDanhException`. |
| `try-with-resources` | Chương 6 (Ch6) | Slide Ch5 trang 15, 16, 22, Bảng 1.1 | Đóng tự động và an toàn `ServerSocket`, `Socket`, luồng đọc `BufferedReader`, luồng ghi `PrintWriter`, tránh rò rỉ cổng mạng và file descriptor. |
| `text block` | Chương 6 (Ch6) | Bảng 1.1 `main.pdf` | Định dạng chuỗi nhiều dòng cho giao thức thông báo, thực đơn dòng lệnh và bảng báo cáo thống kê. |
| `Optional` | Chương 6 (Ch6) | Bảng 1.1 `main.pdf` | Xử lý an toàn khi tìm kiếm sinh viên, lấy phiên hiện tại, tránh `NullPointerException`. |
| `jar` | Chương 6 (Ch6) | Bảng 1.1 `main.pdf`, Mục 1.2 `main.pdf` | Đóng gói mã nguồn thành tệp JAR độc lập kèm manifest để chạy trực tiếp trên máy chủ/máy khách. |
| `java.time` | Chương 6 (Ch6) | Ràng buộc mục 1.2, Đề 43 mục Kỹ thuật bắt buộc | Quản lý thời gian mở/đóng phiên, tính thời lượng hết hạn, ghi dấu thời gian điểm danh (`LocalDateTime`, `Duration`). |
| `Streams API` | Chương 2 (Ch2) | Slide Ch2 trang 26–36 | Lọc danh sách sinh viên vắng mặt (`filter`), trích xuất mã (`map`), sắp xếp thứ tự (`sorted`), đếm số lượng (`count`). |
| `Collectors` | Chương 2 (Ch2) | Slide Ch2 trang 35–36 | Gom nhóm kết quả vào danh sách (`toList`), gom nhóm theo mã sinh viên để tính số buổi vắng cả kỳ (`groupingBy`, `counting`). |
| `Lambda` & `Functional Interface` | Chương 2 (Ch2) | Slide Ch2 trang 4–11, 20–25 | Cung cấp logic vị từ (`Predicate`), hàm biến đổi (`Function`), và tác vụ luồng (`Runnable`). |
| `method reference` (`::`) | Chương 2 (Ch2) | Slide Ch2 trang 10–11 | Viết ngắn gọn các lambda gọi phương thức có sẵn (`SinhVien::maSV`, `String::strip`, `System.out::println`). |
| `Set`, `HashSet` | Chương 3 (Ch3) | Slide Ch3 trang 15–17 | Lưu trữ tập hợp mã sinh viên đã điểm danh đảm bảo tính duy nhất, tra cứu $O(1)$. |
| `Map`, `HashMap` | Chương 3 (Ch3) | Slide Ch3 trang 18–21 | Bảng tra cứu thông tin sinh viên theo mã số sinh viên từ CSV nguồn với chi phí $O(1)$. |
| `equals và hashCode` | Chương 3 (Ch3) | Slide Ch3 trang 22–25 | Đảm bảo tính đúng đắn khi thêm và tìm kiếm đối tượng trong `Set` / `Map`. |
| `Comparable`, `Comparator` | Chương 3 (Ch3) | Slide Ch3 trang 26–29 | Sắp xếp danh sách điểm danh theo mã sinh viên, theo tên, hoặc theo tỷ lệ vắng giảm dần (`thenComparing`). |
| `Thread`, `Runnable` | Chương 4 (Ch4) | Slide Ch4 trang 6–9 | Mô hình hóa tác vụ độc lập chạy nền (tác vụ client handler, tác vụ hẹn giờ đóng phiên). |
| `volatile` | Chương 4 (Ch4) | Slide Ch4 trang 20 | Đảm bảo tính nhìn thấy tức thì giữa các luồng cho cờ trạng thái phiên `isSessionOpen`. |
| `synchronized` | Chương 4 (Ch4) | Slide Ch4 trang 18 | Bảo vệ vùng tới hạn khi ghi nhận lượt điểm danh, tránh tranh chấp dữ liệu (lost update / race condition). |
| `ExecutorService`, `ThreadPool` | Chương 4 (Ch4), Chương 5 (Ch5) | Slide Ch4 trang 23, Slide Ch5 trang 21–23 | Quản lý bể luồng phục vụ nhiều kết nối máy khách đồng thời hiệu quả, không làm sập server. |
| `ScheduledExecutorService` | Chương 4 (Ch4) | Đề 43 kỹ thuật bắt buộc, Slide Ch4 mục 5 | Lập lịch đếm ngược thời gian và tự động thực thi đóng phiên khi hết hạn. |
| `ConcurrentHashMap` | Chương 3 (Ch3), Chương 4 (Ch4) | Slide Ch4 trang 23, Bảng 1.1 `main.pdf` | Cấu trúc bảng băm an toàn luồng lưu trữ danh sách đã điểm danh và ánh xạ IP client mà không cần khóa toàn bộ bảng. |
| `ServerSocket`, `Socket` | Chương 5 (Ch5) | Slide Ch5 trang 11–18 | Thiết lập hạ tầng mạng TCP Client-Server truyền tin cậy qua cổng mạng nội bộ. |
| `BufferedReader`, `PrintWriter` | Chương 5 (Ch5) | Slide Ch5 trang 15–17 | Xử lý đọc và gửi chuỗi văn bản qua luồng mạng TCP có tự động đẩy đệm (`autoFlush = true`). |

---

### Kế hoạch kiểm tra tiến độ theo tuần (Căn cứ Bảng 1.2 `main.pdf`)

- **Tuần 5:** Hoàn thành thiết kế 2 trang (Bảng chức năng này, sơ đồ lớp UML, sơ đồ tuần tự sequence, phân công nhiệm vụ).
- **Tuần 8 (Demo tiến độ tại lớp - 3 phút):** Chạy được dữ liệu (đọc CSV F02) và các chức năng lõi trên máy chủ (F01, F03, F04, F05).
- **Tuần 9 đến 11:** Hoàn thiện đa máy khách qua mạng TCP (F07, F08), báo cáo thống kê bằng Streams (F09, F10), xuất CSV (F11), giao diện giảng viên (F12), và 2 chức năng mở rộng (F14, F15). Đóng gói JAR (F13).
- **Tuần 11:** Nộp gói sản phẩm đầy đủ gồm mã nguồn `src/`, `README.md`, `bao-cao.pdf`, `slide.pdf`, `khai-bao-AI.pdf`, và `git-log.txt`.
- **Tuần 12:** Báo cáo bảo vệ trước giảng viên (8 phút trình bày và demo, 3 phút thử thách sửa mã tại chỗ).

