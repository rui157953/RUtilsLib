package com.ryan.rutilslib;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.WindowManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by Ryan on 2017/12/7.
 */

public class SystemUtils {

    public static String getIPAddress(Context context){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        int ipAddress = 0;
        if (wifiManager != null) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            ipAddress = wifiInfo.getIpAddress();
            LogUtil.i("ipAddress(int):"+ipAddress);
            return intToIp(ipAddress);
        }
        return null;
    }

    public static String getInetIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    return inetAddress.getHostAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    public static int[] getDisplayDimension(Context context){
        int [] dimension = new int[2];
        WindowManager wm1 = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm1.getDefaultDisplay().getWidth();
        int height = wm1.getDefaultDisplay().getHeight();
        if (width<height){
            dimension[0] = width;
            dimension[1] = height;
        }else {
            dimension[1] = width;
            dimension[0] = height;
        }
        return dimension;
    }
}
