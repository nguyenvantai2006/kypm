# Sơ đồ Hoạt động (Activity Diagram): UC-06 - Quản lý Bảo hành

**Dự án:** Phần mềm Quản lý Cửa hàng Giày (KYPM)
**Mục tiêu:** Trực quan hóa quy trình tiếp nhận bảo hành, kiểm tra thời hạn và tình trạng lỗi của sản phẩm. Phục vụ cho việc viết Unit Test cho lớp `BaoHanhBUS`.

```
flowchart TD
    %% Bắt đầu
    Start((Bắt đầu)) --> Open_UI[Mở giao diện BaoHanhPanel]
    Open_UI --> Input_Search[Nhập SĐT khách hàng\nhoặc Mã hóa đơn]
    Input_Search --> Search_DB[Truy vấn CSDL lấy lịch sử mua hàng]

    %% Kiểm tra tồn tại giao dịch (E1)
    Search_DB --> Check_Exist{Tìm thấy\ndữ liệu mua hàng?}
    Check_Exist -- Không (E1) --> Err_E1[UI: Báo lỗi 'Không tìm thấy dữ liệu']
    Err_E1 --> Input_Search
    
    %% Hiển thị và chọn sản phẩm
    Check_Exist -- Có --> Show_List[Hiển thị danh sách Sản phẩm đã mua]
    Show_List --> Select_Product[Nhân viên chọn Sản phẩm gặp lỗi]
    
    %% Kiểm tra thời hạn bảo hành (E2)
    Select_Product --> Check_Date{Còn trong thời hạn\nbảo hành không?}
    Check_Date -- Không (E2) --> Err_E2[UI: Cảnh báo 'Sản phẩm hết hạn bảo hành']
    Err_E2 --> Stop_Or_Fee[Dừng hoặc chuyển sang luồng\nsửa chữa tính phí]
    
    %% Nhập mô tả lỗi (E3)
    Check_Date -- Có --> Input_Defect[Nhập mô tả tình trạng lỗi\nvà hướng xử lý]
    Input_Defect --> Click_Create[Nhấn nút 'Tạo phiếu bảo hành']
    
    Click_Create --> Check_Desc{Bỏ trống\nmô tả lỗi?}
    Check_Desc -- Có (E3) --> Err_E3[UI: Báo lỗi 'Vui lòng nhập mô tả lỗi']
    Err_E3 --> Input_Defect
    
    %% Lưu CSDL
    Check_Desc -- Không --> Save_Ticket[BaoHanhDAO: Lưu Phiếu bảo hành vào CSDL]
    Save_Ticket --> UI_Success[UI: Thông báo 'Tạo phiếu thành công']
    UI_Success --> Refresh_UI[Gọi refreshData() làm sạch form\nvà tải lại danh sách phiếu]
    Refresh_UI --> End_Success((Kết thúc))

    %% Phân loại style
    classDef startEnd fill:#22c55e,stroke:#14532d,stroke-width:2px,color:#fff
    classDef uiAction fill:#bfdbfe,stroke:#2563eb,stroke-width:2px,color:#000
    classDef logicCheck fill:#fef08a,stroke:#ca8a04,stroke-width:2px,color:#000
    classDef errorState fill:#fca5a5,stroke:#dc2626,stroke-width:2px,color:#000
    classDef sysAction fill:#e9d5ff,stroke:#9333ea,stroke-width:2px,color:#000

    class Start,End_Success startEnd
    class Open_UI,Input_Search,Select_Product,Input_Defect,Click_Create,UI_Success,Err_E1,Err_E2,Err_E3,Stop_Or_Fee uiAction
    class Check_Exist,Check_Date,Check_Desc logicCheck
    class Search_DB,Show_List,Save_Ticket,Refresh_UI sysAction

```