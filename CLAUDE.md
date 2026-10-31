# CLAUDE.md

This file provides guidance to AI assistants working in this repository.

## Think Before Coding

- **Understand First:** Read and analyze relevant files, specifications, and architecture before proposing changes.
- **Identify Invariants:** Recognize and respect core project constraints, architectural boundaries, and conventions.
- **Trace Impact:** Consider ripple effects on dependents, consumers, and data persistence before modifying contracts.
- **Formulate a Plan:** Outline the approach with specific files and steps before executing non-trivial edits.

## Simplicity First

- **Minimal Surface:** Solve the specific problem directly; avoid speculative abstraction, unneeded indirection, or overengineering.
- **Idiomatic Style:** Match existing repository conventions, formatting, and patterns over personal preference.
- **Self-Contained Changes:** Prefer changes that make code clearer at the call site rather than hiding complexity behind new helper layers.

## Surgical Changes

- **Precise Edits:** Modify only what is strictly necessary. Avoid reformatting untouched code, reorganizing imports unnecessarily, or reordering members.
- **Preserve Behavior:** Ensure existing tests and contracts remain valid unless explicitly altering functionality.
- **No Scope Creep:** Keep refactorings separate from feature work or bug fixes.

## Goal-Driven Execution

- **Verify Incrementally:** Validate changes against tests, compiler checks, or manual verification steps at each phase.
- **Honest Feedback:** Report failures, unexpected side-effects, or blocked states proactively rather than guessing or hacking around them.
- **Clean State:** Leave the repository buildable and clean after every completed task.

---

## Dự án

- **Đề tài:** Đề 43 "Điểm danh lớp học qua mạng nội bộ" trong `main.pdf`.
- **Tài liệu tham chiếu:**
  - Bảng chức năng: `docs/danh-sach-chuc-nang.md`
  - Sơ đồ kiến trúc, phân rã, lớp UML, tuần tự: `docs/so-do/`
- **Cấu trúc package và các tầng:**
  - `vn.dau.diemdanh.model`: chứa thực thể và value objects (`record`, `enum`).
  - `vn.dau.diemdanh.nghiepvu`: logic điểm danh, tải danh sách, thống kê, phân loại. Tầng này độc lập, tuyệt đối không phụ thuộc trực tiếp vào giao diện (`giaodien`) hoặc mạng (`mang`).
  - `vn.dau.diemdanh.luutru`: đọc/ghi tệp CSV/TXT danh sách lớp và xuất kết quả.
  - `vn.dau.diemdanh.mang`: truyền nhận bản tin điểm danh qua TCP/UDP Socket.
  - `vn.dau.diemdanh.giaodien`: giao diện điều khiển (CLI / Console / Swing).
  - `vn.dau.diemdanh.chung`: các kiểu dùng chung trong toàn hệ thống.
- **Cách diễn đạt thất bại dùng chung:**
  - Dùng `sealed interface KetQua<T>` (`vn.dau.diemdanh.chung.KetQua`) với hai biến thể:
    - `KetQua.ThanhCong<T>(T duLieu)`
    - `KetQua.ThatBai<T>(String maLoi, String thongDiep)`
  - Mọi phương thức nghiệp vụ đều trả về kiểu `KetQua<T>` này.
- **Công nghệ & Thư viện:**
  - Sử dụng **Java 21 LTS** (Temurin-21.0.13+9).
  - Không thêm bất kỳ thư viện ngoài (third-party dependency) nào, ngoại trừ **JUnit 5** dùng cho kiểm thử (`test/`).
  - Chỉ dùng các kỹ thuật đã học trong slide các chương từ `chuong-01-slide.pdf` đến `chuong-06-slide.pdf` (record, pattern matching, try-with-resources, collection framework, thread/concurrency, I/O streams, socket).
- **Quy chuẩn mã nguồn:**
  - Đóng tệp, socket, luồng bằng `try-with-resources`.
  - Không dùng biến toàn cục; quản lý trạng thái an toàn đa luồng bằng `ConcurrentHashMap`, `AtomicInteger`, `CopyOnWriteArrayList` nếu có chia sẻ tài nguyên.
  - Mỗi lần chỉ làm một chức năng trong bảng `docs/danh-sach-chuc-nang.md`.
  - Tuyệt đối không sửa test cho xanh; khi test đỏ phải sửa mã nghiệp vụ/cài đặt.
- **Lệnh biên dịch và chạy chương trình:**
  - Biên dịch toàn bộ:
    ```powershell
    Get-ChildItem -Recurse -Filter *.java -Path src | ForEach-Object { Resolve-Path -Relative $_.FullName } | Set-Content sources.txt -Encoding ASCII
    javac -encoding UTF-8 -d bin "@sources.txt"
    ```
  - Chạy Giảng viên (Server):
    ```powershell
    java -cp bin vn.dau.diemdanh.giaodien.UngDungGiangVien
    ```
  - Chạy Sinh viên (Client):
    ```powershell
    java -cp bin vn.dau.diemdanh.giaodien.UngDungSinhVien
    ```
- **Giao tiếp:** Luôn trả lời và giải thích bằng **tiếng Việt**.
