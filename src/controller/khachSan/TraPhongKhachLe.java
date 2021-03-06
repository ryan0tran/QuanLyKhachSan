package controller.khachSan;

import dao.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.*;
import util.AlertGenerator;
import util.ExceptionHandler;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TraPhongKhachLe {

    @FXML
    private TableColumn<ChiTietDichVu, String> donGiaDvCol;

    @FXML
    private TableColumn<TienPhong, Integer> soNgayPCol;

    @FXML
    private Button luuBtn;

    @FXML
    private TableView<TienPhong> tienPhongTable;

    @FXML
    private Label maDPLabel;

    @FXML
    private Label gioVaoLabel;

    @FXML
    private Label thanhToanLabel;

    @FXML
    private Button inBtn;

    @FXML
    private TableColumn<ChiTietDichVu, String> donViDvCol;

    @FXML
    private Button huyBtn;

    @FXML
    private TextField datCocField;

    @FXML
    private Button kiemDoBtn;

    @FXML
    private TableColumn<BoiThuong, String> tenDoBtCol;

    @FXML
    private TableColumn<BoiThuong, Integer> sttBtCol;

    @FXML
    private TableColumn<ChiTietDichVu, Integer> soLuongDvCol;

    @FXML
    private Label khachHangLabel;

    @FXML
    private TableColumn<ChiTietDichVu, String> thanhTienDvCol;

    @FXML
    private TableColumn<TienPhong, Integer> sttPCol;

    @FXML
    private TableColumn<TienPhong, String> tenPCol;

    @FXML
    private TableColumn<TienPhong, String> thanhTienPCol;

    @FXML
    private TableColumn<BoiThuong, String> donGiaBtCol;

    @FXML
    private TableColumn<TienPhong, String> donGiaPCol;

    @FXML
    private TableView<BoiThuong> boiThuongTable;

    @FXML
    private Label thanhTienTongLabel;

    @FXML
    private Label tienCocTongLabel;

    @FXML
    private TableColumn<ChiTietDichVu, String> tenDvCol;

    @FXML
    private Label gioRaLabel;

    @FXML
    private TableView<ChiTietDichVu> dichVuTable;

    @FXML
    private TableColumn<ChiTietDichVu, Integer> sttDvCol;

    @FXML
    private TableColumn<BoiThuong, Integer> soLuongBtCol;

    @FXML
    private TableColumn<BoiThuong, String> trangThaiBtCol;

    @FXML
    private TableColumn<BoiThuong, String> thanhTienBtCol;

    private ObservableList<BoiThuong> dsBoiThuong;
    private ObservableList<TienPhong> dsTienPhong;
    private ObservableList<ChiTietDichVu> dsChiTietDichVu;
    private ChiTietDatPhong chiTietDatPhong;
    private ObservableList<ChiTietPhong> dsDo;

    private long thanhTien;
    private long thanhToan;
    private long tienCoc;

    ArrayList<HoaDon> dsCtHoaDon = new ArrayList<>();

    private Timestamp now = new Timestamp(System.currentTimeMillis());

    public void init(Phong phong) {
        chiTietDatPhong = ChiTietDatPhongDAO.getInstance().getByPhong(phong);
        DatPhong datPhong = chiTietDatPhong.getDatPhong();
        dsChiTietDichVu = ChiTietDichVuDAO.getInstance().getAll(chiTietDatPhong);

        datPhong.setNgayCheckoutTt(now);
        dsTienPhong = FXCollections.observableArrayList();
        dsTienPhong.addAll(chiTietDatPhong.getDsGia());

        if (datPhong.isKhachDoan()) {
            AlertGenerator.error("Phòng đặt theo đoàn. Vui lòng checkout bằng nút Checkout khách đoàn.");
            huyBtn.fire();
        }
        datPhong.setNgayCheckoutTt(now);
//        soNgay = (int) Math.round((now.getTime() - datPhong.getNgayCheckinTt().getTime()) / 86400000.0);

        // FIELDS
        datCocField.textProperty().addListener((observableValue, s, t1) -> {
            datCocField.setText(t1.replaceAll("[^\\d]", ""));
            tinhTien();
        });

        // INIT TABLE
        sttBtCol.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);
                if (isEmpty() || index < 0)
                    setText(null);
                else
                    setText(Integer.toString(index + 1));
            }
        });
        tenDoBtCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getChiTietPhong().getTenDo()));
        trangThaiBtCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getTrangThaiString()));
        soLuongBtCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getSoLuong()));
        donGiaBtCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%,3d", p.getValue().getChiTietPhong().getGiaTien())));
        thanhTienBtCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%,3d", p.getValue().getBoiThuong())));

        sttPCol.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);
                if (isEmpty() || index < 0)
                    setText(null);
                else
                    setText(Integer.toString(index + 1));
            }
        });
        tenPCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getTen()));
        donGiaPCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%,3d", p.getValue().getDonGia())));
        soNgayPCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getSoNgay()));
        thanhTienPCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%,3d", p.getValue().getThanhTien())));

        sttDvCol.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);
                if (isEmpty() || index < 0)
                    setText(null);
                else
                    setText(Integer.toString(index + 1));
            }
        });
        tenDvCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getDichVu().getTenDv()));
        donGiaDvCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%,3d", p.getValue().getDichVu().getGiaDv())));
        soLuongDvCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getSoLuong()));
        donViDvCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getDichVu().getDonVi()));
        thanhTienDvCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%,3d", p.getValue().getThanhTien())));

        tienPhongTable.setItems(dsTienPhong);
        dichVuTable.setItems(dsChiTietDichVu);

        // Fields
        khachHangLabel.setText(chiTietDatPhong.getDatPhong().getKhachHang().getTenKhach());
        gioVaoLabel.setText(datPhong.getNgayCheckinTt().toString());
        gioRaLabel.setText(datPhong.getNgayCheckoutTt().toString());
        maDPLabel.setText(String.format("%06d", chiTietDatPhong.getDatPhong().getMaDatPhong()));
        datCocField.setText(String.format("%,3d", chiTietDatPhong.getDatPhong().getTienDatCoc()));

        kiemDoBtn.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("view/khachSan/kiemTraCheckOut.fxml"));
            try {
                Parent editRoot = loader.load();
                new JMetro(editRoot, Style.LIGHT);

                Stage stage = new Stage();
                Scene scene = new Scene(editRoot);
                stage.setTitle("Kiểm đồ");
                stage.setScene(scene);
                KiemPhong kiemPhong = loader.getController();
                kiemPhong.init(chiTietDatPhong);

                stage.showAndWait();
                dsBoiThuong = kiemPhong.getDsBoiThuong();
                dsDo = kiemPhong.getDsDo();
            } catch (IOException e) {
                ExceptionHandler.handle(e);
            }
            boiThuongTable.setItems(dsBoiThuong);
            tinhTien();
        });

        luuBtn.setOnAction(event -> {
            save();
            ((Node) (event.getSource())).getScene().getWindow().hide();
        });
        huyBtn.setOnAction(event -> ((Node) (event.getSource())).getScene().getWindow().hide());
    }

    public void print() {

    }

    public void save() {
        dsTienPhong.get(0).getPhong().setTrangThai(Phong.DANGDON);

        for (TienPhong tienPhong : dsTienPhong) {
            dsCtHoaDon.add(new HoaDon(
                    chiTietDatPhong.getDatPhong().getMaDatPhong(),
                    tienPhong.getTen(),
                    tienPhong.getDonGia(),
                    tienPhong.getSoNgay(),
                    "ngày",
                    tienPhong.getThanhTien()
            ));
        }

        for (ChiTietDichVu chiTietDichVu : dsChiTietDichVu) {
            dsCtHoaDon.add(new HoaDon(
                    chiTietDatPhong.getDatPhong().getMaDatPhong(),
                    chiTietDichVu.getDichVu().getTenDv() + new SimpleDateFormat("dd.MM.yy HH:mm").format(chiTietDichVu.getNgayDv()) + " Phòng " + chiTietDichVu.getPhong().getMaPhong(),
                    chiTietDichVu.getDichVu().getGiaDv(),
                    chiTietDichVu.getSoLuong(),
                    chiTietDichVu.getDichVu().getDonVi(),
                    chiTietDichVu.getThanhTien()
            ));
        }
        if (dsBoiThuong != null)
            for (BoiThuong boiThuong : dsBoiThuong) {
                dsCtHoaDon.add(new HoaDon(
                        chiTietDatPhong.getDatPhong().getMaDatPhong(),
                        boiThuong.getChiTietPhong().getTenDo() + " " + boiThuong.getTrangThaiString(),
                        null,
                        boiThuong.getSoLuong(),
                        boiThuong.getChiTietPhong().getDonVi(),
                        boiThuong.getBoiThuong()
                ));
            }

        if (PhongDAO.getInstance().update(chiTietDatPhong.getPhong())
                && DatPhongDAO.getInstance().update(chiTietDatPhong.getDatPhong())
                && ChiTietDatPhongDAO.getInstance().update(chiTietDatPhong)
                && ChiTietPhongDAO.getInstance().update(dsDo)
                && HoaDonDAO.getInstance().create(dsCtHoaDon)) {
            AlertGenerator.success("Trả phòng thành công");
        } else {
            AlertGenerator.error("Trả phòng thất bại");
        }

    }

    public void tinhTien() {
        thanhTien = 0;
        // Tien phong
        for (TienPhong tienPhong : dsTienPhong) {
            thanhTien += tienPhong.getThanhTien();
        }

        if (dsBoiThuong != null) {
            for (BoiThuong boiThuong : dsBoiThuong) {
                thanhTien += boiThuong.getBoiThuong();
            }
        }
        for (ChiTietDichVu chiTietDichVu : dsChiTietDichVu) {
            thanhTien += chiTietDichVu.getThanhTien();
        }

        tienCoc = Long.parseLong(datCocField.getText());
        thanhToan = thanhTien - tienCoc;

        thanhTienTongLabel.setText(String.format("%,d", thanhTien));
        tienCocTongLabel.setText(String.format("%,d", tienCoc));
        thanhToanLabel.setText(String.format("%,d", thanhToan));
    }

}
