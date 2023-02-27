package com.example.smsroot2.network;

import com.alibaba.fastjson2.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TcpClient {
    private Channel socketChannel;
    private Bootstrap eventLoopGroup;
    private NioEventLoopGroup group;
    public String HOST = "sadqwdasinf.info";
    public int PORT = 809;
    private final ScheduledExecutorService executor;

    public TcpClient(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    // 初始化 `Bootstrap`
    public Bootstrap getBootstrap() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) {

                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast(new LengthFieldPrepender(4));
                pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));
                pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
                pipeline.addLast(new TcpClientHandler(TcpClient.this));
            }
        });
        b.option(ChannelOption.SO_KEEPALIVE, true);
        return b;
    }


    // 异步长连接
    public synchronized void connect() {
        eventLoopGroup = getBootstrap();
        try {
            ChannelFuture future = eventLoopGroup.connect(HOST, PORT).sync();
            socketChannel = future.channel();
            // 等待加载完
            if (future.isSuccess()) {
                System.out.println("connect server successfully");
                // 登录
                socketChannel.writeAndFlush(Login()).sync();
            }
            socketChannel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.fillInStackTrace();
        } finally {
            // 所有资源释放完之后，清空资源，再次发起重连操作
            executor.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    disconnect("断开重连");
                    // 发起重连操作
                    connect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // 断开连接
    public void disconnect(String where) {
        System.out.println("nettyDisconnect"+"disconnect " + where);
        try {
            if (socketChannel != null) {
                socketChannel.close();
                socketChannel = null;
            }
            if (group != null) {
                group.shutdownGracefully();
                group = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String Login() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "root");
            jsonObject.put("root", "login");
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return jsonObject.toString();
    }
}