package com.example.smsroot2;

import com.example.smsroot2.entity.UserEntity;
import com.example.smsroot2.util.LoginUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class LoginApplication {
    private static TableView<UserEntity> userEntityTableView;

    public void start(String tab) {
        try {
            Stage newStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 550);
            newStage.setTitle("登录历史");
            newStage.setWidth(1000);
            newStage.setHeight(550);
            newStage.setScene(scene);
            newStage.show();
            initView(fxmlLoader);
            new LoginUtil(fxmlLoader, tab);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setItems(ObservableList<UserEntity> userEntities) {
        userEntityTableView.setItems(userEntities);
    }

    private void initView(FXMLLoader fxmlLoader) {
        LoginApplication.userEntityTableView = findViewById(fxmlLoader, "tableView");
    }

    <T> T findViewById(FXMLLoader fxmlLoader, String value) {
        return (T) fxmlLoader.getNamespace().get(value);
    }
}