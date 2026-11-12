# Sơ đồ Hoạt động (Activity Diagram): UC-04 - Quản lý Khách hàng

**Dự án:** Phần mềm Quản lý Cửa hàng Giày (KYPM)
**Mục tiêu:** Mô tả luồng thêm mới/cập nhật thông tin khách hàng, các bước validation dữ liệu đầu vào và xử lý ngoại lệ trùng lặp. Phục vụ cho việc viết Unit Test cho lớp Khách hàng BUS/DAO.

```
flowchart TD
    %% Khởi tạo và Tải dữ liệu
    Start((Bắt đầu)) --> Open_UI[Mở màn hình KhachHangPanel]
    Open_UI --> Load_List[Truy xuất DB:\nTải danh sách Khách hàng]
    Load_List --> Input_Info[Nhân viên nhập thông tin:\nTên, SĐT, Địa chỉ...]
    
    %% Kích hoạt lưu
    Input_Info --> Click_Save[Nhấn nút 'Lưu' hoặc 'Thêm']
    
    %% Validation cơ bản tại UI/BUS (E1)
    Click_Save --> Check_Empty{Bỏ trống\nthông tin bắt buộc?}
    Check_Empty -- Có (E1) --> Err_E1[UI: Báo lỗi 'Thiếu thông tin bắt buộc']
    Err_E1 --> Input_Info
    
    %% Kiểm tra định dạng (E3)
    Check_Empty -- Không --> Check_Format{Sai định dạng\n(VD: SĐT chứa chữ)?}
    Check_Format -- Có (E3) --> Err_E3[UI: Báo lỗi 'Định dạng không hợp lệ']
    Err_E3 --> Input_Info
    
    %% Gửi xuống BUS và DB (E2)
    Check_Format -- Không --> Call_BUS[KhachHangBUS: Xử lý nghiệp vụ]
    Call_BUS --> Check_Dup{SĐT đã\ntồn tại trong DB?}
    Check_Dup -- Có (E2) --> Err_E2[UI/DAO: Báo lỗi 'SĐT đã được đăng ký']
    Err_E2 --> Input_Info
    
    %% Luồng thành công
    Check_Dup -- Không --> Save_DB[KhachHangDAO:\nThực thi lệnh INSERT / UPDATE]
    Save_DB --> UI_Success[UI: Hiển thị 'Lưu thành công']
    UI_Success --> Refresh_Data[Gọi refreshData()\nlàm mới danh sách]
    Refresh_Data --> End_Success((Thành công))

    %% Phân loại style
    classDef startEnd fill:#22c55e,stroke:#14532d,stroke-width:2px,color:#fff
    classDef uiAction fill:#bfdbfe,stroke:#2563eb,stroke-width:2px,color:#000
    classDef logicCheck fill:#fef08a,stroke:#ca8a04,stroke-width:2px,color:#000
    classDef errorState fill:#fca5a5,stroke:#dc2626,stroke-width:2px,color:#000
    classDef sysAction fill:#e9d5ff,stroke:#9333ea,stroke-width:2px,color:#000

    class Start,End_Success startEnd
    class Open_UI,Input_Info,Click_Save,Err_E1,Err_E2,Err_E3,UI_Success uiAction
    class Check_Empty,Check_Format,Check_Dup logicCheck
    class Load_List,Call_BUS,Save_DB,Refresh_Data sysAction
```