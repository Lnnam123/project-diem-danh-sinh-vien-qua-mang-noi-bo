# Đề tài 43: Điểm danh lớp học qua mạng nội bộ
**Học phần:** Lập trình Java nâng cao (AJP201)  
**Môi trường:** Java JDK 21 LTS (chỉ dùng thư viện chuẩn `java.util`, `java.util.concurrent`, `java.net`, `java.io`, `java.nio`, `java.time` và JUnit 5 cho kiểm thử)

---

## 1. Giới thiệu bài toán
Hệ thống cho phép giảng viên mở phiên điểm danh lớp học có giới hạn thời gian qua mạng nội bộ (LAN / WiFi phòng học). Sinh viên sử dụng ứng dụng máy khách TCP để gửi mã điểm danh. Máy chủ xử lý đồng thời qua bể luồng, tự động đóng phiên khi hết hạn bằng luồng hẹn giờ, chống trùng lặp mã sinh viên, chống gửi hộ qua IP, đối chiếu danh sách lớp từ file CSV, tổng hợp danh sách vắng bằng Java Streams API và xuất báo cáo CSV.

---

## 2. Cây thư mục dự án

```text
project-Điểm danh lớp học qua mạng nội bộ/
├── .agents/                            # Kỹ năng Antigravity (drawio, superpowers)
│   └── skills/
├── data/
│   ├── danh-sach-lop.csv               # File CSV danh sách sinh viên lớp học
│   └── ket-qua/                        # Thư mục lưu kết quả điểm danh từng buổi
├── docs/
│   ├── danh-sach-chuc-nang.md          # Đặc tả 15 chức năng chi tiết và kỹ thuật Java
│   └── so-do/                          # 5 sơ đồ hệ thống (.drawio và .drawio.png)
│       ├── so-do-phan-ra-chuc-nang.drawio.png
│       ├── so-do-kien-truc-cac-tang.drawio.png
│       ├── so-do-lop-uml.drawio.png
│       ├── so-do-tuan-tu.drawio.png
│       └── luu-do-quy-trinh-phan-loi.drawio.png
├── lib/                                # Thư viện bên ngoài (chỉ dùng JUnit 5)
│   └── junit-platform-console-standalone-1.10.2.jar
├── src/
│   └── vn/
│       └── dau/
│           └── diemdanh/
│               ├── model/              # Tầng Mô hình dữ liệu (record, enum)
│               │   ├── SinhVien.java
│               │   ├── PhienDiemDanh.java
│               │   ├── LuotDiemDanh.java
│               │   ├── TrangThaiPhien.java
│               │   ├── TrangThaiDiemDanh.java
│               │   ├── BuoiDiemDanh.java
│               │   └── CanhBaoChuyenCan.java
│               ├── exception/          # Ngoại lệ nghiệp vụ tự định nghĩa
│               │   ├── DiemDanhException.java
│               │   ├── PhienHetHanException.java
│               │   ├── DaDiemDanhException.java
│               │   ├── SinhVienKhongTonTaiException.java
│               │   ├── IPDaDiemDanhException.java
│               │   └── GiaoThucException.java
│               ├── nghiepvu/           # Tầng Nghiệp vụ ĐỘC LẬP
│               │   ├── KetQua.java     # sealed interface kết quả dùng chung
│               │   ├── PhienManager.java
│               │   ├── ChongGianLanService.java
│               │   ├── ThongKeService.java
│               │   └── DiemDanhService.java
│               ├── luutru/             # Tầng Đọc/Ghi file CSV
│               │   └── CsvStorageService.java
│               ├── mang/               # Tầng Mạng TCP Server/Client
│               │   ├── RequestCommand.java
│               │   ├── ProtocolParser.java
│               │   ├── ClientHandler.java
│               │   ├── DiemDanhServer.java
│               │   └── StudentClient.java
│               └── giaodien/           # Tầng Giao diện Console Giảng viên
│                   └── TeacherConsoleUI.java
├── bin/                                # Thư mục chứa các tệp bytecode sau khi biên dịch (.class)
├── test/                               # Thư mục kiểm thử Unit Test (JUnit 5)
│   └── vn/
│       └── dau/
│           └── diemdanh/
│               └── model/
│                   └── SinhVienTest.java
├── CLAUDE.md                           # Bộ nguyên tắc và quy chuẩn dự án cho AI
├── README.md                           # Tài liệu hướng dẫn dự án
└── 2.plan-trien-khai-project.md        # Kế hoạch chi tiết 6 bước triển khai
```

