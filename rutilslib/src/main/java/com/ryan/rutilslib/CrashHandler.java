package com.ryan.rutilslib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ryan on 2017/12/4.
 */

public class CrashHandler implements UncaughtExceptionHandler {

    private static final boolean DEBUG = true;
    private static final String PATH = Environment.getExternalStorageDirectory().getPath()+"/Crash/log/";
    private static final String FILENAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".log";
    private static CrashHandler sInstance = new CrashHandler();
    private Context mContext;
    private UncaughtExceptionHandler mDefaultCrashHandler;


    private CrashHandler(){

    }

    public static CrashHandler getInstance(){
        return sInstance;
    }

    public void init(Context context){
        this.mContext = context.getApplicationContext();
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        dumpExceptionToSDCard(e);//导出到SD卡
        uploadExceptionToServer(e); //上传到服务器


        e.printStackTrace();

        if (mDefaultCrashHandler !=null){
            mDefaultCrashHandler.uncaughtException(t, e);
        }else {
            Process.killProcess(Process.myPid());
        }

    }

    private void dumpExceptionToSDCard(Throwable e) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            if (DEBUG){
                LogUtil.w("sdcard unmounted, skip dump exception");
                return;
            }
        }

        File dir = new File(PATH);

        if (!dir.exists()){
            boolean mkdirs = dir.mkdirs();
        }

        long currentTime = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(currentTime));

        File file = new File(PATH+FILENAME+time+FILE_NAME_SUFFIX);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            e.printStackTrace(pw);
            pw.close();
        } catch (Exception e1) {
            LogUtil.e("dump crash info failed");
        }

    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(),PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(packageInfo.versionName);
        pw.print("_");
        pw.println(packageInfo.versionCode);

        pw.print("Android Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        pw.print("Model: ");
        pw.println(Build.MODEL);

        pw.print("CPU ABIs: ");

        if (Build.VERSION.SDK_INT >= 21){
            String[] supportedAbis = Build.SUPPORTED_ABIS;
            StringBuilder  sb = new StringBuilder ();
            for (String s: supportedAbis) {
                sb.append(s).append("\n");
            }
            pw.print(sb.toString());
        }else {
            pw.println(Build.CPU_ABI);
        }
    }

    private void uploadExceptionToServer(Throwable e) {

    }
}
