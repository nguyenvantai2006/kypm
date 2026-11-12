# Sơ đồ Hoạt động (Activity Diagram): UC-05 - Quản lý Đổi trả hàng hóa

**Dự án:** Phần mềm Quản lý Cửa hàng Giày (KYPM)
**Mục tiêu:** Mô tả chi tiết luồng xử lý đổi/trả hàng hóa, từ việc xác thực hóa đơn cũ, kiểm tra chính sách, đến tính toán bù trừ tiền (sử dụng `BigDecimal`) và cập nhật lại kho hàng. Phục vụ đắc lực cho việc viết Unit Test của `DoiTraBUS`.

```
flowchart TD
    %% Bắt đầu và Tìm kiếm hóa đơn
    Start((Bắt đầu)) --> Open_UI[Mở giao diện DoiTraPanel]
    Open_UI --> Input_Invoice[Nhập mã hóa đơn cần đổi/trả]
    Input_Invoice --> Search_DB[Truy vấn CSDL lấy thông tin Hóa đơn]

    %% Kiểm tra Hóa đơn (E1)
    Search_DB --> Check_Exist{Hóa đơn\ncó tồn tại?}
    Check_Exist -- Không (E1) --> Err_E1[UI: Báo lỗi 'Không tìm thấy hóa đơn']
    Err_E1 --> Input_Invoice
    
    %% Kiểm tra Chính sách (E2)
    Check_Exist -- Có --> Check_Policy{Còn trong\nthời hạn đổi trả?}
    Check_Policy -- Không (E2) --> Err_E2[UI: Báo lỗi 'Quá hạn đổi trả']
    Err_E2 --> Input_Invoice
    
    %% Chọn sản phẩm trả và kiểm tra số lượng (E3)
    Check_Policy -- Có --> Display_Inv[Hiển thị chi tiết Hóa đơn]
    Display_Inv --> Select_Return[Nhân viên chọn SP trả và nhập Số lượng]
    
    Select_Return --> Check_Qty{SL trả <=\nSL đã mua?}
    Check_Qty -- Không (E3) --> Err_E3[UI: Báo lỗi 'SL trả vượt quá SL mua gốc']
    Err_E3 --> Select_Return
    
    %% Luồng Đổi hàng và Kiểm tra tồn kho (E4)
    Check_Qty -- Có --> Is_Exchange{Khách có muốn\nđổi lấy SP mới?}
    
    Is_Exchange -- Có --> Select_New[Chọn SP mới muốn đổi]
    Select_New --> Check_Stock{SP mới đủ\ntồn kho?}
    Check_Stock -- Không (E4) --> Err_E4[UI: Báo lỗi 'SP đổi đã hết hàng']
    Err_E4 --> Select_New
    
    %% Tính toán và Xác nhận
    Check_Stock -- Có --> Calc_Diff[Tính tiền chênh lệch\n(Dùng BigDecimal)]
    Is_Exchange -- Không (Chỉ trả) --> Calc_Diff
    
    Calc_Diff --> Confirm[Nhấn 'Xác nhận đổi/trả']
    
    %% Giao dịch CSDL (Transaction)
    Confirm --> DB_Tx[Mở DB Transaction]
    DB_Tx --> Save_Ticket[Tạo Phiếu Đổi Trả]
    Save_Ticket --> Update_Stock[Cộng tồn kho SP trả\nTrừ tồn kho SP lấy mới]
    Update_Stock --> Update_Finance[Khấu trừ/Cập nhật Doanh thu & Lợi nhuận]
    
    Update_Finance --> Commit_Tx[Đóng Transaction thành công]
    Commit_Tx --> UI_Success[UI: Thông báo thành công]
    UI_Success --> Refresh_UI[Gọi refreshData() làm sạch form]
    Refresh_UI --> End_Success((Kết thúc))

    %% Phân loại style
    classDef startEnd fill:#22c55e,stroke:#14532d,stroke-width:2px,color:#fff
    classDef uiAction fill:#bfdbfe,stroke:#2563eb,stroke-width:2px,color:#000
    classDef logicCheck fill:#fef08a,stroke:#ca8a04,stroke-width:2px,color:#000
    classDef errorState fill:#fca5a5,stroke:#dc2626,stroke-width:2px,color:#000
    classDef sysAction fill:#e9d5ff,stroke:#9333ea,stroke-width:2px,color:#000

    class Start,End_Success startEnd
    class Open_UI,Input_Invoice,Select_Return,Select_New,Confirm,UI_Success,Err_E1,Err_E2,Err_E3,Err_E4 uiAction
    class Check_Exist,Check_Policy,Check_Qty,Is_Exchange,Check_Stock logicCheck
    class Search_DB,Display_Inv,Calc_Diff,DB_Tx,Save_Ticket,Update_Stock,Update_Finance,Commit_Tx,Refresh_UI sysAction

```