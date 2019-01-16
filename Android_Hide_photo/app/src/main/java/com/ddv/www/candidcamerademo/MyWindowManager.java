package com.ddv.www.candidcamerademo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyWindowManager implements SurfaceHolder.Callback {

    /**
     * 小悬浮窗View的实例
     */
    private FloatWindowSmallView smallWindow;


    /**
     * 小悬浮窗View的参数
     */
    private LayoutParams smallWindowParams;


    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private WindowManager mWindowManager;
    private SurfaceView mSurfaceview;
    private SurfaceHolder mSurfaceHolder;
    private static MediaRecorder mRecorder;
    private static Camera camera;
    private String path;
    private Context mContext;
    private String videoTitle;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    public void createSmallWindow(Context context) {
        mContext = context;
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (smallWindow == null) {
            smallWindow = new FloatWindowSmallView(context);
            if (smallWindowParams == null) {
                smallWindowParams = new LayoutParams();
                smallWindowParams.type = LayoutParams.TYPE_PHONE;
                smallWindowParams.format = PixelFormat.RGBA_8888;
                smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallWindowParams.width = FloatWindowSmallView.viewWidth;
                smallWindowParams.height = FloatWindowSmallView.viewHeight;
                smallWindowParams.x = screenWidth;
                smallWindowParams.y = screenHeight / 2;
            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow, smallWindowParams);

            mSurfaceview = (SurfaceView) smallWindow.findViewById(R.id.percent);
            SurfaceHolder holder = mSurfaceview.getHolder();
            holder.addCallback(this);
            // setType必须设置，要不出错.
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        }
    }

    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public void startVideo() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        if (camera != null) {
            camera.setDisplayOrientation(270);
            camera.unlock();
            mRecorder.setCamera(camera);
        }
        try {
            // 这两项需要放在setOutputFormat之前
            mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            // Set output file format
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            // 这两项需要放在setOutputFormat之后
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

            mRecorder.setVideoSize(640, 480);
//                        mRecorder.setVideoSize(width, height);
            mRecorder.setVideoFrameRate(30);
            mRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
            mRecorder.setOrientationHint(90);
            //设置记录会话的最大持续时间（毫秒）
            mRecorder.setMaxDuration(7200 * 1000);
//                        mRecorder.setMaxDuration(Integer.MAX_VALUE);
            mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

            path = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (path != null) {
                File dir = new File(path + "/DCIM/Camera");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                videoTitle = System.currentTimeMillis() + ".mp4";
                path = dir + "/" + videoTitle;
                mRecorder.setOutputFile(path);
                mRecorder.prepare();
                mRecorder.start();
//                            mBtnStartStop.setText("录制结束");
                Log.i("LogUtils", "path=====>" + path);
            } else {
                Toast.makeText(mContext, "SD卡不存在...", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopVideo() {
        try {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
//                            mBtnStartStop.setText("开始录制视频");
            if (camera != null) {
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
// 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceview = null;
        mSurfaceHolder = null;
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
            Log.i("LogUtils", "surfaceDestroyed release mRecorder");
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }

    }
}
