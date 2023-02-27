package com.example.smsroot2;

import com.example.smsroot2.entity.ContentEntity;
import com.example.smsroot2.util.SmsUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class SmsApplication {
    private static TableView<ContentEntity> contentEntityTableView;

    public void start() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("sms-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 800);
        stage.setTitle("接收短信历史");
        stage.setScene(scene);
        stage.show();
        new SmsUtil(fxmlLoader, "table_sms");
        initView(fxmlLoader);
    }


    public static void setItems(ObservableList<ContentEntity> contentEntities) {
        contentEntityTableView.setItems(contentEntities);
    }


    private static void initView(FXMLLoader fxmlLoader) {
        contentEntityTableView = findViewById(fxmlLoader, "tableView");
    }

    private static <T> T findViewById(FXMLLoader fxmlLoader, String value) {
        return (T) fxmlLoader.getNamespace().get(value);
    }
}