---

## 3. Hướng dẫn biên dịch và chạy bằng dòng lệnh

### 3.1. Biên dịch toàn bộ mã nguồn
Mở terminal (PowerShell hoặc Command Prompt) tại thư mục gốc của dự án và chạy:

```powershell
# Tạo thư mục bin nếu chưa có
if (-not (Test-Path bin)) { New-Item -ItemType Directory -Path bin | Out-Null }

# Tạo danh sách các file nguồn và biên dịch bằng javac (Java 21 UTF-8)
Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { Resolve-Path -Relative $_.FullName } | Out-File -FilePath sources.txt -Encoding ascii
javac -encoding UTF-8 -d bin "@sources.txt"
Remove-Item sources.txt
```

### 3.2. Chạy ứng dụng Máy chủ Giảng viên (Teacher Console)
Chạy lớp chính quản trị điều khiển phiên điểm danh:
```powershell
java -cp bin vn.dau.diemdanh.giaodien.TeacherConsoleUI
```

### 3.3. Chạy ứng dụng Máy khách Sinh viên (Student Client)
Mở một cửa sổ dòng lệnh khác để chạy ứng dụng client gửi mã điểm danh:
```powershell
# Cú pháp chạy dòng lệnh:
java -cp bin vn.dau.diemdanh.mang.StudentClient <IP_Server> <Port> <Mã_Phiên> <Mã_SV>

# Ví dụ gửi điểm danh tới máy chủ chạy tại localhost cổng 8888:
java -cp bin vn.dau.diemdanh.mang.StudentClient localhost 8888 P01 22CNTT01
```

---

## 4. Kiểm thử tự động với JUnit 5

### 4.1. Biên dịch mã kiểm thử
Biên dịch các tệp kiểm thử trong thư mục `test/` với classpath gồm `bin/` và thư viện JUnit 5 standalone trong `lib/`:

```powershell
Get-ChildItem -Path test -Recurse -Filter *.java | ForEach-Object { Resolve-Path -Relative $_.FullName } | Out-File -FilePath test-sources.txt -Encoding ascii
javac -encoding UTF-8 -cp "bin;lib/*" -d bin "@test-sources.txt"
Remove-Item test-sources.txt
```

### 4.2. Chạy toàn bộ kiểm thử
Chạy bộ kiểm thử tự động bằng JUnit 5 Platform Console Launcher:

```powershell
java -jar lib/junit-platform-console-standalone-1.10.2.jar execute --class-path bin --scan-class-path
```

Kết quả hiển thị trên console:
```text
JUnit Jupiter ✔
  Kiểm thử thực thể SinhVien ✔
    ✔ Khởi tạo sinh viên hợp lệ thành công
    ✔ Chuỗi trắng thừa ở hai đầu được cắt gọn tự động (strip)
    ✔ Ném NullPointerException khi có trường thông tin bị null
    ✔ Hai sinh viên cùng thông tin có equals và hashCode bằng nhau (record equality)

[ 4 tests found ]
[ 4 tests successful ]
[ 0 tests failed ]
```

---

## 5. Đóng gói ứng dụng thành file JAR độc lập (Chương 6)

### 5.1. Đóng gói Server JAR
```powershell
jar --create --file diemdanh-server.jar --main-class vn.dau.diemdanh.giaodien.TeacherConsoleUI -C bin .
# Chạy server từ file JAR:
java -jar diemdanh-server.jar
```

### 5.2. Đóng gói Client JAR
```powershell
jar --create --file diemdanh-client.jar --main-class vn.dau.diemdanh.mang.StudentClient -C bin .
# Chạy client từ file JAR:
java -jar diemdanh-client.jar localhost 8888 P01 22CNTT01
```
