package com.ddv.www.candidcamerademo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class FloatWindowService extends Service {


    private MyWindowManager myWindowManager;

    @Override
    public IBinder onBind(Intent intent) {
        return new myServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myWindowManager = new MyWindowManager();
        createWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void createWindow() {
        // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。
        myWindowManager.removeSmallWindow(getApplicationContext());
        myWindowManager.createSmallWindow(getApplicationContext());

    }


    public class myServiceBinder extends Binder {
        public void startRecord() {
            myWindowManager.startVideo();
        }

        public void stopRecord() {
            myWindowManager.stopVideo();
        }
    }
}
