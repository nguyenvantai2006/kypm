from zipfile import ZipFile, ZIP_DEFLATED
from xml.sax.saxutils import escape
from pathlib import Path

root = Path(__file__).resolve().parent
out = root / 'TaiLieu_KiemThu_PhanMem.docx'

paragraphs = [
    ('Title', 'TÀI LIỆU YÊU CẦU VÀ KIỂM THỬ PHẦN MỀM HỆ THỐNG QUẢN LÝ CỬA HÀNG GIÀY'),
    ('Heading1', '1. UML'),
    ('Heading2', '1.1 Sơ đồ Use Case'),
    ('Text', 'Actor: Quản trị viên, Nhân viên, Khách hàng; Hệ thống: phần mềm quản lý cửa hàng giày.'),
    ('Text', 'Use Case chính gồm:'),
    ('Bullet', '- Đăng nhập hệ thống'),
    ('Bullet', '- Quản lý sản phẩm'),
    ('Bullet', '- Nhập hàng từ nhà cung cấp'),
    ('Bullet', '- Bán hàng và tạo hóa đơn'),
    ('Bullet', '- Quản lý khách hàng'),
    ('Bullet', '- Quản lý kho và tồn kho'),
    ('Bullet', '- Quản lý voucher và khuyến mãi'),
    ('Bullet', '- Đổi trả, bảo hành và thống kê doanh thu'),
    ('Text', 'UML mô tả dạng văn bản:'),
    ('Text', '[Quản trị viên] -- Đăng nhập --> [Hệ thống]'),
    ('Text', '[Nhân viên] -- Quản lý sản phẩm --> [Hệ thống] --> [Kho]'),
    ('Text', '[Nhân viên] -- Tạo hóa đơn --> [Hệ thống] --> [Hóa đơn]'),
    ('Text', '[Khách hàng] -- Mua hàng --> [Hệ thống] --> [Thanh toán]'),
    ('Heading2', '1.2 Quy trình hoạt động'),
    ('Text', 'Start -> Đăng nhập -> Kiểm tra Quyền -> Nếu sai: báo lỗi -> End; Nếu đúng: mở màn hình chính -> chọn chức năng -> thực hiện nghiệp vụ -> lưu dữ liệu -> kết thúc.'),
    ('Heading2', '1.3 Sơ đồ tuần tự (Sequence diagram mô tả)'),
    ('Text', 'Người dùng -> Hệ thống: Yêu cầu đăng nhập'),
    ('Text', 'Hệ thống -> CSDL: Kiểm tra tài khoản và quyền'),
    ('Text', 'CSDL --> Hệ thống: Trả về thông tin hợp lệ'),
    ('Text', 'Hệ thống --> Người dùng: Mở màn hình chức năng'),
    ('Text', 'Nhân viên -> Hệ thống: Thêm/sửa/xóa sản phẩm hoặc tạo hóa đơn'),
    ('Text', 'Hệ thống -> CSDL: Lưu dữ liệu'),
    ('Text', 'CSDL --> Hệ thống: Xác nhận thành công'),
    ('Text', 'Hệ thống --> Người dùng: Thông báo kết quả'),

    ('Heading1', '2. Quy trình nghiệp vụ'),
    ('Heading2', '2.1 Quy trình đăng nhập'),
    ('Number', '1. Người dùng nhập tên đăng nhập và mật khẩu.'),
    ('Number', '2. Hệ thống kiểm tra thông tin tài khoản trong CSDL.'),
    ('Number', '3. Nếu hợp lệ, hệ thống xác thực quyền và mở màn hình tương ứng.'),
    ('Number', '4. Nếu không hợp lệ, hiển thị thông báo lỗi và yêu cầu nhập lại.'),
    ('Heading2', '2.2 Quy trình quản lý sản phẩm'),
    ('Number', '1. Admin hoặc nhân viên mở chức năng Sản phẩm.'),
    ('Number', '2. Nhập thông tin sản phẩm: tên, giá nhập, % lợi nhuận, giá bán, giá khuyến mãi, tồn kho.'),
    ('Number', '3. Hệ thống tự tính giá bán theo công thức: giá bán = giá nhập x (1 + % lợi nhuận / 100).'),
    ('Number', '4. Nếu giá nhập thay đổi thì giá bán mới được cập nhật; nếu giá nhập không thay đổi thì giữ nguyên giá bán hiện tại.'),
    ('Number', '5. Khi có khuyến mãi, hệ thống hiển thị giá cũ gạch ngang kèm giá mới cho người mua.'),
    ('Heading2', '2.3 Quy trình nhập hàng'),
    ('Number', '1. Chọn nhà cung cấp và phiếu nhập.'),
    ('Number', '2. Chọn sản phẩm, số lượng và giá nhập.'),
    ('Number', '3. Hệ thống kiểm tra dữ liệu và lưu phiếu nhập.'),
    ('Number', '4. Cập nhật số lượng tồn kho sau khi nhập hàng thành công.'),
    ('Heading2', '2.4 Quy trình bán hàng'),
    ('Number', '1. Nhân viên chọn khách hàng và sản phẩm.'),
    ('Number', '2. Chọn số lượng và áp dụng voucher hoặc khuyến mãi nếu có.'),
    ('Number', '3. Hệ thống tính tổng tiền và xác nhận thanh toán.'),
    ('Number', '4. Tạo hóa đơn, lưu lịch sử giao dịch và giảm tồn kho tương ứng.'),
    ('Heading2', '2.5 Quy trình đổi trả và bảo hành'),
    ('Number', '1. Khách hàng yêu cầu đổi trả hoặc bảo hành.'),
    ('Number', '2. Hệ thống kiểm tra thời hạn, điều kiện và trạng thái hàng hóa.'),
    ('Number', '3. Xác nhận hoặc từ chối yêu cầu theo chính sách cửa hàng.'),
    ('Number', '4. Cập nhật hồ sơ và trạng thái xử lý.'),
    ('Heading2', '2.6 Quy trình thống kê báo cáo'),
    ('Number', '1. Quản lý chọn khoảng thời gian thống kê.'),
    ('Number', '2. Hệ thống tổng hợp doanh thu, số lượng bán, top sản phẩm, khách hàng thân thiết.'),
    ('Number', '3. Hiển thị báo cáo và biểu đồ để phục vụ quyết định quản lý.'),

    ('Heading1', '3. Yêu cầu (mô tả) chức năng theo quy chuẩn kiểm thử phần mềm'),
    ('Heading2', '3.1 Mục tiêu kiểm thử'),
    ('Text', 'Mục tiêu của kiểm thử là xác nhận hệ thống hoạt động đúng theo nghiệp vụ, bảo đảm độ chính xác của dữ liệu, tính ổn định của giao diện, tính nhất quán khi thao tác và khả năng đáp ứng yêu cầu của người dùng.'),
    ('Heading2', '3.2 Yêu cầu chức năng chung'),
    ('Text', 'Hệ thống phải đảm bảo: đăng nhập đúng tài khoản, phân quyền rõ ràng, thao tác lưu dữ liệu đúng, hiển thị thông tin theo vai trò, xử lý lỗi hợp lý và trả về thông báo rõ ràng.'),
    ('Heading2', '3.3 Mô tả yêu cầu chức năng chi tiết'),
    ('Number', 'Mã chức năng FC-01: Đăng nhập hệ thống. Đầu vào: tên đăng nhập, mật khẩu. Đầu ra: vào chức năng chính hoặc thông báo lỗi. Tiêu chí chấp nhận: tài khoản hợp lệ được phép truy cập; dữ liệu sai sẽ báo lỗi rõ ràng.'),
    ('Number', 'Mã chức năng FC-02: Quản lý tài khoản và quyền. Đầu vào: thông tin nhân viên/quyền truy cập. Đầu ra: cập nhật quyền thành công. Tiêu chí chấp nhận: tài khoản không có quyền không thể truy cập chức năng bị cấm.'),
    ('Number', 'Mã chức năng FC-03: Quản lý sản phẩm. Đầu vào: mã sản phẩm, tên, loại, giá nhập, % lợi nhuận, giá bán, khuyến mãi, tồn kho. Đầu ra: danh sách sản phẩm được cập nhật. Tiêu chí chấp nhận: không lưu dữ liệu rỗng hoặc không hợp lệ.'),
    ('Number', 'Mã chức năng FC-04: Tính giá bán tự động. Đầu vào: giá nhập và % lợi nhuận. Đầu ra: giá bán được tính tự động. Tiêu chí chấp nhận: nếu giá nhập thay đổi thì giá bán mới được cập nhật; nếu không thay đổi thì giữ nguyên.'),
    ('Number', 'Mã chức năng FC-05: Khuyến mãi và hiển thị giá cho khách hàng. Đầu vào: giá khuyến mãi. Đầu ra: hiển thị giá cũ gạch ngang và giá mới. Tiêu chí chấp nhận: % lợi nhuận chỉ hiển thị ở phần quản lý nội bộ, không hiển thị cho người mua.'),
    ('Number', 'Mã chức năng FC-06: Nhập hàng từ nhà cung cấp. Đầu vào: mã nhà cung cấp, sản phẩm, số lượng, giá nhập. Đầu ra: phiếu nhập và tồn kho mới. Tiêu chí chấp nhận: nếu thiếu dữ liệu bắt buộc, hệ thống báo lỗi và không lưu.'),
    ('Number', 'Mã chức năng FC-07: Bán hàng và hóa đơn. Đầu vào: sản phẩm, số lượng, khách hàng, voucher. Đầu ra: hóa đơn thanh toán. Tiêu chí chấp nhận: tổng tiền tính đúng, tồn kho giảm chính xác sau khi bán.'),
    ('Number', 'Mã chức năng FC-08: Quản lý khách hàng. Đầu vào: thông tin khách hàng. Đầu ra: hồ sơ khách hàng lưu trữ đầy đủ. Tiêu chí chấp nhận: dữ liệu khách hàng được cập nhật chính xác và không trùng lặp không hợp lệ.'),
    ('Number', 'Mã chức năng FC-09: Đổi trả và bảo hành. Đầu vào: mã hóa đơn, lý do, trạng thái sản phẩm. Đầu ra: trạng thái xử lý. Tiêu chí chấp nhận: các trường hợp không hợp lệ bị từ chối kịp thời.'),
    ('Number', 'Mã chức năng FC-10: Thống kê báo cáo. Đầu vào: khoảng thời gian thống kê. Đầu ra: báo cáo doanh thu, sản phẩm và khách hàng. Tiêu chí chấp nhận: dữ liệu báo cáo phải chính xác với dữ liệu thực tế trong hệ thống.'),
    ('Heading2', '3.4 Mức độ kiểm thử đề xuất'),
    ('Text', 'Kiểm thử đơn vị: kiểm tra tính toán giá, khuyến mãi, tồn kho và logic form.'),
    ('Text', 'Kiểm thử tích hợp: kiểm tra hoạt động giữa giao diện, tầng BUS, DAO và CSDL.'),
    ('Text', 'Kiểm thử chức năng: kiểm tra đúng từng nghiệp vụ theo yêu cầu.'),
    ('Text', 'Kiểm thử chấp nhận: xác nhận hệ thống đáp ứng yêu cầu kinh doanh của cửa hàng.'),
    ('Heading2', '3.5 Điều kiện chấp nhận cuối cùng'),
    ('Text', 'Hệ thống được chấp nhận khi: tất cả chức năng cốt lõi hoạt động đúng, dữ liệu được lưu nhất quán, không có lỗi nghiêm trọng, và quyền hiển thị thông tin theo vai trò được thực hiện đúng.')
]


