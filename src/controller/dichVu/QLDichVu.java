package controller.dichVu;

import controller.basic.KhungUngDung;
import dao.DichVuDAO;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.DichVu;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import util.ExceptionHandler;
import util.Reporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

public class QLDichVu {

    @FXML
    private TableView<DichVu> dichVuTable;

    @FXML
    private TableColumn<DichVu, String> idCol;

    @FXML
    private TableColumn<DichVu, String> tenDvCol;

    @FXML
    private TableColumn<DichVu, String> giaDvCol;

    @FXML
    private TableColumn<DichVu, String> ghiChuCol;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button editBtn;

    @FXML
    private Button addBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private ImageView refreshIcon;

    @FXML
    private TableColumn<DichVu, String> donViCol;

    private ObservableList<DichVu> data;

    public void init(KhungUngDung c) {

        idCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%03d", p.getValue().getMaDv())));
        tenDvCol.setCellValueFactory(new PropertyValueFactory<>("tenDv"));
        giaDvCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(String.format("%,3d", p.getValue().getGiaDv())));
        donViCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getDonVi()));
        ghiChuCol.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));

        reloadData();

        dichVuTable.setRowFactory(tv -> {
            TableRow<DichVu> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()))
                    edit();
            });
            return row;
        });

        editBtn.setOnAction(event -> edit());
        addBtn.setOnAction(event -> add());
        deleteBtn.setOnAction(event -> delete());
        refreshBtn.setOnAction(event -> refresh());

