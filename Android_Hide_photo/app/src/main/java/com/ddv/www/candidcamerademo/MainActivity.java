package com.ddv.www.candidcamerademo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button startVideo;
    private Button stopVideo;
    private Intent intent;
    private FloatWindowService.myServiceBinder binder;
    private MyServiceConn conn;

    private class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            binder = (FloatWindowService.myServiceBinder) service;
            if (isVedio) {
                binder.startRecord();
            } else {
                binder.stopRecord();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        }

    }

    private static boolean isVedio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(MainActivity.this, FloatWindowService.class);
        startService(intent);

        startVideo = (Button) findViewById(R.id.btn_start_video);
        stopVideo = (Button) findViewById(R.id.btn_stop_video);
        startVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVedio) {
                    Toast.makeText(MainActivity.this, "正在录像中", Toast.LENGTH_LONG).show();
                } else {
                    conn = new MyServiceConn();
                    bindService(intent, conn, BIND_AUTO_CREATE);
                    finish();
                    isVedio = true;
                }
            }
        });
        stopVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVedio = false;
                conn = new MyServiceConn();
                bindService(intent, conn, BIND_AUTO_CREATE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑服务
        if (conn != null) {
            unbindService(conn);
        }
    }
}