def build_paragraph(kind, text):
    text_xml = escape(text)
    if kind == 'Title':
        return (
            '<w:p><w:pPr><w:pStyle w:val="Title"/></w:pPr>'
            f'<w:r><w:t>{text_xml}</w:t></w:r></w:p>'
        )
    if kind == 'Heading1':
        return (
        '<w:p><w:pPr><w:pStyle w:val="Heading1"/></w:pPr>'
            f'<w:r><w:t>{text_xml}</w:t></w:r></w:p>'
        )
    if kind == 'Heading2':
        return (
            '<w:p><w:pPr><w:pStyle w:val="Heading2"/></w:pPr>'
            f'<w:r><w:t>{text_xml}</w:t></w:r></w:p>'
        )
    if kind == 'Bullet':
        return (
            '<w:p><w:pPr><w:pStyle w:val="ListParagraph"/></w:pPr>'
            f'<w:r><w:t>{text_xml}</w:t></w:r></w:p>'
        )
    if kind == 'Number':
        return (
            '<w:p><w:pPr><w:pStyle w:val="ListParagraph"/></w:pPr>'
            f'<w:r><w:t>{text_xml}</w:t></w:r></w:p>'
        )
    return f'<w:p><w:r><w:t>{text_xml}</w:t></w:r></w:p>'

