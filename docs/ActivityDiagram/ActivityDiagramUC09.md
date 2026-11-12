# Sơ đồ Hoạt động (Activity Diagram): UC-09 - Thống kê và Báo cáo

**Dự án:** Phần mềm Quản lý Cửa hàng Giày (KYPM)
**Mục tiêu:** Mô tả luồng trích xuất dữ liệu, tính toán tài chính (Doanh thu, Lợi nhuận) và xử lý hiển thị biểu đồ. Phục vụ cho việc viết Unit Test cho `LoiNhuanTheoKyDTO` và `ThongKeDAO`.

```
flowchart TD
    %% Khởi tạo
    Start((Bắt đầu)) --> Open_UI[Mở màn hình ThongKePanel]
    Open_UI --> Choose_Criteria[Quản lý chọn tiêu chí thống kê:\n(Ngày/Tháng/Năm, Top SP, Top KH)]
    Choose_Criteria --> Send_Req[Gửi yêu cầu truy xuất dữ liệu]

    %% Truy xuất Database
    Send_Req --> Call_DAO[ThongKeDAO: Mở kết nối DBConnect]
    Call_DAO --> Exec_Query[Thực thi truy vấn SQL\n(SELECT, SUM, GROUP BY...)]

    %% Bắt lỗi truy vấn (E3)
    Exec_Query --> Check_SQL_Err{Có lỗi kết nối\nhoặc Timeout?}
    Check_SQL_Err -- Có (E3) --> Err_E3[UI: Báo lỗi 'Lỗi truy xuất dữ liệu từ máy chủ']
    Err_E3 --> End_Fail((Dừng))

    %% Kiểm tra dữ liệu rỗng (E1)
    Check_SQL_Err -- Không --> Check_Data{Có dữ liệu\ntrong kỳ không?}
    Check_Data -- Không (E1) --> Err_E1[UI: Thông báo 'Không có dữ liệu thống kê']
    Err_E1 --> End_Fail

    %% Ánh xạ và Xử lý logic (E2)
    Check_Data -- Có --> Map_DTO[Ánh xạ dữ liệu thô vào các DTO:\nDoanhThuDTO, TopSanPhamDTO...]
    
    Map_DTO --> Calc_Profit[Tính Lợi nhuận\n(LoiNhuanTheoKyDTO)]
    Calc_Profit --> Check_Null{Doanh thu hoặc\nTiền vốn là NULL?}
    
    Check_Null -- Có (E2) --> Handle_Null[Setter tự động gán giá trị\nthành BigDecimal.ZERO]
    Handle_Null --> Subtract_Math[Thực hiện phép trừ:\nLợi nhuận = Doanh thu - Tiền vốn]
    Check_Null -- Không --> Subtract_Math
    
    %% Hiển thị và Xuất báo cáo
    Subtract_Math --> Render_UI[UI: Vẽ Biểu đồ (Chart)\nvà Bảng dữ liệu (Table)]
    Render_UI --> Opt_Export{Quản lý có muốn\nXuất báo cáo?}
    
    Opt_Export -- Có --> Export_File[Kết xuất ra file Excel / PDF]
    Export_File --> End_Success((Kết thúc))
    Opt_Export -- Không --> End_Success

    %% Phân loại style
    classDef startEnd fill:#22c55e,stroke:#14532d,stroke-width:2px,color:#fff
    classDef uiAction fill:#bfdbfe,stroke:#2563eb,stroke-width:2px,color:#000
    classDef logicCheck fill:#fef08a,stroke:#ca8a04,stroke-width:2px,color:#000
    classDef errorState fill:#fca5a5,stroke:#dc2626,stroke-width:2px,color:#000
    classDef sysAction fill:#e9d5ff,stroke:#9333ea,stroke-width:2px,color:#000

    class Start,End_Success,End_Fail startEnd
    class Open_UI,Choose_Criteria,Render_UI,Opt_Export,Export_File,Err_E1,Err_E3 uiAction
    class Check_SQL_Err,Check_Data,Check_Null logicCheck
    class Send_Req,Call_DAO,Exec_Query,Map_DTO,Calc_Profit,Handle_Null,Subtract_Math sysAction


```