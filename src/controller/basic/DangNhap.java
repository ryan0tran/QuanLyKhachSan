/*
 * Copyright (c) 2019 Nghia Tran.
 * Project I - Library Management System
 */

package controller.basic;

import dao.NhanVienDAO;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.NhanVien;
import util.DbConnection;
import util.ExceptionHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DangNhap {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginBtn;
    @FXML
    private Label statusLabel;

    public void init() {
        statusLabel.setVisible(true);
        Runnable checkDbCon = () -> {
            Connection con = DbConnection.getConnection();
            if (con == null) {
                Platform.runLater(() -> {
                    ExceptionHandler.handle(new Exception("Lỗi kết nối tới cơ sở dữ liệu! Chương trình sẽ thoát."));
                    System.exit(0);
                });
            } else {
                Platform.runLater(() -> statusLabel.setText("Trình trạng CSDL: Đã kết nối"));
                try {
                    con.close();
                } catch (SQLException e) {
                    ExceptionHandler.handle(e);
                }
            }
        };
        new Thread(checkDbCon).start();
    }

    public void login(Event event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi đăng nhập");
            alert.setHeaderText("Không được bỏ trống tên đăng nhập hoặc mật khẩu!");
            alert.setContentText("Vui lòng kiểm tra lại thông tin đăng nhập.");
            alert.showAndWait();
            return;
        }

       try {
            NhanVien user = NhanVienDAO.authenticate(username, password);

            if (user != null) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getClassLoader().getResource("view/basic/frame.fxml"));
                    Parent root = loader.load();

                    new JMetro(root, Style.LIGHT);
                    Stage stage = new Stage();
                    stage.setTitle("Gold Hotel");
                    stage.setScene(new Scene(root, 450, 450));
                    stage.setMaximized(true);

                    KhungUngDung khungUngDung = loader.getController();
                    khungUngDung.init(user);

//                    stage.getIcons().add(new Image("/resources/icon/app-icon.png"));
                    stage.show();
                    ((Node)(event.getSource())).getScene().getWindow().hide();
                } catch (IOException e) {
                    ExceptionHandler.handle(e);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi đăng nhập!");
                alert.setHeaderText("Tên đăng nhập hoặc mật khẩu sai!");
                alert.setContentText("Vui lòng kiểm tra lại thông tin đăng nhập.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi CSDL");
            alert.setHeaderText("Có lỗi xảy ra trong quá trình đăng nhập: ");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.out.println(e.getMessage());
        }
    }
}