content_xml = ''.join(build_paragraph(kind, text) for kind, text in paragraphs)

styles_xml = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:style w:type="paragraph" w:default="1" w:styleId="Normal"><w:name w:val="Normal"/></w:style>
  <w:style w:type="paragraph" w:styleId="Title"><w:name w:val="Title"/><w:basedOn w:val="Normal"/><w:next w:val="Normal"/><w:qFormat/><w:rPr><w:b/><w:sz w:val="36"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="Heading1"><w:name w:val="heading 1"/><w:basedOn w:val="Normal"/><w:next w:val="Normal"/><w:uiPriority w:val="1"/><w:qFormat/><w:rPr><w:b/><w:sz w:val="36"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="Heading2"><w:name w:val="heading 2"/><w:basedOn w:val="Normal"/><w:next w:val="Normal"/><w:uiPriority w:val="2"/><w:qFormat/><w:rPr><w:b/><w:sz w:val="26"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="ListParagraph"><w:name w:val="List Paragraph"/><w:basedOn w:val="Normal"/><w:qFormat/><w:rPr><w:sz w:val="22"/></w:rPr></w:style>
</w:styles>
'''

content_types = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
  <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
  <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
</Types>
'''

rels_xml = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
</Relationships>
'''

app_xml = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties" xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
  <Application>Microsoft Office Word</Application>
</Properties>
'''

