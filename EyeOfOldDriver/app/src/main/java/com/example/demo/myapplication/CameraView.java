package com.example.demo.myapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by HeWenjie on 2016/5/11.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback, android.hardware.Camera.PictureCallback {
    private SurfaceHolder holder;
    private Camera camera;

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

    public void onPictureTaken(byte[] data, Camera camera) { // 拍摄完成后保存照片
        try {
            String path = Environment.getExternalStorageDirectory() + "/formula.png";
            data2file(data, path);
        } catch(Exception e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    private void data2file(byte[] w, String fileName) throws Exception { // 将二进制数据转换成文件
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            out.write(w);
            out.close();
        } catch(Exception e) {
            if(out != null) {
                out.close();
            }
            throw e;
        }
    }
}
