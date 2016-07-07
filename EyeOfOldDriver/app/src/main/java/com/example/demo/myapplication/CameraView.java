package com.example.demo.myapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by HeWenjie on 2016/5/11.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback, android.hardware.Camera.PictureCallback {
    private SurfaceHolder holder;
    private Camera camera;
    private String mode;

    public CameraView(Context context, AttributeSet attrs) { // 构造函数
        super(context, attrs);

        holder = getHolder(); // 生成Surface Holder
        holder.addCallback(this);

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // 指定Push Buffer
    }

    public void surfaceCreated(SurfaceHolder holder) { // Surface生成事件的处理
        if(camera == null) {
            camera = Camera.open(); // 摄像头初始化
            try {
                camera.setPreviewDisplay(holder);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { // Surface改变事件的处理
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE); // 自动对焦

        if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
            parameters.setRotation(90);
        } else {
            parameters.set("orientation", "landscape");
            camera.setDisplayOrientation(0);
            parameters.setRotation(0);
        }

        camera.setParameters(parameters);
        camera.startPreview();
        camera.cancelAutoFocus();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void takePicture() {
        camera.takePicture(null, null, this);
    }

    public void takePicture(String mode) {
        takePicture();
        this.mode = mode;
    }

    public void onPictureTaken(byte[] data, Camera camera) { // 拍摄完成后保存照片
        try {
            String path = Environment.getExternalStorageDirectory() + "/result.png";
            data2file(data, path);
        } catch(Exception e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    private void data2file(byte[] w, String fileName) throws Exception { // 将二进制数据转换成文件
        Bitmap bitmap = Byte2Bitmap(w);
        bitmap = getScanArea(bitmap);
        saveMyBitmap(bitmap, fileName);
    }

    public Bitmap Byte2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    private void saveMyBitmap(Bitmap bitmap, String fileName) {
        File f = new File(fileName);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取取景框中的部分
    public Bitmap getScanArea(Bitmap bitmap) {
        Bitmap result = bitmap;

        if (bitmap == null) {
            return null;
        }
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();

        int xTopLeft = (int)(0.1 * bmpWidth);
        int yTopLeft = (int)(0.1 * bmpHeight);

        int width = (int)(0.8 * bmpWidth);
        int height = 0;
        if(mode.equals("formula")) {
            height = (int)(0.1 * bmpHeight);
        } else if (mode.equals("sudoku")) {
            height = width;
        }

        try {
            result = Bitmap.createBitmap(bitmap, xTopLeft, yTopLeft, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
