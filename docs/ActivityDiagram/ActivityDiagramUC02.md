# Sơ đồ Hoạt động (Activity Diagram): UC-02 - Điều hướng và Kiểm soát quyền

**Dự án:** Phần mềm Quản lý Cửa hàng Giày (KYPM)
**Mục tiêu:** Mô tả luồng xử lý thiết lập giao diện dựa trên quyền hạn và luồng điều hướng giữa các chức năng trên `MainFrame`, phục vụ cho việc viết test logic phân quyền (như `MainFramePermissionTest`).

```mermaid
flowchart TD
    %% Khởi tạo
    Start((Bắt đầu)) --> Init_MainFrame[Khởi tạo màn hình MainFrame]
    Init_MainFrame --> Check_Session{AuthSession\ncó hợp lệ?}

    %% Ngoại lệ E3: Mất phiên làm việc
    Check_Session -- Không (E3) --> Err_E3[UI: Báo lỗi 'Phiên đăng nhập không hợp lệ']
    Err_E3 --> Go_Login[Đóng MainFrame,\nMở lại LoginFrame]
    Go_Login --> End_Fail((Kết thúc / Dừng))

    %% Luồng cấp quyền UI
    Check_Session -- Có --> Read_Quyen[Đọc QuyenDTO từ AuthSession]
    Read_Quyen --> Loop_Menu[Duyệt qua danh sách các menu chức năng]
    
    Loop_Menu --> Eval_Perm{Kiểm tra quyền\n(permissionForKey)?}
    
    %% Xử lý hiển thị (E1)
    Eval_Perm -- Không (E1) --> Disable_Menu[Ẩn hoặc Disable nút menu đó]
    Disable_Menu --> Wait_Click
    
    Eval_Perm -- Có --> Enable_Menu[Hiển thị và Enable nút menu]
    Enable_Menu --> Wait_Click

    %% Sự kiện tương tác
    Wait_Click[Chờ người dùng click chọn Menu] --> User_Click[Người dùng click vào chức năng\n(VD: Bán hàng, Thống kê)]
    
    %% Kiểm tra bảo mật vòng 2 (E2)
    User_Click --> Verify_Bypass{Có quyền truy cập\nPanel này không?}
    
    Verify_Bypass -- Không (E2) --> Err_E2[UI: Báo lỗi 'Không có quyền truy cập']
    Err_E2 --> Wait_Click
    
    %% Chuyển hướng thành công
    Verify_Bypass -- Có --> Load_Panel[Khởi tạo và Load Panel đích\n(VD: BanHangPanel)]
    Load_Panel --> Refresh[Gọi refreshData() để làm mới dữ liệu]
    Refresh --> Show_Panel[Hiển thị Panel lên vùng trung tâm]
    Show_Panel --> End_Success((Thành công\nSẵn sàng nghiệp vụ))

    %% Phân loại style
    classDef startEnd fill:#22c55e,stroke:#14532d,stroke-width:2px,color:#fff
    classDef uiAction fill:#bfdbfe,stroke:#2563eb,stroke-width:2px,color:#000
    classDef logicCheck fill:#fef08a,stroke:#ca8a04,stroke-width:2px,color:#000
    classDef errorState fill:#fca5a5,stroke:#dc2626,stroke-width:2px,color:#000
    classDef sysAction fill:#e9d5ff,stroke:#9333ea,stroke-width:2px,color:#000

    class Start,End_Fail,End_Success startEnd
    class Init_MainFrame,Err_E3,Err_E2,Disable_Menu,Enable_Menu,Wait_Click,Go_Login,Show_Panel uiAction
    class Check_Session,Eval_Perm,Verify_Bypass logicCheck
    class Read_Quyen,Loop_Menu,Load_Panel,Refresh sysAction
```