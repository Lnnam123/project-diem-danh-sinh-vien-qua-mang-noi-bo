---
name: drawio
description: >-
  Sử dụng khi người dùng yêu cầu vẽ, tạo, thiết kế hoặc xuất các loại sơ đồ
  (phân rã chức năng, kiến trúc các tầng, sơ đồ lớp UML, sơ đồ tuần tự, lưu đồ quy trình)
  bằng Draw.io Desktop, xuất ra file .drawio và ảnh .drawio.png có nhúng sẵn XML theo chuẩn AJP201.
---

# Kỹ năng Tạo và Xuất Sơ đồ với Draw.io (`drawio`)

Kỹ năng này tích hợp công cụ Draw.io Desktop (theo chuẩn từ repository `jgraph/drawio-mcp`) vào dự án, cho phép AI tự động thiết kế các sơ đồ kỹ thuật và xuất trực tiếp ra định dạng `.drawio.png` (ảnh PNG hiển thị đẹp mắt, đồng thời nhúng nguyên vẹn mã XML để mở và chỉnh sửa trực tiếp bằng Draw.io Desktop).

---

## 1. Quy trình làm việc (The Pipeline)

Mỗi sơ đồ được tạo ra theo quy trình 3 bước:
1. **Thiết kế mô hình sơ đồ (Source Generation):**
   - Tạo mã nguồn sơ đồ dưới dạng file `.drawio` (chuẩn mxGraph XML) hoặc file `.mmd` (Mermaid). Ưu tiên định dạng XML native của Draw.io để kiểm soát chính xác hình dạng, màu sắc, font chữ và vị trí.
   - Lưu file nguồn tại thư mục `docs/so-do/<ten-so-do>.drawio`.

2. **Biên dịch và Xuất ảnh PNG nhúng XML (Export Phase):**
   - Chạy script tiện ích:
     ```powershell
     powershell -ExecutionPolicy Bypass -File .agents/skills/drawio/scripts/export-diagram.ps1 -InputFile docs/so-do/<ten-so-do>.drawio -OutputFile docs/so-do/<ten-so-do>.drawio.png -Scale 2
     ```
   - Cờ `-Scale 2` giúp ảnh xuất ra có độ phân giải cao (Retina/High-DPI) phục vụ chèn vào báo cáo PDF rõ nét, không vỡ hạt.

3. **Kiểm tra và Bàn giao (Verification):**
   - Đảm bảo tệp `.drawio.png` đã được sinh ra tại `docs/so-do/`.
   - Cung cấp link xem trước và hướng dẫn người dùng mở chỉnh sửa bằng Draw.io Desktop nếu cần.

---

## 2. Danh mục 5 sơ đồ chuẩn cho đồ án Java (AJP201)

Theo hướng dẫn `2.plan-trien-khai-project.md` (Bước 2), mọi đề tài đều phải có 5 sơ đồ sau trong thư mục `docs/so-do/`:

1. **Sơ đồ phân rã chức năng (Functional Tree):**
   - Tên tệp: `docs/so-do/so-do-phan-ra-chuc-nang.drawio` & `.drawio.png`
   - Nội dung: Cây phân cấp chức năng từ hệ thống tổng thể $\to$ Module (Quản lý phiên, Điểm danh, Thống kê, Mạng...) $\to$ Các chức năng F01 đến F15.
2. **Kiến trúc các tầng (Layer Stack Architecture):**
   - Tên tệp: `docs/so-do/so-do-kien-truc-cac-tang.drawio` & `.drawio.png`
   - Nội dung: Tầng Giao diện/Mạng (Client/Server) $\to$ Tầng Nghiệp vụ (Service/Core Logic) $\to$ Tầng Lưu trữ dữ liệu (File CSV).
   - *Quy tắc bắt buộc:* Tầng Nghiệp vụ hoàn toàn độc lập, không phụ thuộc vào giao diện hay mạng để phục vụ viết Unit Test.
3. **Sơ đồ lớp UML (Class Diagram):**
   - Tên tệp: `docs/so-do/so-do-lop-uml.drawio` & `.drawio.png`
   - Nội dung: Các `record`, `class`, `interface`, `enum`, các thuộc tính, phương thức và quan hệ (kế thừa, cài đặt, phụ thuộc).
4. **Sơ đồ tuần tự (Sequence Diagram):**
   - Tên tệp: `docs/so-do/so-do-tuan-tu.drawio` & `.drawio.png`
   - Nội dung: Luồng tương tác từng bước: Sinh viên gửi điểm danh qua Socket $\to$ Server nhận lệnh $\to$ Service kiểm tra và ghi nhận $\to$ Trả thông điệp phản hồi.
5. **Lưu đồ quy trình xử lý phần lõi (Core Flowchart):**
   - Tên tệp: `docs/so-do/luu-do-quy-trinh-phan-loi.drawio` & `.drawio.png`
   - Nội dung: Tiến trình tiếp nhận yêu cầu điểm danh: kiểm tra phiên còn mở $\to$ kiểm tra danh sách lớp $\to$ kiểm tra trùng mã SV $\to$ kiểm tra trùng IP $\to$ cập nhật kết quả.

---

## 3. Cấu trúc chuẩn XML của Draw.io (`.drawio`)

Mẫu chuẩn để tạo tệp XML tương thích hoàn toàn với Draw.io Desktop:

```xml
<mxfile host="Electron" agent="Antigravity" version="21.0.0" type="device">
  <diagram id="diagram_id" name="Page-1">
    <mxGraphModel dx="1200" dy="800" grid="1" gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" page="1" pageScale="1" pageWidth="1169" pageHeight="827" background="#ffffff" math="0" shadow="0">
      <root>
        <mxCell id="0" />
        <mxCell id="1" parent="0" />
        <!-- Các node và edge định nghĩa ở đây -->
      </root>
    </mxGraphModel>
  </diagram>
</mxfile>
```

---

## 4. Lệnh chạy và Xuất tự động

Khi cần xuất hàng loạt tất cả các sơ đồ trong thư mục `docs/so-do/`:
```powershell
Get-ChildItem docs/so-do/*.drawio | ForEach-Object {
    powershell -ExecutionPolicy Bypass -File .agents/skills/drawio/scripts/export-diagram.ps1 -InputFile $_.FullName -Scale 2
}
```

