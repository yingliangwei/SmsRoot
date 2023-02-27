package com.example.smsroot2.entity;

import com.alibaba.fastjson2.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class UserEntity {
    public String getUser() {
        return user;
    }

    public void write(JSONObject jsonObject) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        if (getSocketChannel().isConnected()) {
            getSocketChannel().write(byteBuffer);
        }
    }
    public void write(String str) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8));
        if (getSocketChannel().isConnected()) {
            getSocketChannel().write(byteBuffer);
        }
    }

    public void setUser(String user) {
        this.user = user;
    }

    //管理连接才有的用户名
    public String user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String id;
    //手机号码
    private String phone;
    //连接的地址
    private SocketChannel socketChannel;
    private String ip;
    private String time;
    private String send_size;
    private String login_id;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public UserEntity(String phone, SocketChannel channel, String time) {
        this.time = time;
        this.setSocketChannel(channel);
    }

    public UserEntity(SocketChannel channel) {
        this.setSocketChannel(channel);
        setIp(channel.socket().getInetAddress().getHostAddress());
    }

    public UserEntity() {

    }


    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getSend_size() {
        return send_size;
    }

    public void setSend_size(String send_size) {
        this.send_size = send_size;
    }
}
