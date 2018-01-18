package com.ryan.myapp;

import java.io.Serializable;

import struct.StructClass;
import struct.StructField;

@StructClass
public class HeartBeat implements Serializable {
    @StructField(order = 0)
    public MyTime time;
    @StructField(order = 1)
    public byte[] imei = new byte[15];

    public HeartBeat(MyTime time, String imei) {
        this.time = time;
        this.imei = imei.getBytes();
    }

    public HeartBeat() {

    }

//        public void send(OutputStream outputStream) throws IOException {
//            time.send(outputStream);
//            outputStream.write(imei);
//        }

    public int getLen() {
        return time.getLen() + 15;
    }

    public byte[] getByteArray() {
        byte[] bytes = time.getByteArray();

        byte[] data3 = new byte[bytes.length + imei.length];
        System.arraycopy(bytes, 0, data3, 0, bytes.length);
        System.arraycopy(imei, 0, data3, bytes.length, imei.length);

        return data3;
    }

    @Override
    public String toString() {
        return time.toString()+new String(imei);
    }
}