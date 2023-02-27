package com.example.smsroot2.util;

import com.example.smsroot2.entity.ContentEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class SmsUtil {
    FXMLLoader fxmlLoader;
    String tabId;

    public SmsUtil(FXMLLoader fxmlLoader, String tabId) {
        this.fxmlLoader = fxmlLoader;
        this.tabId = tabId;
        run();
    }

    private void run() {
        initView();
    }

    ObservableList<ContentEntity> contentEntities = FXCollections.observableArrayList();

    private void initView() {
        Label size = findViewById("size");
        TextField field = findViewById("edit_search");
        TableView<ContentEntity> contentEntityTableView = findViewById("tableView");
        contentEntityTableView.setEditable(true);
        initTableColumn("id", "id");
        initTableColumn("tab_phone", "phone");
        initTableColumn("tab_send_phone", "send_phone");
        initTableColumn("tab_context", "content");
        initTableColumn("tab_time", "time");
        initTableColumn("tab_state", "state");
        Button button = findViewById("search");
        button.setOnAction(actionEvent -> field.setText("失败率:0"));
        TableColumn<ContentEntity, String> tableColumn = findViewById("tab_state");
        if (tabId.equals("table_sms_send")) {
            tableColumn.setVisible(true);
        }
        FilteredList<ContentEntity> filteredReports = new FilteredList<>(contentEntities);
        field.setOnKeyReleased(keyEvent -> {
            if (contentEntities.size() == 0) {
                contentEntities.addAll(contentEntityTableView.getItems());
            }
            field.textProperty().addListener((observableValue, s, t1) -> filteredReports.setPredicate(Report -> {
                if (t1 == null || t1.isEmpty()) {
                    return true;
                }
                if (t1.startsWith("失败率:")) {
                    String[] t = t1.split(":");
                    if (t.length == 2) {
                        return Report.getState().equals(t[1]);
                    }
                } else {
                    String lowerCaseFilter = t1.toLowerCase();
                    if (Report.getPhone().contains(t1)) {
                        return true;
                    } else if (Report.getContent().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else return Report.getSend_phone().contains(lowerCaseFilter);
                }
                return false;
            }));
            SortedList<ContentEntity> sortedReports = new SortedList<>(filteredReports);
            sortedReports.comparatorProperty().bind(contentEntityTableView.comparatorProperty());
            contentEntityTableView.setItems(sortedReports);
            size.setText("总数量：" + sortedReports.size());
        });
    }

    void initTableColumn(String id, String value) {
        TableColumn<ContentEntity, String> tab_receive_content_send_phone = findViewById(id);
        PropertyValueFactory<ContentEntity, String> contentEntityStringPropertyValueFactory1 = new PropertyValueFactory<>(value);
        tab_receive_content_send_phone.setCellValueFactory(contentEntityStringPropertyValueFactory1);
        tab_receive_content_send_phone.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    <T> T findViewById(String value) {
        return (T) fxmlLoader.getNamespace().get(value);
    }
}
