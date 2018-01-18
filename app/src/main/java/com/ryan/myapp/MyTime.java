package com.ryan.myapp;


import struct.StructClass;
import struct.StructField;

@StructClass
public class MyTime {
    @StructField(order = 0)
    public short nYear;
    @StructField(order = 1)
    public byte nMonth;
    @StructField(order = 2)
    public byte nDay;
    @StructField(order = 3)
    public byte nHour;
    @StructField(order = 4)
    public byte nMin;
    @StructField(order = 5)
    public byte nSec;


    public MyTime(int nYear, int nMonth, int nDay, int nHour, int nMin, int nSec) {
        this.nYear = (short) nYear;
        this.nMonth = intToBytes(nMonth)[0];
        this.nDay = intToBytes(nDay)[0];
        this.nHour = intToBytes(nHour)[0];
        this.nMin = intToBytes(nMin)[0];
        this.nSec = intToBytes(nSec)[0];
    }

    public MyTime() {
    }

    //        public void send(OutputStream outputStream) throws IOException {
//            outputStream.write(shortToBytes(nYear));
//            outputStream.write(nMonth);
//            outputStream.write(nDay);
//            outputStream.write(nHour);
//            outputStream.write(nMin);
//            outputStream.write(nSec);
//        }

    public byte[] getByteArray() {
        byte[] bytes = shortToBytes(nYear);

        byte[] b = new byte[getLen()];
        b[0] = bytes[1];
        b[1] = bytes[0];
        b[2] = nMonth;
        b[3] = nDay;
        b[4] = nHour;
        b[5] = nMin;
        b[6] = nSec;
        return b;
    }

    public int getLen() {
        return 7;
    }

    private short[] intToshorts(int value) {
        short[] src = new short[2];
//        src[3] =  (byte) ((value>>24) & 0xFF);
//        src[2] =  (byte) ((value>>16) & 0xFF);
//        src[1] = (byte) ((value >> 16) & 0xFFFF);
        src[0] = (byte) (value & 0xFFFF);
        return src;
    }

    private byte[] shortToBytes(short value) {
        byte[] src = new byte[2];
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }
    private int shortToInt(short src) {
        int value;
        value = (int) ((src & 0xFFFF))
                | ((src & 0xFFFF)<<16
                /*| ((src[offset+2] & 0x00)<<16)
                | ((src[offset+3] & 0x00)<<24)*/);
        return value;
    }

    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    @Override
    public String toString() {
        return nYear +" "+ byteToInt(nMonth) +" "+byteToInt(nDay)  +" "+byteToInt(nHour)  +" " +byteToInt(nMin) +" "+byteToInt(nSec) +" ";
    }

    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset+1] & 0xFF))
                | ((src[offset] & 0xFF)<<8
                /*| ((src[offset+2] & 0x00)<<16)
                | ((src[offset+3] & 0x00)<<24)*/);
        return value;
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }
}