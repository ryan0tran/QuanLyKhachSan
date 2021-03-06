/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.basic;

import controller.dichVu.QLDichVu;
import controller.hoaDon.QLHoaDon;
import controller.khachHang.QLKhachHang;
import controller.nhanVien.TimNhanVien;
import controller.phong.QLGiaDacBiet;
import controller.phong.QLLoaiPhong;
import controller.phong.QLPhong;
import controller.thongKe.ThongKeChung;
import controller.vatTu.QLVatTu;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.NhanVien;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.AlertGenerator;
import util.ExceptionHandler;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

public class KhungUngDung {

    @FXML
    private BorderPane window;

    @FXML
    private TreeView sideMenu;

    @FXML
    private MenuItem exitMenu;

    @FXML
    private MenuItem logOutMenu;

    @FXML
    public MenuItem editMenu;

    @FXML
    public MenuItem addMenu;

    @FXML
    public MenuItem deleteMenu;

    @FXML
    public MenuItem exportMenu;

    @FXML
    private MenuItem aboutMenu;

    @FXML
    public MenuItem importMenu;

    @FXML
    public Menu menuForm;

    public NhanVien currentUser;

    public void init(NhanVien user) {

        this.currentUser = user;

        TreeItem rootItem = new TreeItem("Menu");

        TreeItem qlPhongItem = new TreeItem("Quản lý phòng");
        qlPhongItem.getChildren().add(new TreeItem("Thông tin phòng"));
        qlPhongItem.getChildren().add(new TreeItem("Quản lý loại phòng"));
        qlPhongItem.getChildren().add(new TreeItem("Quản lý giá phòng"));
        rootItem.getChildren().add(qlPhongItem);
        qlPhongItem.setExpanded(true);


        TreeItem qlDichVuItem = new TreeItem("Quản lý dịch vụ");
        rootItem.getChildren().add(qlDichVuItem);
        qlDichVuItem.setExpanded(true);

        TreeItem qlKhachHangItem = new TreeItem("Quản lý khách hàng");
        rootItem.getChildren().add(qlKhachHangItem);
        qlKhachHangItem.setExpanded(true);

        TreeItem qlHoaDonItem = new TreeItem("Quản lý hoá đơn");
        rootItem.getChildren().add(qlHoaDonItem);
        qlHoaDonItem.setExpanded(true);

        TreeItem qlVatTuItem = new TreeItem("Quản lý vật tư");
        rootItem.getChildren().add(qlVatTuItem);
        qlVatTuItem.setExpanded(true);

        TreeItem thongKeItem = new TreeItem("Báo cáo & Thống kê");
        rootItem.getChildren().add(thongKeItem);
        thongKeItem.setExpanded(true);

        if (currentUser.getLoaiNv() == 0) {
            TreeItem qlNhanVienItem = new TreeItem("Quản lý nhân viên");
            rootItem.getChildren().add(qlNhanVienItem);
            qlNhanVienItem.setExpanded(true);
        }

        sideMenu.setRoot(rootItem);

        sideMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            Node node = event.getPickResult().getIntersectedNode();

            if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                String option = (String) ((TreeItem)sideMenu.getSelectionModel().getSelectedItem()).getValue();
                renderMainScene(option);
            }
        });

        sideMenu.getSelectionModel().select(0);

        // TOP MENU BAR

        // FILE MENU
        exitMenu.setOnAction(event-> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Thoát chương trình");
            alert.setHeaderText("Bạn chắc chắn muốn thoát?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK)
                System.exit(0);
        });

        logOutMenu.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/basic/login.fxml"));
                Scene firstScene = new Scene(root, 1000, 600);
                firstScene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
                new JMetro(root, Style.DARK);
                Stage stage = new Stage();
                stage.setTitle("Đăng nhập - QLTV");
