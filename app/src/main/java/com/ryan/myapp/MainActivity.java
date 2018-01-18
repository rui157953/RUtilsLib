package com.ryan.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ryan.rutilslib.LogUtil;

import java.io.DataOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    boolean isd = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.d("test");
        LogUtil.toast(MainActivity.this,"MainActivityToast");
        startService(new Intent(this,TCPServerService.class));
    }

    @Override
    protected void onDestroy() {
        isd = false;

        super.onDestroy();
    }

    public void input(View view) {
//        if(execRootCmdSilent()!=-1){
//            Log.d("remount", "success");
//            Toast.makeText(MainActivity.this, "remount success", Toast.LENGTH_LONG).show();
//            finish();
//            System.exit(0);
//        }else {
//            Log.d("remount", "error");
//            Toast.makeText(MainActivity.this, "remount error", Toast.LENGTH_LONG).show();
//        }
        startService(new Intent(this,TCPService.class));
    }

    protected static int execRootCmdSilent() {
        try {
            Process localProcess = Runtime.getRuntime().exec("su");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(
                    (OutputStream) localObject);
            String str = String.valueOf("stop adbd");
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            str = String.valueOf("start adbd");
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            int value = localProcess.exitValue();
            return value;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return -1;
    }
}
