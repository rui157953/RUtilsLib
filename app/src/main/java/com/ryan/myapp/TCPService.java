package com.ryan.myapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Time;

import com.ryan.rutilslib.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.Struct;
import java.util.TimeZone;

import struct.JavaStruct;
import struct.StructClass;
import struct.StructException;
import struct.StructField;
import struct.StructPacker;

/**
 * Created by Ryan on 2017/12/12.
 */

public class TCPService extends Service {


    private Socket mClientSocket;
    private OutputStream outputStream;
    private int index = 0;

    /** 心跳检测时间  */
    private static final long HEART_BEAT_RATE = 5 * 1000;

    private boolean mIsDestroy = false;


    @Override
    public void onCreate() {
        super.onCreate();
        new CThread().start();
//        send();

    }
    public void send(){
        Time t=new Time("GMT+8:00");
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month+1;
        int day = t.monthDay;
        int hour = t.hour; // 0-23
        int minute = t.minute;
        int second = t.second;
        HeartBeat heartBeat = new HeartBeat(new MyTime(year,month,day,hour,minute,second),"546854674554112");
        if (outputStream != null) {
            byte[] pack = new byte[0];
            try {
                pack = JavaStruct.pack(heartBeat);
            } catch (StructException e) {
                e.printStackTrace();
            }
//            byte[] byteArray = heartBeat.getByteArray();
            int length = pack.length;
            LogUtil.d("length:"+length);
            for (int i = 0; i < length; i++) {
                LogUtil.d(pack[i]+" ");
            }
            int pakLength  = 6+length+1;
            byte[] toBytes = intToBytes(pakLength);
            byte[] bytes= new byte[pakLength];
            bytes[0] = 0x3c;
            bytes[1] = 0x5B;
            bytes[2] = toBytes[1];
            bytes[3] = toBytes[0];
            bytes[4] = 0x01;
            byte[] bIndex = intToBytes(index);
//            bytes[5] = bIndex[0];
            bytes[5] = 0x00;
            index++;
            int i = 6;
            for (byte sb:pack) {
                bytes[i] = sb;
                i++;
            }
            bytes[i] = 0x3e;
            try {
                outputStream.write(bytes);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        if (mClientSocket !=null){
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class CThread extends Thread{
        @Override
        public void run() {
            Socket socket = null;
            while (socket == null) {
                try {
                    socket = new Socket("47.97.113.246", 2503);
//                    socket = new Socket("localhost", 8088);
                    mClientSocket = socket;
                    outputStream = mClientSocket.getOutputStream();
                    LogUtil.i("socket", "Client connect server");
                    send();
                } catch (IOException e) {
                        e.printStackTrace();
                }
            }
            try {
                InputStream inputStream = socket.getInputStream();

                while (!mIsDestroy) {
                    byte[] b = new byte[1024];
                    int i = -1;
                    while ((i = inputStream.read(b))!=-1){
                        LogUtil.i("socket","resp:"+b.toString());
                    }
                }
                outputStream.close();
                inputStream.close();
                socket.close();
                LogUtil.i("socket","socket close");
                if (mClientSocket !=null){
                    try {
                        mClientSocket.close();
                        mClientSocket=null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     * @param value
     *            要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytes( int value )
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }




}