//                stage.getIcons().add(new Image("/resources/icon/app-icon.png"));
                stage.setScene(firstScene);
                stage.setResizable(false);
                stage.setScene(firstScene);
                window.getScene().getWindow().hide();
                stage.show();
            } catch (IOException e) {
                ExceptionHandler.handle(e);
                System.exit(0);
            }
        });

        // EDIT MENU

        editMenu.setDisable(true);
        deleteMenu.setDisable(true);
        addMenu.setDisable(true);

        exportMenu.setDisable(true);
        importMenu.setDisable(true);

        // FORM MENU

        MenuItem item1 = new MenuItem("Form Dich Vu");
        MenuItem item2 = new MenuItem("Form Khach Hang");
        MenuItem item3 = new MenuItem("Form Loai Phong");
        MenuItem item4 = new MenuItem("Form Gia Phong");
        MenuItem item5 = new MenuItem("Form Nhan Vien");
        MenuItem item6 = new MenuItem("Form Vat Tu");

        item1.setOnAction(event -> {
            String[] headers = {"Tên DV", "Giá tiền", "Đơn vị", "Ghi chú"};
                    saveForm(headers, item1.getText());
        });

        item2.setOnAction(event -> {
            String[] headers = {"Họ tên", "Giới tính", "CMND", "Điện thoại", "Email", "Địa chỉ", "Ghi chú"};
                    saveForm(headers, item2.getText());
        });

        item3.setOnAction(event -> {
            String[] headers = {"Mã loại phòng", "Tên loại phòng", "Giá tiền", "Số người", "Ghi chú"};
            saveForm(headers, item3.getText());
        });

        item4.setOnAction(event -> {
            String[] headers = {"Mã loại phòng", "Diễn gỉải", "Ngày bắt đầu", "Ngày kết thúc", "Lặp lại", "Giá tiền", "Ghi chú"} ;
                    saveForm(headers, item4.getText());
        });

        item5.setOnAction(event -> {
            String[] headers = {"Tên NV", "Loại NV", "Tên đăng nhập", "Mật khẩu", "Giới tính", "CMND", "Điện thoại", "Email", "Địa chỉ", "Ghi chú"};
            saveForm(headers, item5.getText());
        });

        item6.setOnAction(event -> {
            String[] headers = {"Mã phòng", "Tên đồ", "Số lượng", "Trạng thái", "Giá tiền", "Đơn vị tính", "Ghi chú"};
            saveForm(headers, item6.getText());
        });


        // ABOUT MENU
        aboutMenu.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Về ứng dụng");
                alert.setContentText("Nhóm N03 - CT6.\nThành viên nhóm:");

                TextArea textArea = new TextArea(
                        "Trần Trung Nghĩa\tMSSV: 20173281" +
                                "\nNguyễn Khắc Bình\tMSSV: 20165058" +
                                "\nNguyễn Mạnh Đức\tMSSV: 20173039");
                textArea.setEditable(false);
                textArea.setWrapText(true);

                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);

                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(textArea, 0, 1);

                alert.getDialogPane().setExpandableContent(expContent);
                alert.getDialogPane().setExpanded(true);
                alert.showAndWait();
        });
    }

    public void renderMainScene(String option) {
        FXMLLoader loader = new FXMLLoader();
        switch (option) {
            case "Quản lý phòng":
            case "Thông tin phòng":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/phong/qlPhong.fxml"));
                    window.setCenter(loader.load());
                    QLPhong qlPhong = loader.getController();
                    qlPhong.init(this);
                } catch (Exception e) {
                    ExceptionHandler.handle(e);
                }
                break;
            case "Quản lý loại phòng":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/phong/qlLoaiPhong.fxml"));
                    window.setCenter(loader.load());
                    QLLoaiPhong qlLoaiPhong = loader.getController();
                    qlLoaiPhong.init(this);
                } catch (Exception e) {
                    ExceptionHandler.handle(e);
                }
                break;
            case "Quản lý giá phòng":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/phong/qlGiaDacBiet.fxml"));
                    window.setCenter(loader.load());
                    QLGiaDacBiet qlGiaDacBiet = loader.getController();
                    qlGiaDacBiet.init(this);
                } catch (Exception e) {
                    ExceptionHandler.handle(e);
                }
                break;
            case "Quản lý dịch vụ":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/dichVu/qlDichVu.fxml"));
                    window.setCenter(loader.load());
                    QLDichVu qlDichVu = loader.getController();
                    qlDichVu.init(this);
                } catch (Exception e) {
                    ExceptionHandler.handle(e);
                }
                break;
            case "Quản lý khách hàng":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/khachHang/qlKhachHang.fxml"));
                    window.setCenter(loader.load());
                    QLKhachHang qlKhachHang = loader.getController();
                    qlKhachHang.init(this);
                } catch (Exception e) {
                    ExceptionHandler.handle(e);
                }
                break;
            case "Quản lý hoá đơn":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/thongKe/danhSachHoaDon.fxml"));
                    window.setCenter(loader.load());
                    QLHoaDon qlHoaDon = loader.getController();
                    qlHoaDon.init(this);
                } catch (Exception e) {
                    ExceptionHandler.handle(e);
                }
                break;
            case "Quản lý vật tư":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/khachHang/qlKhachHang.fxml"));
                    window.setCenter(loader.load());
                    QLVatTu qlVatTu = loader.getController();
                    qlVatTu.init(this);
                } catch (Exception e) {
                    ExceptionHandler.handle(e);
                }
                break;
            case "Báo cáo & Thống kê":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/thongKe/thongKeChung.fxml"));
                    window.setCenter(loader.load());
                    ThongKeChung thongKeChung = loader.getController();
                    thongKeChung.init(this);
                } catch (Exception e) {
                    ExceptionHandler.handle(e);
                }
                break;
            case "Quản lý nhân viên":
                try {
                    loader.setLocation(getClass().getClassLoader().getResource("view/nhanVien/qlNhanVien.fxml"));
                    window.setCenter(loader.load());
                    TimNhanVien timNhanVien = loader.getController();
                    timNhanVien.init(this);
                } catch (Exception e) {
                    ExceptionHandler.handle(e);
                }
                break;
        }
    }

    public void saveForm(String[] headers, String name) {

        XSSFWorkbook excelWorkBook = new XSSFWorkbook();
        XSSFSheet sheet = excelWorkBook.createSheet();

        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
        }

//             Ghi file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn vị trí lưu.");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName(name);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedFile = fileChooser.showSaveDialog(window.getScene().getWindow());

        try {
            FileOutputStream output = new FileOutputStream(selectedFile);
            excelWorkBook.write(output);
            output.close();
            Desktop.getDesktop().open(selectedFile);
        } catch (IOException e) {
            ExceptionHandler.handle(e);
        }
    }
}


