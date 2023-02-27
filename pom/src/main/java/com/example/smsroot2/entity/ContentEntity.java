package com.example.smsroot2.entity;

public class ContentEntity {
    //id
    private String id;
    //设备号码
    private String phone;
    //接收人号码
    private String send_phone;
    //发送内容
    private String Content;
    //时间
    private String time;
    //状态
    private String state;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSend_phone() {
        return send_phone;
    }

    public void setSend_phone(String send_phone) {
        this.send_phone = send_phone;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "ContentEntity{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", send_phone='" + send_phone + '\'' +
                ", Content='" + Content + '\'' +
                ", time='" + time + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
