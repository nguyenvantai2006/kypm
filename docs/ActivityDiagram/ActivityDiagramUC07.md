# Sơ đồ Hoạt động (Activity Diagram): UC-07 - Quản lý Danh mục Sản phẩm

**Dự án:** Phần mềm Quản lý Cửa hàng Giày (KYPM)
**Mục tiêu:** Mô tả luồng xử lý CRUD (Thêm, Sửa, Xóa) danh mục sản phẩm, tập trung vào việc bắt lỗi dữ liệu đầu vào (giá âm, trùng mã) và xử lý an toàn khi xóa sản phẩm đã có lịch sử giao dịch. Phục vụ cho việc viết Unit Test của `SanPhamBUS`.

```
flowchart TD
    %% Khởi tạo và Tải dữ liệu
    Start((Bắt đầu)) --> Open_UI[Mở màn hình SanPhamPanel]
    Open_UI --> Load_List[SanPhamBUS: Tải danh sách Sản phẩm từ CSDL]
    Load_List --> User_Action{Người dùng chọn\nthao tác gì?}

    %% ================= LUỒNG THÊM / SỬA =================
    User_Action -- Thêm / Sửa --> Input_Info[Nhập thông tin SP:\nMã SKU, Tên, Giá bán, Tồn kho...]
    Input_Info --> Click_Save[Nhấn nút 'Lưu']

    %% Validation E1: Thiếu thông tin
    Click_Save --> Check_Empty{Thiếu thông tin\nbắt buộc?}
    Check_Empty -- Có (E1) --> Err_E1[UI: Báo lỗi 'Vui lòng nhập đủ các trường *']
    Err_E1 --> Input_Info

    %% Validation E3: Lỗi kiểu số / Số âm
    Check_Empty -- Không --> Check_Number{Giá bán / Tồn kho\nlà số âm hoặc sai định dạng?}
    Check_Number -- Có (E3) --> Err_E3[UI: Báo lỗi 'Giá bán và Số lượng phải >= 0']
    Err_E3 --> Input_Info

    %% Validation E2: Trùng lặp SKU
    Check_Number -- Không --> Call_BUS[Gửi dữ liệu xuống SanPhamBUS]
    Call_BUS --> Check_SKU{Mã SKU đã\ntồn tại? (Chỉ khi Thêm)}
    Check_SKU -- Có (E2) --> Err_E2[UI: Báo lỗi 'Mã sản phẩm đã tồn tại']
    Err_E2 --> Input_Info

    %% Lưu thành công
    Check_SKU -- Không --> Save_DB[SanPhamDAO: Thực thi lệnh INSERT/UPDATE]
    Save_DB --> UI_Success[UI: Thông báo 'Lưu thành công']
    UI_Success --> Refresh_UI[Gọi refreshData() làm mới lưới dữ liệu]

    %% ================= LUỒNG XÓA =================
    User_Action -- Xóa --> Select_Del[Chọn Sản phẩm cần xóa]
    Select_Del --> Click_Del[Nhấn nút 'Xóa']
    Click_Del --> Check_FK{SP đã từng\nđược bán chưa?}
    
    %% Xử lý E4: Dính khóa ngoại (Đã có giao dịch)
    Check_FK -- Có (E4) --> Soft_Delete[SanPhamDAO: Chuyển trạng thái\nthành 'Ngừng kinh doanh' (Soft Delete)]
    Soft_Delete --> Msg_Soft[UI: Thông báo 'Sản phẩm đã ẩn do có lịch sử GD']
    Msg_Soft --> Refresh_UI
    
    %% Xóa vĩnh viễn (Hard Delete)
    Check_FK -- Không --> Hard_Delete[SanPhamDAO: Thực thi lệnh DELETE]
    Hard_Delete --> Msg_Hard[UI: Thông báo 'Xóa thành công']
    Msg_Hard --> Refresh_UI
    
    %% Kết thúc chung
    Refresh_UI --> End_Success((Kết thúc))

    %% Phân loại style
    classDef startEnd fill:#22c55e,stroke:#14532d,stroke-width:2px,color:#fff
    classDef uiAction fill:#bfdbfe,stroke:#2563eb,stroke-width:2px,color:#000
    classDef logicCheck fill:#fef08a,stroke:#ca8a04,stroke-width:2px,color:#000
    classDef errorState fill:#fca5a5,stroke:#dc2626,stroke-width:2px,color:#000
    classDef sysAction fill:#e9d5ff,stroke:#9333ea,stroke-width:2px,color:#000

    class Start,End_Success startEnd
    class Open_UI,User_Action,Input_Info,Click_Save,Select_Del,Click_Del,UI_Success,Msg_Soft,Msg_Hard,Err_E1,Err_E2,Err_E3 uiAction
    class Check_Empty,Check_Number,Check_SKU,Check_FK logicCheck
    class Load_List,Call_BUS,Save_DB,Soft_Delete,Hard_Delete,Refresh_UI sysAction

```