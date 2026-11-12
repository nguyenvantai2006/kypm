# Sơ đồ Hoạt động (Activity Diagram): UC-01 - Đăng nhập

**Dự án:** Phần mềm Quản lý Cửa hàng Giày (KYPM)
**Mục tiêu:** Mô tả luồng xử lý chi tiết của Use Case 01, bao gồm cả luồng chính (Happy Path) và các nhánh lỗi (Alternative Flows) để phục vụ việc viết Unit Test (đặc biệt là cho lớp `AuthBUS`).

```mermaid
flowchart TD
    %% Khởi tạo
    Start((Bắt đầu)) --> UI_Load[Hiển thị màn hình LoginFrame]
    UI_Load --> Input[Nhân viên nhập Username & Password\nNhấn 'Đăng nhập']

    %% Validation cơ bản (E1)
    Input --> Validate{Dữ liệu bị\ntrống?}
    Validate -- Có --> Err_E1[UI: Báo lỗi 'Vui lòng nhập đầy đủ...']
    Err_E1 --> Input
    Validate -- Không --> Call_BUS[Gửi yêu cầu tới AuthBUS]

    %% Xử lý nghiệp vụ và truy xuất DB
    Call_BUS --> DB_Connect{Lỗi kết nối\nCSDL?}
    DB_Connect -- Có (E4) --> Err_E4[UI: Báo lỗi kết nối DB]
    Err_E4 --> End((Kết thúc / Dừng))
    
    DB_Connect -- Không --> Check_Cred{Sai tài khoản\nhoặc mật khẩu?}
    Check_Cred -- Có (E2) --> Err_E2[UI: Báo lỗi 'Sai thông tin...']
    Err_E2 --> Input
    
    Check_Cred -- Không --> Check_Active{Tài khoản\nbị khóa?}
    Check_Active -- Có (E3) --> Err_E3[UI: Báo lỗi 'Tài khoản bị khóa...']
    Err_E3 --> Input
    
    %% Luồng thành công (Happy Path)
    Check_Active -- Không --> Load_Role[QuyenDAO: Tải cờ quyền hạn]
    Load_Role --> Create_Session[Khởi tạo AuthSession\nchứa NhanVienDTO & QuyenDTO]
    Create_Session --> Switch_UI[Đóng LoginFrame,\nMở MainFrame]
    Switch_UI --> Pass_Session[Truyền AuthSession\nvào MainFrame]
    Pass_Session --> End_Success((Thành công))

    %% Phân loại style
    classDef startEnd fill:#22c55e,stroke:#14532d,stroke-width:2px,color:#fff
    classDef uiAction fill:#bfdbfe,stroke:#2563eb,stroke-width:2px,color:#000
    classDef logicCheck fill:#fef08a,stroke:#ca8a04,stroke-width:2px,color:#000
    classDef errorState fill:#fca5a5,stroke:#dc2626,stroke-width:2px,color:#000
    classDef sysAction fill:#e9d5ff,stroke:#9333ea,stroke-width:2px,color:#000

    class Start,End,End_Success startEnd
    class UI_Load,Input,Err_E1,Err_E2,Err_E3,Err_E4,Switch_UI uiAction
    class Validate,DB_Connect,Check_Cred,Check_Active logicCheck
    class Call_BUS,Load_Role,Create_Session,Pass_Session sysAction
```