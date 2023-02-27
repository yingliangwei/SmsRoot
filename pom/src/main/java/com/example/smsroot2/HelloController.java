package com.example.smsroot2;

import com.alibaba.fastjson2.JSONObject;
import com.example.smsroot2.database.SaveList;
import com.example.smsroot2.entity.ContentEntity;
import com.example.smsroot2.entity.UserEntity;
import com.example.smsroot2.taskManager.OnNotice;
import com.example.smsroot2.taskManager.SendTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class HelloController {
    @FXML
    public Button button_start;
    @FXML
    public TextArea SendMobileNumber;
    @FXML
    public TextArea log;
    @FXML
    public TextArea SendContext;
    @FXML
    public TextField DistributionSpeed;
    @FXML
    public TableView<ContentEntity> tab_send_content;
    public TableView<UserEntity> tableView;
    public Button Button_SelectAll;
    public Label Select_size;
    public Button button_send;
    private SendTask sendTask;
    private long millis;


    private final OnNotice onNotice = new OnNotice() {
        @Override
        public void success() {
            Platform.runLater(() -> {
                button_start.setText("开始分配群发");
                button_start.setStyle("-fx-background-color: #337ab7");
            });
            sendTask = null;
        }

        @Override
        public void error() {

        }
    };

    //开始分配群发
    public void StartDistribution() {
        if (button_start.getText().equals("关闭分配群发")) {
            sendTask.setRun(true);
            button_start.setText("开始分配群发");
            button_start.setStyle("-fx-background-color: #337ab7");
            return;
        }
        String[] name = SendMobileNumber.getText().split("\n");
        if (name.length == 0) {
            log.appendText("可分配号码为空\n");
            return;
        }
        if (SendContext.getText().length() == 0) {
            log.appendText("内容为空\n");
            return;
        }
        if (SaveList.getPhonesSize() == 0) {
            log.appendText("可分配设备为空\n");
            return;
        }
        if (button_start.getText().equals("开始分配群发")) {
            button_start.setText("关闭分配群发");
            button_start.setStyle("-fx-background-color: #b73333");
        }
        String mills = DistributionSpeed.getText();
        millis = 0;
        if (mills.length() != 0) {
            millis = Long.parseLong(DistributionSpeed.getText());
        }

        sendTask = new SendTask(onNotice, false, tab_send_content, SaveList.getPhones(), name, SendContext.getText(), millis);
        sendTask.start();
    }

    public void button_log() {
        try {
            new SmsApplication().start();
            write("query_sms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void button_send_log() {
        try {
            new SmsSendApplication().start();
            write("query_sms_send");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login_log() {
        new LoginApplication().start("Login");
        write("query_login");
    }

    public void button_login_break() {
        new LoginBackApplication().start("Login_back");
        write("query_login_back");
    }

    public void write(String user) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "root");
        jsonObject.put("user", user);
    }


    private final OnNotice onNotice1 = new OnNotice() {
        @Override
        public void success() {
            Platform.runLater(() -> {
                button_send.setText("开始直接发送短信");
                button_send.setStyle("-fx-background-color: #337ab7");
            });

        }

        @Override
        public void error() {

        }
    };

    private SendTask sendTask1;

    public void button_send() {
        if (button_send.getText().equals("关闭直接发送短信")) {
            sendTask1.setRun(true);
            button_send.setText("开始直接发送短信");
            button_send.setStyle("-fx-background-color: #337ab7");
            return;
        }
        String[] name = SendMobileNumber.getText().split("\n");
        if (name.length == 0) {
            log.appendText("可分配号码为空\n");
            return;
        }
        if (SendContext.getText().length() == 0) {
            log.appendText("内容为空\n");
            return;
        }
        if (SaveList.getPhonesSize() == 0) {
            log.appendText("可分配设备为空\n");
            return;
        }
        if (button_start.getText().equals("开始直接发送短信")) {
            button_start.setText("关闭直接发送短信");
            button_send.setStyle("-fx-background-color: #b73333");
        }
        String mills = DistributionSpeed.getText();
        millis = 0;
        if (mills.length() != 0) {
            millis = Long.parseLong(DistributionSpeed.getText());
        }
        sendTask1 = new SendTask(onNotice1, true, tab_send_content, SaveList.getPhones(), name, SendContext.getText(), millis);
        sendTask1.start();
    }

    public void SelectAll() {
        if (Button_SelectAll.getText().equals("全选")) {
            Button_SelectAll.setText("取消全选");
            SaveList.SelectAll(tableView);
            Select_size.setText("选中数量：" + SaveList.getPhonesSize());
        } else {
            SaveList.NoSelectAll(tableView);
            Button_SelectAll.setText("全选");
            Select_size.setText("选中数量：" + SaveList.getPhonesSize());
        }
    }

    public void close() {
        System.exit(0);
    }
}