package com.example.smsroot2;

import com.example.smsroot2.database.SaveList;
import com.example.smsroot2.entity.ContentEntity;
import com.example.smsroot2.entity.UserEntity;
import com.example.smsroot2.network.TcpClient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class HelloApplication extends Application {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @FXML
    public static TextArea log;
    public static Button Button_SelectAll;
    @FXML // 连接状态
    public static Label NetworkStatus;
    @FXML
    Label Time;
    @FXML
    public static Label connections;
    @FXML
    TextArea ServerLog;
    public static Label Select_size;
    @FXML
    public static TableView<UserEntity> userEntityTableView;
    @FXML
    public static TableView<ContentEntity> send_TableView;
    @FXML
    public static TableView<ContentEntity> contentEntityTableView;

    public static void setNetworkStatus(String text) {
        Platform.runLater(() -> NetworkStatus.setText("连接状态:" + text));
    }

    private FXMLLoader fxmlLoader;

    public static void print(String text) {
        log.appendText(text + "\n");
    }

    public static void setConnections(int size) {
        Platform.runLater(() -> connections.setText("连接设备数量：" + size));
    }

    @Override
    public void start(Stage stage) throws IOException {
        fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 800);
        stage.setTitle("管理系统v1.0");
        stage.setScene(scene);
        stage.show();
        setFxmlLoader(fxmlLoader);
        initView();
        initLoad();
        executor.execute(new Runnable() {

            @Override
            public void run() {
                TcpClient tcpClient = new TcpClient(executor);
                tcpClient.connect();
            }
        });
    }

    private void initLoad() {
        DateFormat currentTime = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss"); // 设置时间格式
        EventHandler<ActionEvent> eventHandler = e -> Time.setText(currentTime.format(new Date()));
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), eventHandler)); // 一秒刷新一次
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private void initView() {
        HelloApplication.Button_SelectAll = findViewById("Button_SelectAll");
        HelloApplication.log = findViewById("log");
        HelloApplication.Select_size = findViewById("Select_size");
        HelloApplication.userEntityTableView = findViewById("tableView");
        HelloApplication.send_TableView = findViewById("tab_send_content");
        HelloApplication.contentEntityTableView = findViewById("tab_receive_content");

        ServerLog = findViewById("ServerLog");
        Time = findViewById("time");
        HelloApplication.connections = findViewById("connections");
        NetworkStatus = findViewById("NetworkStatus");

        TextArea SendMobileNumber = findViewById("SendMobileNumber");
        Label label = findViewById("phone_size");
        // 监听文本更改
        SendMobileNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            String[] name = newValue.split("\n");
            label.setText("号码总数量:" + name.length);
        });

        initTableView();
    }

    private void initTableView() {
        initTableColumnUserEntity("tab_ip", "ip");
        initTableColumnUserEntity("tab_phone", "phone");
        initTableColumnUserEntity("tab_send_size", "send_size");
        initTableColumnUserEntity("tab_time", "time");
        CheckBox();

        initTableColumnContentEntity("tab_receive_content_phone", "phone");
        initTableColumnContentEntity("tab_receive_content_send_phone", "send_phone");
        initTableColumnContentEntity("tab_receive_content_content", "Content");
        initTableColumnContentEntity("tab_receive_content_time", "time");

        initTableColumnContentEntity("tab_send_content_state", "state");
        initTableColumnContentEntity("tab_send_content_phone", "phone");
        initTableColumnContentEntity("tab_send_content_send_phone", "send_phone");
        initTableColumnContentEntity("tab_send_content_content", "content");
        initTableColumnContentEntity("tab_send_content_time", "time");
    }

    private void CheckBox() {
        TableColumn<UserEntity, String> checkboxCol = findViewById("tab_x");
        PropertyValueFactory<UserEntity, String> phone1 = new PropertyValueFactory<>("phone");
        checkboxCol.setCellValueFactory(phone1);
        checkboxCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<UserEntity, String> call(TableColumn<UserEntity, String> userEntityStringTableColumn) {
                return new TextFieldTableCell<>() {
                    @Override
                    public void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (s == null || b) {
                            return;
                        }
                        CheckBox checkBox = new CheckBox();
                        if (SaveList.containsPhones(s)) {
                            checkBox.setSelected(true);
                        }
                        HBox hbox = new HBox();
                        hbox.setAlignment(Pos.CENTER);
                        hbox.getChildren().add(checkBox);
                        // 增加监听
                        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue) {
                                UserEntity user = SaveList.getLoginPhone(s);
                                if (user == null) {
                                    ServerLog.appendText("不存在该设备\n");
                                    return;
                                }
                                int size = Integer.parseInt(user.getSend_size());
                                if (size >= 200) {
                                    checkBox.setSelected(false);
                                    ServerLog.appendText("该设备今天已经超额！\n");
                                } else {
                                    SaveList.addPhones(s);
                                    Select_size.setText("选中数量：" + SaveList.getPhonesSize());
                                }
                            } else {
                                SaveList.removePhones(s);
                                Select_size.setText("选中数量：" + SaveList.getPhonesSize());
                            }
                        });
                        this.setGraphic(hbox);
                    }
                };
            }
        });
    }

    void initTableColumnContentEntity(String id, String value) {
        TableColumn<ContentEntity, String> tab_receive_content_send_phone = findViewById(id);
        PropertyValueFactory<ContentEntity, String> contentEntityStringPropertyValueFactory1 = new PropertyValueFactory<>(
                value);
        tab_receive_content_send_phone.setCellValueFactory(contentEntityStringPropertyValueFactory1);
        tab_receive_content_send_phone.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    void initTableColumnUserEntity(String id, String value) {
        TableColumn<UserEntity, String> tab_receive_content_send_phone = findViewById(id);
        PropertyValueFactory<UserEntity, String> contentEntityStringPropertyValueFactory1 = new PropertyValueFactory<>(
                value);
        tab_receive_content_send_phone.setCellValueFactory(contentEntityStringPropertyValueFactory1);
        tab_receive_content_send_phone.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    public void setFxmlLoader(FXMLLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }

    @FXML
    public <T> T findViewById(String value) {
        return (T) fxmlLoader.getNamespace().get(value);
    }

    public static void main(String[] args) {
        launch(args);
    }
}