# Sơ đồ Hoạt động (Activity Diagram): UC-03 - Quản lý Bán hàng tại quầy

**Dự án:** Phần mềm Quản lý Cửa hàng Giày (KYPM)
**Mục tiêu:** Trực quan hóa quy trình bán hàng, thêm sản phẩm vào giỏ, kiểm tra tồn kho, xử lý thanh toán và lưu dữ liệu. Phục vụ cho việc viết Unit Test cho `BanHangBUS` và `SanPhamBUS`.

```
flowchart TD
    %% Bắt đầu và Tìm kiếm sản phẩm
    Start((Bắt đầu)) --> UI_Search[Nhân viên tìm/quét\nmã Sản phẩm]
    UI_Search --> Get_Info[Truy xuất thông tin\ntừ SanPhamBUS]
    Get_Info --> Input_Qty[Nhân viên nhập số lượng\nmuốn mua]

    %% Kịch bản Hủy hóa đơn (E4)
    Input_Qty -.-> |Người dùng chọn Hủy (E4)| Cancel_Order[Xóa giỏ hàng,\nLàm mới giao diện]
    Cancel_Order -.-> Start

    %% Kiểm tra tồn kho (E1)
    Input_Qty --> Check_Stock{Số lượng mua >\nTồn kho?}
    Check_Stock -- Có (E1) --> Err_E1[UI: Báo lỗi 'Tồn kho không đủ']
    Err_E1 --> UI_Search
    
    Check_Stock -- Không --> Add_Cart[Thêm sản phẩm vào\nGiỏ hàng (Hóa đơn tạm)]
    Add_Cart --> Calc_Total[Hệ thống tính\nTổng tiền hóa đơn]
    
    %% Tùy chọn thêm khách hàng
    Calc_Total --> Add_Cust[Tùy chọn: Chọn/Thêm\nthông tin Khách hàng]
    
    %% Nhập tiền và thanh toán
    Add_Cust --> Input_Cash[Nhập số tiền\nKhách đưa]
    Input_Cash --> Click_Pay[Nhấn nút 'Thanh toán']
    
    %% Kiểm tra Giỏ hàng (E2)
    Click_Pay --> Check_Empty{Giỏ hàng\ncó trống?}
    Check_Empty -- Có (E2) --> Err_E2[UI: Báo lỗi 'Vui lòng thêm SP']
    Err_E2 --> UI_Search
    
    %% Kiểm tra Tiền khách đưa (E3)
    Check_Empty -- Không --> Check_Cash{Tiền khách đưa <\nTổng tiền?}
    Check_Cash -- Có (E3) --> Err_E3[UI: Báo lỗi 'Tiền khách đưa không đủ']
    Err_E3 --> Input_Cash
    
    %% Xử lý Giao dịch (Transaction)
    Check_Cash -- Không --> DB_Tx[Mở Database Transaction]
    DB_Tx --> Save_Invoice[Lưu chi tiết Hóa đơn]
    Save_Invoice --> Minus_Stock[SanPhamBUS:\nTrừ số lượng tồn kho]
    Minus_Stock --> Update_Stats[Thống kê:\nCộng doanh thu, tích điểm KH]
    
    Update_Stats --> Commit_Tx[Đóng Transaction thành công]
    Commit_Tx --> Show_Success[UI: Báo 'Thanh toán thành công',\nIn hóa đơn]
    Show_Success --> Refresh_UI[Gọi refreshData()\nlàm mới BanHangPanel]
    Refresh_UI --> End_Success((Kết thúc\nSẵn sàng đơn mới))

    %% Phân loại style
    classDef startEnd fill:#22c55e,stroke:#14532d,stroke-width:2px,color:#fff
    classDef uiAction fill:#bfdbfe,stroke:#2563eb,stroke-width:2px,color:#000
    classDef logicCheck fill:#fef08a,stroke:#ca8a04,stroke-width:2px,color:#000
    classDef errorState fill:#fca5a5,stroke:#dc2626,stroke-width:2px,color:#000
    classDef sysAction fill:#e9d5ff,stroke:#9333ea,stroke-width:2px,color:#000

    class Start,End_Success startEnd
    class UI_Search,Input_Qty,Input_Cash,Click_Pay,Add_Cust,Show_Success,Err_E1,Err_E2,Err_E3,Cancel_Order uiAction
    class Check_Stock,Check_Empty,Check_Cash logicCheck
    class Get_Info,Add_Cart,Calc_Total,DB_Tx,Save_Invoice,Minus_Stock,Update_Stats,Commit_Tx,Refresh_UI sysAction

```