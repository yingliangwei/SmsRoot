package com.example.smsroot2.database;

import com.example.smsroot2.HelloApplication;
import com.example.smsroot2.entity.ContentEntity;
import com.example.smsroot2.entity.UserEntity;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

//储存列表
public class SaveList {
    //储存选中
    private static final List<UserEntity> phones = new ArrayList<>();
    //储存登录数据
    private static final ObservableList<UserEntity> login = FXCollections.observableArrayList();
    //储存收短信的内容
    private static final ObservableList<ContentEntity> contentEntities = FXCollections.observableArrayList();
    //储存发短信
    private static final ObservableList<ContentEntity> send_contentEntities = FXCollections.observableArrayList();
    //登录
    private static ChannelHandlerContext context;

    public static void setContext(ChannelHandlerContext context) {
        SaveList.context = context;
    }

    public static void remove() {
        context = null;
    }

    public static void SelectAll(TableView<UserEntity> tableView) {
        for (UserEntity user : login) {
            if (isPhones(user)) {
                phones.add(user);
                //刷新
                tableView.refresh();
            }
        }
    }

    public static void removeLoginALL() {
        login.clear();
        HelloApplication.userEntityTableView.getItems().clear();
        HelloApplication.setConnections(login.size());
        remove();
    }

    public static void NoSelectAll(TableView<UserEntity> tableView) {
        phones.clear();
        tableView.refresh();
    }

    /**
     * @param user 判断是否存在
     * @return 不存在
     */
    private static boolean isPhones(UserEntity user) {
        for (UserEntity userEntity : phones) {
            if (userEntity.getPhone().equals(user.getPhone())) {
                return false;
            }
        }
        return true;
    }


    public static void clearLogin() {
        login.clear();
        HelloApplication.userEntityTableView.getItems().clear();
    }

    public static void clearPhones() {
        phones.clear();
    }

    //判断是否存在
    public static boolean containsPhones(String phone) {
        for (UserEntity user : phones) {
            if (user.getPhone().equals(phone)) {
                return true;
            }
        }
        return false;
    }

    public static UserEntity getLoginPhone(String phone) {
        for (UserEntity user : login) {
            if (user.getPhone().equals(phone)) {
                return user;
            }
        }
        return null;
    }

    //选中账号
    public static void addPhones(String phone) {
        for (UserEntity user : login) {
            if (user.getPhone().equals(phone)) {
                phones.add(user);
                if (phones.size() == login.size()) {
                    Platform.runLater(() -> {
                        HelloApplication.Button_SelectAll.setText("取消全选");
                        HelloApplication.Select_size.setText("选中数量：" + getPhonesSize());
                    });
                }
            }
        }
    }

    public static void removePhones(String phone) {
        for (int i = 0; i < phones.size(); i++) {
            UserEntity user = phones.get(i);
            if (user.getPhone().equals(phone)) {
                phones.remove(i);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        HelloApplication.Button_SelectAll.setText("全选");
                        HelloApplication.Select_size.setText("选中数量:" + getPhonesSize());
                    }
                });
                break;
            }
        }
    }

    //添加登录
    public static void addLogin(UserEntity user) {
        if (isUser(user)) {
            return;
        }
        login.add(user);
        HelloApplication.userEntityTableView.setItems(login);
        HelloApplication.setConnections(login.size());
    }

    public static boolean isUser(UserEntity user) {
        for (UserEntity userEntity : login) {
            if (user.getPhone().equals(userEntity.getPhone())) {
                return true;
            }
        }
        return false;
    }

    //删除该登录
    public static void removeLogin(UserEntity user) {
        for (int i = 0; i < login.size(); i++) {
            UserEntity userEntity = login.get(i);
            if (userEntity.getPhone().equals(user.getPhone())) {
                removePhones(userEntity.getPhone());
                login.remove(i);
                HelloApplication.userEntityTableView.getItems().remove(i);
                break;
            }
        }
    }

    //更新发送数量
    public static void UpdateLoginSend_size(String phone, String send_size) {
        for (int i = 0; i < login.size(); i++) {
            UserEntity user = login.get(i);
            if (user.getPhone().equals(phone)) {
                HelloApplication.userEntityTableView.getItems().get(i).setSend_size(send_size);
            }
        }
    }

    //更新发送状态
    public static void UpdateState(ContentEntity contentEntity) {
        for (int i = 0; i < HelloApplication.send_TableView.getItems().size(); i++) {
            ContentEntity content = HelloApplication.send_TableView.getItems().get(i);
            if (content.getState().equals("0")) {
                if (content.getPhone().equals(contentEntity.getPhone()) && content.getSend_phone().equals(contentEntity.getSend_phone()) && content.getContent().equals(contentEntity.getContent())) {
                    content.setState(contentEntity.getState());
                    HelloApplication.send_TableView.getItems().set(i, content);
                    break;
                }
            }
        }
    }

    //添加接收短信
    public static void addContentEntity(ContentEntity contentEntity) {
        contentEntities.add(contentEntity);
        System.out.println(contentEntities.size() + "\n");
        HelloApplication.contentEntityTableView.setItems(contentEntities);
    }

    //添加发送短信
    public static void addSendContentEntity(ContentEntity content) {
        send_contentEntities.add(content);
        HelloApplication.send_TableView.getItems().add(content);
    }

    public static int getPhonesSize() {
        return phones.size();
    }

    public static List<UserEntity> getPhones() {
        return phones;
    }

    public static void write(String toString) {
        if (context != null) {
            context.channel().writeAndFlush(toString);
        }
    }

    //退出登录,删除全部数据
    public static void removeChannelHandlerContext(ChannelHandlerContext ctx) {
        removeLoginALL();
    }
}
