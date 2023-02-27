package com.example.smsroot2.taskManager;

import com.alibaba.fastjson2.JSONObject;
import com.example.smsroot2.HelloApplication;
import com.example.smsroot2.database.SaveList;
import com.example.smsroot2.entity.ContentEntity;
import com.example.smsroot2.entity.UserEntity;
import com.example.smsroot2.utils.ListUtils;
import javafx.application.Platform;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendTask extends Thread {
    TableView<ContentEntity> contentEntityTableView;
    List<UserEntity> user;
    List<String> phones = new ArrayList<>();
    String context;
    long millis;
    OnNotice onNotice;
    //判断是否退出循环
    boolean isRun;
    boolean root;

    //掉线的资源
    List<String> Offline = new ArrayList<>();

    /**
     * @param onNotice               通知是否完毕
     * @param contentEntityTableView 显示发送日志
     * @param user                   选中账号集合
     * @param phones                 发送手机号码集合
     * @param context                内容
     * @param millis                 暂停时间
     */
    public SendTask(OnNotice onNotice, boolean root, TableView<ContentEntity> contentEntityTableView, List<UserEntity> user, String[] phones, String context, long millis) {
        this.user = user;
        this.root = root;
        this.onNotice = onNotice;
        this.contentEntityTableView = contentEntityTableView;
        this.context = context;
        this.millis = millis;
        addPhones(phones);
    }


    /**
     * @param onNotice               通知是否完毕
     * @param contentEntityTableView 显示发送日志
     * @param user                   要发送的设备
     * @param phones                 集合号码
     * @param context                内容
     * @param millis                 暂停时间
     */
    public SendTask(OnNotice onNotice, boolean root, TableView<ContentEntity> contentEntityTableView, UserEntity user, String[] phones, String context, long millis) {
        this.contentEntityTableView = contentEntityTableView;
        this.root = root;
        this.onNotice = onNotice;
        this.user = new ArrayList<>();
        this.user.add(user);
        this.context = context;
        this.millis = millis;
        addPhones(phones);
    }

    /**
     * @param phones 遍历到集合，好去除发送的号码
     */
    private void addPhones(String[] phones) {
        this.phones.addAll(Arrays.asList(phones));
    }

    // 创建可缓存线程池
    public void run() {
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        //分配好的数据
        List<List<String>> list = null;
        if (Offline.size() == 0) {
            //分配好的数据
            list = ListUtils.averageAssign(phones, user.size());
        } else if (user.size() != 0) {
            list = ListUtils.averageAssign(Offline, user.size());
            Offline.clear();
        } else {
            Offline.clear();
        }
        if (list == null) {
            onNotice.success();
            print("当前无可分配用户");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            List<String> phones = list.get(i);
            UserEntity user = this.user.get(i);
            //创建任务
            Runnable runnable = () -> SendContext(user, this.context, phones);
            newCachedThreadPool.execute(runnable);
        }
        newCachedThreadPool.shutdown();
        try {
            while (true) {
                if (newCachedThreadPool.isTerminated()) {
                    if (Offline.size() == 0) {
                        onNotice.success();
                        print("发送完毕");
                    } else {
                        print("开始重新分配");
                        run();
                    }
                    break;
                }
                Thread.sleep(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param user 选中号码
     */
    private void SendContext(UserEntity user, String context, List<String> phones) {
        int size = Integer.parseInt(user.getSend_size());
        if (!root && size >= 200) {
            print("【" + user.getPhone() + "】该设备今天已经达到200上限");
            return;
        }
        //开始发送短信
        for (int i = 0; i < phones.size(); i++) {
            String phone = phones.get(i);
            try {
                write(user, context, phone);
                Thread.sleep(millis);
            } catch (IOException | InterruptedException e) {
                Offline.add(phone);
                print(phones + "发送失败:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private void print(String text) {
        Platform.runLater(() -> HelloApplication.log.appendText(text + "\n"));
    }

    /**
     * @param userEntity 选中设备
     * @param context    内容
     * @param send_phone 目标号码
     * @throws IOException tcp发送失败
     */
    private void write(UserEntity userEntity, String context, String send_phone) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "root");
        jsonObject.put("user", "send");
        jsonObject.put("phone", userEntity.getPhone());
        jsonObject.put("send_phone", send_phone);
        jsonObject.put("context", context);
        SaveList.write(jsonObject.toString());
    }


    /**
     * @param phone      设备号码
     * @param send_phone 发送号码
     * @param context    内容
     * @return ContentEntity
     */
    public ContentEntity getContentEntity(String phone, String send_phone, String context) {
        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setPhone(phone);
        contentEntity.setSend_phone(send_phone);
        contentEntity.setContent(context);
        DateFormat currentTime = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");    //设置时间格式
        contentEntity.setTime(currentTime.format(new Date()));
        return contentEntity;
    }


    public void setRun(boolean run) {
        isRun = run;
    }
}
