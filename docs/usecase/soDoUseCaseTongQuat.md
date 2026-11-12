# Sơ đồ Use Case Tổng quát (General Use Case Diagram)
**Dự án:** Phần mềm Quản lý Cửa hàng Giày (KYPM)

Dưới đây là mã nguồn `Mermaid` biểu diễn Sơ đồ Use Case tổng quát của hệ thống. Bạn có thể xem nó trực quan trên các trình duyệt Markdown hỗ trợ Mermaid (như GitHub, GitLab, Notion).

```mermaid
flowchart LR
    %% Định nghĩa các Tác nhân (Actors)
    NV(["👤 Nhân viên cửa hàng"])
    QL(["👤 Quản lý / Chủ cửa hàng"])
    
    %% Kế thừa tác nhân: Quản lý có mọi quyền của Nhân viên
    QL -.->|"Kế thừa quyền (Inherits)"| NV

    %% Hệ thống ranh giới (System Boundary)
    subgraph KYPM ["Hệ thống Quản lý Cửa hàng Giày (KYPM)"]
        direction TB
        
        %% Nhóm Hệ thống cốt lõi
        UC01(["UC-01: Đăng nhập"])
        UC02(["UC-02: Phân quyền & Điều hướng"])
        
        %% Nhóm Giao dịch & Khách hàng
        UC03(["UC-03: Quản lý Bán hàng"])
        UC04(["UC-04: Quản lý Khách hàng"])
        UC05(["UC-05: Quản lý Đổi trả"])
        UC06(["UC-06: Quản lý Bảo hành"])
        
        %% Nhóm Kho & Báo cáo (Dành cho Quản lý)
        UC07(["UC-07: Quản lý Sản phẩm (Danh mục)"])
        UC08(["UC-08: Quản lý Nhập hàng & Trả NCC"])
        UC09(["UC-09: Thống kê & Báo cáo"])
        
        %% Các quan hệ <<include>> nội bộ hệ thống
        UC03 -.->|"<< include >>"| UC02
        UC04 -.->|"<< include >>"| UC02
        UC07 -.->|"<< include >>"| UC02
    end

    %% Gán mối quan hệ Tác nhân - Use Case
    NV ===> UC01
    NV ---> UC03
    NV ---> UC04
    NV ---> UC05
    NV ---> UC06

    QL ===> UC01
    QL ---> UC07
    QL ---> UC08
    QL ---> UC09
    
    %% Style cho đẹp
    classDef actorStyle fill:#fef08a,stroke:#ca8a04,stroke-width:2px,color:#000
    classDef ucStyle fill:#bae6fd,stroke:#0284c7,stroke-width:2px,color:#000
    classDef systemStyle fill:#f8fafc,stroke:#94a3b8,stroke-width:2px,stroke-dasharray: 5 5
    
    class NV,QL actorStyle
    class UC01,UC02,UC03,UC04,UC05,UC06,UC07,UC08,UC09 ucStyle
    class KYPM systemStyle
```