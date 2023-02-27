/*
 * @Author: 'yingliangwei' '2958474980@qq.com'
 * @Date: 2023-02-25 15:32:20
 * @LastEditors: 'yingliangwei' '2958474980@qq.com'
 * @LastEditTime: 2023-02-27 10:00:45
 * @FilePath: \smsroot2\src\main\java\com\example\smsroot2\network\TcpClientHandler.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.example.smsroot2.network;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson2.JSONObject;
import com.example.smsroot2.HelloApplication;
import com.example.smsroot2.analysis.AnalysisData;
import com.example.smsroot2.database.SaveList;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class TcpClientHandler extends SimpleChannelInboundHandler<String> {
    TcpClient client;

    public TcpClientHandler(TcpClient b) {
        this.client = b;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        HelloApplication.setNetworkStatus("连接");
        System.out.println("激活时间是：" + new Date());
        System.out.println("链接已经激活");
        SaveList.setContext(ctx);
        ctx.fireChannelActive();
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SaveList.removeLoginALL();
        HelloApplication.setNetworkStatus("断开");
        client.disconnect("channelInactive");
        Thread.sleep(5 * 1000);
        client.connect();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {// 超时事件
            IdleStateEvent idleEvent = (IdleStateEvent) evt;
            if (idleEvent.state() == IdleState.READER_IDLE) {// 读
                // ctx.channel().close();
            } else if (idleEvent.state() == IdleState.WRITER_IDLE) {// 写
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "heartbeat");
                // write heartbeat to server
                ctx.writeAndFlush(jsonObject.toString());
            } else if (idleEvent.state() == IdleState.ALL_IDLE) {// 全部
                System.out.println("全部");
                //ctx.channel().close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws InterruptedException {
        EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                client.disconnect("异常重连");
                client.connect();
            }
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        AnalysisData.analysis(msg);
    }
}
