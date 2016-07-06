package com.example.demo.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by HeWenjie on 2016/5/11.
 */
public class FormulaActivity extends Activity implements View.OnClickListener {

    private CameraView cameraSurfaceView;
    private ImageView takePictureView;
    private LinearLayout rectangleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formula_activity);

        findView();
        setLayout();
        bindButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void findView() {
        cameraSurfaceView = (CameraView)findViewById(R.id.cameraSurfaceView);
        takePictureView = (ImageView)findViewById(R.id.takePicture);
        rectangleLayout = (LinearLayout)findViewById(R.id.rectangleLayout);
    }

    private void setLayout() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { // 当屏幕是竖屏的时候
            GradientDrawable drawable = new GradientDrawable();
            // 获取当前窗口长和宽
            WindowManager windowManager = this.getWindowManager();
            int width = windowManager.getDefaultDisplay().getWidth();
            int height = windowManager.getDefaultDisplay().getHeight();

            // 重新设置Camera窗口的大小（不然效果有一点变形）
            int cameraWidth = width;
            int cameraHeight = (int)(((double)width / 480) * 640);
            ViewGroup.LayoutParams layoutParams = cameraSurfaceView.getLayoutParams();
            layoutParams.height = cameraHeight;
            cameraSurfaceView.setLayoutParams(layoutParams);

            int rectangleWidth = (int)(0.8 * cameraWidth);
            int rectangleHeight = (int)(0.1 * cameraHeight);
            drawable.setSize(rectangleWidth, rectangleHeight);
            drawable.setStroke(10, Color.parseColor("#000000"));
            rectangleLayout.setBackground(drawable);

            // N.LayoutParams中的N是要调整的控件的父类
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)rectangleLayout.getLayoutParams();
            lp.setMargins(0, (int)(0.2 * cameraHeight), 0, 0);


        } else { // 当屏幕是横屏的时候

        }
    }

    private void bindButton() {
        takePictureView.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.takePicture:
                cameraSurfaceView.takePicture();
                // 延时0.2s来保存图片
                try {
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setClass(FormulaActivity.this, CalculateActivity.class);
                startActivity(intent);
                this.finish();
                break;
        }
    }

}
