# Sơ đồ Hoạt động (Activity Diagram): UC-08 - Quản lý Nhập hàng & Trả NCC

**Dự án:** Phần mềm Quản lý Cửa hàng Giày (KYPM)
**Mục tiêu:** Mô tả quy trình quản lý dòng hàng hóa đầu vào, bao gồm tạo phiếu nhập hàng (tăng tồn kho) và trả hàng cho nhà cung cấp (giảm tồn kho), kèm theo xử lý các ngoại lệ validation.

```
flowchart TD
    %% Khởi tạo
    Start((Bắt đầu)) --> Open_UI[Mở màn hình NhapHangPanel]
    Open_UI --> Choose_Action{Nhân viên chọn\nthao tác nào?}

    %% ================= LUỒNG 1: NHẬP HÀNG =================
    Choose_Action -- Nhập hàng --> Select_Import[Chọn Nhà cung cấp và thêm Sản phẩm]
    Select_Import --> Input_Import[Nhập Số lượng và Giá nhập]
    Input_Import --> Click_Import[Nhấn 'Lưu phiếu nhập']

    %% Validation E1 (Nhập)
    Click_Import --> Check_Empty_Imp{Bỏ trống SP hoặc\nSL/Giá nhập < 0?}
    Check_Empty_Imp -- Có (E1) --> Err_E1_Imp[UI: Báo lỗi 'Nhập đủ thông tin, SL và Giá > 0']
    Err_E1_Imp --> Input_Import

    Check_Empty_Imp -- Không --> Calc_Total[Hệ thống tính Tổng tiền Phiếu nhập]
    Calc_Total --> DB_Tx_Imp[Mở DB Transaction]
    DB_Tx_Imp --> Save_Import[Lưu Phiếu nhập vào CSDL]
    Save_Import --> Add_Stock[Cộng dồn Tồn kho Sản phẩm]
    Add_Stock --> Commit_Imp[Đóng Transaction]
    Commit_Imp --> Msg_Imp[UI: Thông báo 'Nhập hàng thành công']

    %% ================= LUỒNG 2: TRẢ HÀNG NCC =================
    Choose_Action -- Trả hàng --> Select_Return[Chuyển sang tab Trả hàng]
    Select_Return --> Input_Return[Nhập Mã phiếu, Mã SP, SL trả,\nvà Giá nhập hoàn lại (BigDecimal)]
    Input_Return --> Click_Return[Nhấn 'Tạo phiếu trả']

    %% Validation E1 (Trả)
    Click_Return --> Check_Empty_Ret{Bỏ trống SP hoặc\nSL/Giá nhập < 0?}
    Check_Empty_Ret -- Có (E1) --> Err_E1_Ret[UI: Báo lỗi 'Nhập đủ thông tin, SL và Giá > 0']
    Err_E1_Ret --> Input_Return

    %% Validation E2 (Tồn kho)
    Check_Empty_Ret -- Không --> Check_Stock{Số lượng trả >\nTồn kho hiện tại?}
    Check_Stock -- Có (E2) --> Err_E2[UI: Báo lỗi 'SL trả vượt quá tồn kho']
    Err_E2 --> Input_Return

    Check_Stock -- Không --> DB_Tx_Ret[Mở DB Transaction]
    DB_Tx_Ret --> Save_Return[Lưu ChiTietTraNccDTO vào CSDL]
    
    %% Xử lý Lỗi DB (E3)
    Save_Return --> Check_DB{Lỗi kết nối / Khóa ngoại?}
    Check_DB -- Có (E3) --> Err_E3[Rollback, ném Exception\nUI: Cảnh báo 'Lỗi hệ thống']
    Err_E3 --> End_Fail((Dừng))
    
    Check_DB -- Không --> Minus_Stock[Trừ Tồn kho Sản phẩm tương ứng]
    Minus_Stock --> Commit_Ret[Đóng Transaction]
    Commit_Ret --> Msg_Ret[UI: Thông báo 'Trả hàng thành công']

    %% ================= KẾT THÚC =================
    Msg_Imp --> Refresh_UI[Gọi refreshData() làm mới form và lưới dữ liệu]
    Msg_Ret --> Refresh_UI
    Refresh_UI --> End_Success((Kết thúc))

    %% Phân loại style
    classDef startEnd fill:#22c55e,stroke:#14532d,stroke-width:2px,color:#fff
    classDef uiAction fill:#bfdbfe,stroke:#2563eb,stroke-width:2px,color:#000
    classDef logicCheck fill:#fef08a,stroke:#ca8a04,stroke-width:2px,color:#000
    classDef errorState fill:#fca5a5,stroke:#dc2626,stroke-width:2px,color:#000
    classDef sysAction fill:#e9d5ff,stroke:#9333ea,stroke-width:2px,color:#000

    class Start,End_Success,End_Fail startEnd
    class Open_UI,Choose_Action,Select_Import,Input_Import,Click_Import,Select_Return,Input_Return,Click_Return,Msg_Imp,Msg_Ret,Err_E1_Imp,Err_E1_Ret,Err_E2,Err_E3 uiAction
    class Check_Empty_Imp,Check_Empty_Ret,Check_Stock,Check_DB logicCheck
    class Calc_Total,DB_Tx_Imp,Save_Import,Add_Stock,Commit_Imp,DB_Tx_Ret,Save_Return,Minus_Stock,Commit_Ret,Refresh_UI sysAction

```