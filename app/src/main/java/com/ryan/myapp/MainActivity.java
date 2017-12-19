package com.ryan.myapp;

import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ryan.rutilslib.LogUtil;

public class MainActivity extends AppCompatActivity {
    boolean isd = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.d("test");
        LogUtil.toast(MainActivity.this,"MainActivityToast");

    }

    @Override
    protected void onDestroy() {
        isd = false;

        super.onDestroy();
    }
}