//        c.addMenu.setDisable(false);
//        c.editMenu.setDisable(false);
//        c.deleteMenu.setDisable(false);
//        c.exportMenu.setDisable(false);
//        c.importMenu.setDisable(false);
//
//        c.addMenu.setOnAction(e -> addBtn.fire());
//        c.editMenu.setOnAction(e -> editBtn.fire());
//        c.deleteMenu.setOnAction(e -> deleteBtn.fire());
//        c.exportMenu.setOnAction(e -> export());
//        c.importMenu.setOnAction(event -> importData());
    }

    public void reloadData() {
        data = FXCollections.observableArrayList(DichVuDAO.getInstance().getAll());
        dichVuTable.setItems(data);
    }

    public void refresh() {
        reloadData();

        RotateTransition rt = new RotateTransition(Duration.millis(750), refreshIcon);
        rt.setByAngle(360 * 3);
        rt.setCycleCount(1);
        rt.setInterpolator(Interpolator.EASE_BOTH);
        rt.play();
    }

    public void add() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("view/dichVu/suaDichVu.fxml"));
            Parent editRoot = loader.load();
            new JMetro(editRoot, Style.LIGHT);

            Stage stage = new Stage();
            Scene scene = new Scene(editRoot);

            stage.setTitle("Thêm dịch vụ");
            stage.setScene(scene);
            stage.setResizable(false);

            SuaDichVu suaDichVu = loader.getController();
            suaDichVu.init();

            stage.showAndWait();
            reloadData();
        } catch (IOException e) {
            ExceptionHandler.handle(e);
        }

    }

    public void delete() {
        DichVu focusedDichVu = dichVuTable.getSelectionModel().getSelectedItem();

        if (focusedDichVu == null) {
            ExceptionHandler.handle(new RuntimeException("Bạn chưa chọn dịch vụ nào."));
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xoá");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xoá?");
        confirmAlert.setContentText("Bạn đang thực hiện xoá Dịch vụ " + focusedDichVu.getTenDv() + ".");
        confirmAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> choice = confirmAlert.showAndWait();

        if (choice.get() == ButtonType.OK) {
            if (DichVuDAO.getInstance().delete(focusedDichVu)) {
                Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
                resultAlert.setTitle("Kết quả xoá");
                resultAlert.setHeaderText("Xoá thành công!");
                resultAlert.setContentText("Đã xoá dịch vụ " + focusedDichVu.getTenDv() + " thành công.");
                resultAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                resultAlert.showAndWait();
                reloadData();
            } else {
                Alert resultAlert = new Alert(Alert.AlertType.ERROR);
                resultAlert.setTitle("Kết quả xoá");
                resultAlert.setHeaderText("Xoá thất bại!");
                resultAlert.setContentText("Dịch vụ " + focusedDichVu.getTenDv() + " chưa xoá khỏi CSDL.");
                resultAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                resultAlert.showAndWait();
            }
        }
    }

    public void edit() {
        DichVu focusedDichVu = dichVuTable.getSelectionModel().getSelectedItem();

        if (focusedDichVu == null) {
            ExceptionHandler.handle(new RuntimeException("Bạn chưa chọn dịch vụ nào."));
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("view/dichVu/suaDichVu.fxml"));
        try {
            Parent editRoot = loader.load();
            new JMetro(editRoot, Style.LIGHT);

            Stage stage = new Stage();
            Scene scene = new Scene(editRoot);
            stage.setTitle("Sửa dịch vụ");
            stage.setScene(scene);
            stage.setResizable(false);
            SuaDichVu suaDichVu = loader.getController();
            suaDichVu.init(focusedDichVu);

            stage.showAndWait();
        } catch (IOException e) {
            ExceptionHandler.handle(e);
        }

        reloadData();
    }

    public void export() {
        if (data.size() == 0) {
            ExceptionHandler.handle(new Exception("Không tìm thấy dữ liệu."));
        } else {

            XSSFWorkbook workbook = Reporter.getWorkbook("DsDichVu.xlsx");
            XSSFSheet sheet = workbook.getSheetAt(0);

            // CELL STYLES
            XSSFCellStyle tableElementStyle = workbook.createCellStyle();
            tableElementStyle.setBorderLeft(BorderStyle.THIN);
            tableElementStyle.setBorderRight(BorderStyle.THIN);
            tableElementStyle.setFont(workbook.createFont());
            tableElementStyle.getFont().setFontHeightInPoints((short) 9);
            tableElementStyle.getFont().setFontName("Arial");
            tableElementStyle.setWrapText(true);

            XSSFCellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setFont(workbook.createFont());
            dateStyle.getFont().setFontHeightInPoints((short) 9);
            dateStyle.getFont().setFontName("Arial");
            dateStyle.setAlignment(HorizontalAlignment.CENTER);

            XSSFCellStyle searchInfoStyle = workbook.createCellStyle();
            searchInfoStyle.setFont(workbook.createFont());
            searchInfoStyle.getFont().setFontHeightInPoints((short) 9);
            searchInfoStyle.setAlignment(HorizontalAlignment.CENTER);
            searchInfoStyle.getFont().setFontName("Arial");

            XSSFCell cell = null;

            DichVu dichVu;

            // DATE
            cell = sheet.getRow(3).getCell(0);
            cell.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("'Ngày 'dd' tháng 'MM' năm 'yyyy")));
            cell.setCellStyle(dateStyle);

            // DATA
            for (int i = 0; i < data.size(); i++) {
                dichVu = data.get(i);
                int row = 6 + i;
                sheet.createRow(row);

                cell = sheet.getRow(row).createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(1);
                cell.setCellValue(String.format("%03d", dichVu.getMaDv()));
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(2);
                cell.setCellValue(dichVu.getTenDv());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(3);
                cell.setCellValue(dichVu.getGiaDv());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(4);
                cell.setCellValue(dichVu.getDonVi());
                cell.setCellStyle(tableElementStyle);

                cell = sheet.getRow(row).createCell(5);
                cell.setCellValue(dichVu.getGhiChu());
                cell.setCellStyle(tableElementStyle);
            }

            Reporter.saveWorkbook(workbook, dichVuTable);

        }
    }

    public void importData() {
        ArrayList<DichVu> dsDichVu = new ArrayList<>();
        DichVu newDichVu;

        XSSFWorkbook excelWorkBook;

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn file");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selectedFile = fileChooser.showOpenDialog(dichVuTable.getScene().getWindow());

            FileInputStream inputStream = new FileInputStream(selectedFile);
            excelWorkBook = new XSSFWorkbook(inputStream);
            inputStream.close();
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return;
        }

        XSSFSheet sheet = excelWorkBook.getSheetAt(0);

        // DATA
        Iterator rows = sheet.rowIterator();
        XSSFRow row = (XSSFRow) rows.next();
        if (row.getLastCellNum() >= 6) {
//                {"Tên DV", "Giá tiền", "Đơn vị", "Ghi chú"}

            while (rows.hasNext()) {
                row = (XSSFRow) rows.next();
                newDichVu = new DichVu();

                newDichVu.setTenDv(row.getCell(0, CREATE_NULL_AS_BLANK).getStringCellValue());
                newDichVu.setGiaDv((long) row.getCell(1, CREATE_NULL_AS_BLANK).getNumericCellValue());
                newDichVu.setDonVi(row.getCell(2, CREATE_NULL_AS_BLANK).getStringCellValue());
                newDichVu.setGhiChu(row.getCell(3, CREATE_NULL_AS_BLANK).getStringCellValue());

                dsDichVu.add(newDichVu);
            }
        } else {
            ExceptionHandler.handle(new Exception("File không đúng định dạng." + row.getLastCellNum()));
        }

        DichVuDAO.getInstance().importData(dsDichVu);
        refresh();
    }
}