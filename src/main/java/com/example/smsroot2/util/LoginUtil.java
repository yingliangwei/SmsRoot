package com.example.smsroot2.util;

import com.example.smsroot2.entity.UserEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class LoginUtil {
    private FXMLLoader fxmlLoader;
    private String tab;

    public LoginUtil(FXMLLoader fxmlLoader, String tab) {
        this.fxmlLoader = fxmlLoader;
        this.tab = tab;
        run();
    }

    private void run() {
        initView();
    }

    ObservableList<UserEntity> userEntities = FXCollections.observableArrayList();

    public void initView() {
        Label size = findViewById("size");
        TextField field = findViewById("edit_search");
        TableView<UserEntity> contentEntityTableView = findViewById("tableView");
        contentEntityTableView.setEditable(true);
        initTableColumn("id", "id");
        initTableColumn("tab_phone", "phone");
        initTableColumn("tab_ip", "ip");
        initTableColumn("tab_time", "time");

        if (tab.equals("Login_back")) {

        }
        FilteredList<UserEntity> filteredReports = new FilteredList<>(userEntities);
        field.setOnKeyReleased(keyEvent -> {
            if (userEntities.size() == 0) {
                userEntities.addAll(contentEntityTableView.getItems());
            }
            field.textProperty().addListener((observableValue, s, t1) -> filteredReports.setPredicate(Report -> {
                if (t1 == null || t1.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = t1.toLowerCase();
                if (Report.getPhone().contains(t1)) {
                    return true;
                } else if (Report.getId().equals(t1)) {
                    return true;
                } else if (Report.getTime().contains(t1)) {
                    return true;
                } else return Report.getIp().toLowerCase().contains(lowerCaseFilter);
            }));
            SortedList<UserEntity> sortedReports = new SortedList<>(filteredReports);
            sortedReports.comparatorProperty().bind(contentEntityTableView.comparatorProperty());
            contentEntityTableView.setItems(sortedReports);
            size.setText("总数量：" + sortedReports.size());
        });
    }

    void initTableColumn(String id, String value) {
        TableColumn<UserEntity, String> tab_receive_content_send_phone = findViewById(id);
        PropertyValueFactory<UserEntity, String> contentEntityStringPropertyValueFactory1 = new PropertyValueFactory<>(value);
        tab_receive_content_send_phone.setCellValueFactory(contentEntityStringPropertyValueFactory1);
        tab_receive_content_send_phone.setCellFactory(TextFieldTableCell.forTableColumn());
    }


    <T> T findViewById(String value) {
        return (T) fxmlLoader.getNamespace().get(value);
    }
}
