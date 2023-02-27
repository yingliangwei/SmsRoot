package com.example.smsroot2.analysis;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import com.example.smsroot2.database.SaveList;
import com.example.smsroot2.entity.ContentEntity;
import com.example.smsroot2.entity.UserEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AnalysisData {
    public static void analysis(String str) {
        JSONObject jsonObject = JSONObject.parseObject(str);
        String type = jsonObject.getString("type");
        // 用户登录
        switch (type) {
            case "login":
                SaveList.addLogin(getUserEntity(jsonObject));
                break;
            case "login_back":
                SaveList.removeLogin(getUserEntity(jsonObject));
                break;
            case "sms":
                SaveList.addContentEntity(getSmsContentEntity(jsonObject));
                break;
            case "resultCode": {
                ContentEntity content = getContentEntity(jsonObject);
                String code = jsonObject.getString("code");
                content.setState(code);
                String send_size = jsonObject.getString("send_size");
                SaveList.UpdateLoginSend_size(content.getPhone(), send_size);
                SaveList.UpdateState(content);
            }
            case "send_sms": {
                ContentEntity content = getContentEntity(jsonObject);
                content.setState("0");
                String send_size = jsonObject.getString("send_size");
                SaveList.UpdateLoginSend_size(content.getPhone(), send_size);
                SaveList.addSendContentEntity(content);
            }
          
            case "query_sms": {
                // 接收短信历史记录
                JSONArray array = jsonObject.getJSONArray("data");
                //SmsApplication.setItems(getContentEntities(array));
            }
            case "query_sms_send": {
                // 发送短信历史记录
                JSONArray array = jsonObject.getJSONArray("data");
               // SmsSendApplication.setItems(getContentEntities(array));
            }
            case "query_login": {
                // 登录历史记录
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                //LoginApplication.setItems(getUserEntities(jsonArray));
            }

            default: {
                break;
            }
        }
    }

    private static ObservableList<UserEntity> getUserEntities(JSONArray jsonArray) {
        ObservableList<UserEntity> userEntities = FXCollections.observableArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            UserEntity userEntity = new UserEntity();
            userEntity.setId(object.getString("id"));
            userEntity.setPhone(object.getString("phone"));
            userEntity.setIp(object.getString("ip"));
            userEntity.setTime(object.getString("time"));
            userEntities.add(userEntity);
        }
        return userEntities;
    }

    private static ObservableList<ContentEntity> getContentEntities(JSONArray array) {
        ObservableList<ContentEntity> contentEntities = FXCollections.observableArrayList();
        if(array==null){
            return contentEntities;
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            ContentEntity contentEntity = new ContentEntity();
            contentEntity.setPhone(object.getString("phone"));
            contentEntity.setContent(object.getString("content"));
            contentEntity.setSend_phone(object.getString("send_phone"));
            contentEntity.setId(object.getString("id"));
            contentEntity.setTime(object.getString("time"));
            contentEntity.setState(object.getString("state"));
            contentEntities.add(contentEntity);
        }
        return contentEntities;
    }

    private static ContentEntity getSmsContentEntity(JSONObject jsonObject) {
        String phone = jsonObject.getString("number");
        String send_phone = jsonObject.getString("senderNumber");
        String smsMessages = jsonObject.getString("smsMessages");
        String time = jsonObject.getString("time");
        ContentEntity content = new ContentEntity();
        content.setPhone(phone);
        content.setSend_phone(send_phone);
        content.setTime(time);
        content.setContent(smsMessages);
        return content;
    }

    private static ContentEntity getContentEntity(JSONObject jsonObject) {
        String phone = jsonObject.getString("phone");
        String send_phone = jsonObject.getString("send_phone");
        String context = jsonObject.getString("context");
        String time = jsonObject.getString("time");
        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setPhone(phone);
        if (JSON.isValid("id")) {
            contentEntity.setId(jsonObject.getString("id"));
        }
        contentEntity.setSend_phone(send_phone);
        contentEntity.setContent(context);
        contentEntity.setTime(time);
        return contentEntity;
    }

    private static UserEntity getUserEntity(JSONObject jsonObject) {
        String phone = jsonObject.getString("phone");
        String ip = jsonObject.getString("ip");
        String send_size = jsonObject.getString("send_size");
        String time = jsonObject.getString("time");

        UserEntity userEntity = new UserEntity();
        userEntity.setPhone(phone);
        userEntity.setIp(ip);
        userEntity.setSend_size(send_size);
        userEntity.setTime(time);
        return userEntity;
    }
}
