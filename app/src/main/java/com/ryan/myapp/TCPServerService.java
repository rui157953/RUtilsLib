package com.ryan.myapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;


import com.ryan.rutilslib.LogUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import struct.JavaStruct;

public class TCPServerService extends Service {

    private final String TAG = this.getClass().getSimpleName();

    private boolean mIsServiceDisconnected;

    @Override
    public void onCreate() {
        super.onCreate();
        new TCPServer().run();    //新开一个线程开启 Socket
    }


    private class TCPServer implements Runnable {
        @Override
        public void run() {
            EventLoopGroup bossGroup = new NioEventLoopGroup(); //用于处理服务器端接收客户端连接
            EventLoopGroup workerGroup = new NioEventLoopGroup(); //进行网络通信（读写）
            try {
                ServerBootstrap bootstrap = new ServerBootstrap(); //辅助工具类，用于服务器通道的一系列配置
                bootstrap.group(bossGroup, workerGroup) //绑定两个线程组
                        .channel(NioServerSocketChannel.class) //指定NIO的模式
                        .childHandler(new ChannelInitializer<SocketChannel>() { //配置具体的数据处理方式
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(new ServerHandler());
                            }
                        })
                        /**
                         * 对于ChannelOption.SO_BACKLOG的解释：
                         * 服务器端TCP内核维护有两个队列，我们称之为A、B队列。客户端向服务器端connect时，会发送带有SYN标志的包（第一次握手），服务器端
                         * 接收到客户端发送的SYN时，向客户端发送SYN ACK确认（第二次握手），此时TCP内核模块把客户端连接加入到A队列中，然后服务器接收到
                         * 客户端发送的ACK时（第三次握手），TCP内核模块把客户端连接从A队列移动到B队列，连接完成，应用程序的accept会返回。也就是说accept
                         * 从B队列中取出完成了三次握手的连接。
                         * A队列和B队列的长度之和就是backlog。当A、B队列的长度之和大于ChannelOption.SO_BACKLOG时，新的连接将会被TCP内核拒绝。
                         * 所以，如果backlog过小，可能会出现accept速度跟不上，A、B队列满了，导致新的客户端无法连接。要注意的是，backlog对程序支持的
                         * 连接数并无影响，backlog影响的只是还没有被accept取出的连接
                         */
                        .option(ChannelOption.SO_BACKLOG, 128) //设置TCP缓冲区
                        .option(ChannelOption.SO_SNDBUF, 32 * 1024) //设置发送数据缓冲大小
                        .option(ChannelOption.SO_RCVBUF, 32 * 1024) //设置接受数据缓冲大小
                        .childOption(ChannelOption.SO_KEEPALIVE, true); //保持连接
                ChannelFuture future = bootstrap.bind(8088).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
    }


    public class ServerHandler  extends ChannelInboundHandlerAdapter {


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            ByteBuf buf = (ByteBuf)msg;
            try {
                //do something msg

                byte[] data = new byte[buf.readableBytes()-7];
                ByteBuf copy = buf.copy(6, buf.readableBytes() - 7);
                copy.readBytes(data);
                LogUtil.d(data.length+"");
                for (int i = 0; i < data.length; i++) {
                    System.out.print(data[i]+" ");
                }

//                String request = new String(data, "utf-8");

                HeartBeat heartBeat = new HeartBeat();
                JavaStruct.unpack(heartBeat,data);
                LogUtil.d("Server: " + heartBeat.toString());
                //写给客户端
                String response = "我是反馈的信息";
                ctx.writeAndFlush(Unpooled.copiedBuffer("888".getBytes()));
                //.addListener(ChannelFutureListener.CLOSE);
            } finally {
                buf.release();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}