core_xml = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dcmitype="http://purl.org/dc/dcmitype/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <dc:title>Tài liệu kiểm thử phần mềm</dc:title>
  <dc:creator>GitHub Copilot</dc:creator>
  <cp:lastModifiedBy>GitHub Copilot</cp:lastModifiedBy>
  <dcterms:created xsi:type="dcterms:W3CDTF">2026-09-22T00:00:00Z</dcterms:created>
  <dcterms:modified xsi:type="dcterms:W3CDTF">2026-09-22T00:00:00Z</dcterms:modified>
</cp:coreProperties>
'''

document_xml = f'''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <w:body>
    {content_xml}
    <w:sectPr>
      <w:pgSz w:w="12240" w:h="15840"/>
      <w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440" w:header="720" w:footer="720" w:gutter="0"/>
    </w:sectPr>
  </w:body>
</w:document>
'''

with ZipFile(out, 'w', ZIP_DEFLATED) as z:
    z.writestr('[Content_Types].xml', content_types)
    z.writestr('_rels/.rels', rels_xml)
    z.writestr('docProps/core.xml', core_xml)
    z.writestr('docProps/app.xml', app_xml)
    z.writestr('word/document.xml', document_xml)
    z.writestr('word/styles.xml', styles_xml)

print(f'Created file: {out}')
print(f'File exists: {out.exists()}')
print(f'File size: {out.stat().st_size} bytes')
