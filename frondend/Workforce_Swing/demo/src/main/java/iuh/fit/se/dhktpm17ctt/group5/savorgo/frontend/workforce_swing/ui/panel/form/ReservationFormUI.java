package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.SystemForm;

import java.awt.Font;
import java.awt.Color;
@SystemForm(name = "Reservation", description = "Reservation is a user interface component to make reservation", tags = { "reservation" })

public class ReservationFormUI extends Form {

    private static final long serialVersionUID = 1L;
    private JTextField txtSDT;
    private JTextField txtHoTen;
    private JTextField txtHoTenPV;
    private JTextField txtTongTienSauKhuyenMai;
    private JTextField txtTongTienTruocKhuyenMai;
    private JTextField txtTienThoi;
    private JTextField txtTimKiemDD;
    private JPanel pnDanhSach;
    private JPanel pnThanhToan;
    private JSpinner spnTienKhachDua;
    private JTextField txtTimKiemKH;
    private JTextField txtTimKiemPV;
    private JTextField txtMaKH;
    private JTextField txtHoiVien;
    private JTextField txtGioiTinh;
    private JTextField txtMaPV;
    private JTextField txtThoiHan;
    private JTable tblPhong;
    private JTable tblDV;
	private JPanel pnDatVaThanhToan;
	private JLabel lblThoiHan;
	public ReservationFormUI() {
		// TODO Auto-generated constructor stub
		init();
	}
	private void init() {
        setLayout(new MigLayout("", "[65%:65%:65%,grow][][33%:33%:33%,grow]", "[109.00][370.00,grow][30px:40px:40px]"));

        JPanel pnDieuKhien = new JPanel();
        add(pnDieuKhien, "cell 0 0,grow");
        pnDieuKhien.setLayout(new MigLayout("", "[][][][grow]", "[30px:40px:40px][][]"));

        JButton btnHienDSDonDat = new JButton("Hiện danh sách Đơn đặt");
        btnHienDSDonDat.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnDieuKhien.add(btnHienDSDonDat, "cell 0 0,growx");

        JButton btnHienDSPhong = new JButton("Hiện danh sách Phòng");
        btnHienDSPhong.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnDieuKhien.add(btnHienDSPhong, "cell 1 0,growx");

        JButton btnHienDSDV = new JButton("Hiện danh sách Dịch vụ");
        btnHienDSDV.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnDieuKhien.add(btnHienDSDV, "flowx,cell 2 0,growx");

        JButton btnLamMoiDanhSach = new JButton("Làm mới");
        btnLamMoiDanhSach.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnDieuKhien.add(btnLamMoiDanhSach, "cell 3 0,growx");

        txtTimKiemDD = new JTextField();
        txtTimKiemDD.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnDieuKhien.add(txtTimKiemDD, "cell 0 1 4 1,growx");
        txtTimKiemDD.setEnabled(false);

        JLabel lblDanhSach = new JLabel("Hãy chọn nút ở trên để hiện danh sách bất kỳ!");
        lblDanhSach.setFont(new Font("Times New Roman", Font.BOLD, 20));
        pnDieuKhien.add(lblDanhSach, "cell 0 2 4 1,growx");

        pnDanhSach = new JPanel();
        add(pnDanhSach, "cell 0 1,grow");
        pnDanhSach.setLayout(new BorderLayout(0, 0));

        pnThanhToan = new JPanel();
        add(pnThanhToan, "cell 2 0 1 2,grow");
        pnThanhToan.setLayout(new MigLayout("", "[100px:100px][80px:80px:80px][grow]",
                "[][][:30px:40px,grow][30px:40px:40px,fill][30px:40px:40px][][:30px:40px,grow][30px:40px:40px][:30px:40px,grow][30px:100px,grow][:30px:40px,grow][30px:100px,grow][30px:40px:40px][30px:40px:40px][grow][30px:40px:40px][grow]"));

        JLabel lblKhachHang = new JLabel("Thông tin Khách hàng");
        lblKhachHang.setFont(new Font("Times New Roman", Font.BOLD, 20));
        pnThanhToan.add(lblKhachHang, "cell 0 1 3 1,growx");

        txtTimKiemKH = new JTextField();
        txtTimKiemKH.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnThanhToan.add(txtTimKiemKH, "cell 0 2 3 1,growx");
        txtTimKiemKH.setColumns(10);

        txtMaKH = new JTextField();
        txtMaKH.setForeground(Color.WHITE);
        txtMaKH.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtMaKH.setColumns(10);
        pnThanhToan.add(txtMaKH, "cell 0 3,growx");

        txtGioiTinh = new JTextField();
        txtGioiTinh.setForeground(Color.WHITE);
        txtGioiTinh.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtGioiTinh.setColumns(10);
        pnThanhToan.add(txtGioiTinh, "cell 1 3,grow");

        txtSDT = new JTextField();
        txtSDT.setForeground(Color.WHITE);
        txtSDT.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnThanhToan.add(txtSDT, "cell 2 3,grow");
        txtSDT.setColumns(10);

        txtHoiVien = new JTextField();
        txtHoiVien.setForeground(Color.WHITE);
        txtHoiVien.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtHoiVien.setColumns(10);
        pnThanhToan.add(txtHoiVien, "cell 0 4 2 1,grow");

        txtHoTen = new JTextField();
        txtHoTen.setForeground(Color.WHITE);
        txtHoTen.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtHoTen.setColumns(10);
        pnThanhToan.add(txtHoTen, "flowx,cell 2 4,grow");

        JLabel lblPhucVu = new JLabel("Thông tin Phục vụ");
        lblPhucVu.setFont(new Font("Times New Roman", Font.BOLD, 20));
        pnThanhToan.add(lblPhucVu, "cell 0 5 3 1");

        txtTimKiemPV = new JTextField();
        txtTimKiemPV.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnThanhToan.add(txtTimKiemPV, "cell 0 6 3 1,growx");
        txtTimKiemPV.setColumns(10);

        txtMaPV = new JTextField();
        txtMaPV.setForeground(Color.WHITE);
        txtMaPV.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtMaPV.setColumns(10);
        pnThanhToan.add(txtMaPV, "cell 0 7,grow");

        txtHoTenPV = new JTextField();
        txtHoTenPV.setForeground(Color.WHITE);
        txtHoTenPV.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtHoTenPV.setColumns(10);
        pnThanhToan.add(txtHoTenPV, "cell 1 7 2 1,grow");

        JLabel lblDSPhong = new JLabel("Danh sách chọn Phòng");
        lblDSPhong.setFont(new Font("Times New Roman", Font.BOLD, 20));
        pnThanhToan.add(lblDSPhong, "cell 0 8 2 1");

        JScrollPane spDanhSachPhong = new JScrollPane();
        tblPhong = new JTable();
        tblPhong.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Mã phòng", "Tên phòng", "Giá phòng",
                "Bắt đầu", "Kết thúc", "Số giờ", "Thành tiền", "Chức năng" }));
        spDanhSachPhong.setViewportView(tblPhong);
        pnThanhToan.add(spDanhSachPhong, "cell 0 9 3 1,grow");

        JLabel lblDSDV = new JLabel("Danh sách chọn Dịch vụ");
        lblDSDV.setFont(new Font("Times New Roman", Font.BOLD, 20));
        pnThanhToan.add(lblDSDV, "cell 0 10");

        JScrollPane spDanhSachDichVu = new JScrollPane();
        tblDV = new JTable();
        tblDV.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Mã dịch vụ", "Tên dịch vụ",
                "Giá dịch vụ", "Số lượng chọn", "Thành tiền", "Chức năng" }));
        spDanhSachDichVu.setViewportView(tblDV);
        pnThanhToan.add(spDanhSachDichVu, "cell 0 11 3 1,grow");

        txtTongTienTruocKhuyenMai = new JTextField();
        txtTongTienTruocKhuyenMai.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtTongTienTruocKhuyenMai.setColumns(10);
        pnThanhToan.add(txtTongTienTruocKhuyenMai, "cell 0 12 2 1,grow");

        txtTongTienSauKhuyenMai = new JTextField();
        txtTongTienSauKhuyenMai.setHorizontalAlignment(JTextField.TRAILING);
        txtTongTienSauKhuyenMai.setForeground(Color.RED);
        txtTongTienSauKhuyenMai.setFont(new Font("Times New Roman", Font.BOLD, 15));
        txtTongTienSauKhuyenMai.setColumns(10);
        pnThanhToan.add(txtTongTienSauKhuyenMai, "cell 2 12,grow");

        JButton btnDatHayThanhToan = new JButton("Đặt");
        pnThanhToan.add(btnDatHayThanhToan, "cell 0 13 3 1,grow");

        pnDatVaThanhToan = new JPanel();
        pnThanhToan.add(pnDatVaThanhToan, "cell 0 14 3 1,grow");
        pnDatVaThanhToan.setLayout(new MigLayout("", "[106px][grow]", "[30px:40px:40px][30px:40px:40px][30px:40px:40px]"));

        JLabel lblTienKhachDua = new JLabel("Số tiền khách đưa");
        lblTienKhachDua.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnDatVaThanhToan.add(lblTienKhachDua, "cell 0 0,alignx trailing");

        spnTienKhachDua = new JSpinner();
        spnTienKhachDua.setForeground(Color.WHITE);
        spnTienKhachDua.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnDatVaThanhToan.add(spnTienKhachDua, "cell 1 0,grow");

        JLabel lblTienThoi = new JLabel("Tiền thối");
        lblTienThoi.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnDatVaThanhToan.add(lblTienThoi, "cell 0 1,alignx trailing");

        txtTienThoi = new JTextField();
        txtTienThoi.setForeground(Color.WHITE);
        txtTienThoi.setHorizontalAlignment(JTextField.TRAILING);
        txtTienThoi.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtTienThoi.setColumns(10);
        pnDatVaThanhToan.add(txtTienThoi, "cell 1 1,grow");

        lblThoiHan = new JLabel("Thời hạn");
        lblThoiHan.setHorizontalAlignment(JLabel.TRAILING);
        lblThoiHan.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnDatVaThanhToan.add(lblThoiHan, "cell 0 2,alignx trailing");

        txtThoiHan = new JTextField();
        txtThoiHan.setForeground(Color.WHITE);
        txtThoiHan.setText("Thời hạn");
        txtThoiHan.setHorizontalAlignment(JTextField.TRAILING);
        txtThoiHan.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtThoiHan.setColumns(10);
        pnDatVaThanhToan.add(txtThoiHan, "cell 1 2,grow");

        JButton btnLamMoiThongTin = new JButton("Làm mới");
        btnLamMoiThongTin.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnThanhToan.add(btnLamMoiThongTin, "cell 0 15 2 1,grow");

        JButton btnThanhToan = new JButton("Thanh toán");
        btnThanhToan.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        pnThanhToan.add(btnThanhToan, "cell 2 15,grow");

        JLabel txtCanhBao = new JLabel("Thông tin lọc bên trên chỉ là kết quả tìm kiếm đầu tiên, thông tin lọc càng chính xác thì kết quả càng chuẩn");
        txtCanhBao.setForeground(Color.ORANGE);
        txtCanhBao.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        add(txtCanhBao, "cell 0 2 3 1,alignx right");
    }
}