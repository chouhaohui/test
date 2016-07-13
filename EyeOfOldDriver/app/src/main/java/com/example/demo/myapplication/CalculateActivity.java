package com.example.demo.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import rb.popview.PopField;

/**
 * Created by HeWenjie on 2016/6/28.
 */
public class CalculateActivity extends Activity implements View.OnClickListener {

    private String TAG = "CalculateActivity";
    private Button ackButton;
    private Button retryButton;
    private TextView formula;
    private TextView answerText;
    private LinearLayout waitLayout;

    private ArrayList<Bitmap> boundrects;

    private Data data;

    private PopField mPopField;
    private ProgressBar progressBar;

    private String expression;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bitmapSplit();
                            progressBar.setVisibility(View.INVISIBLE);
                            waitLayout.setVisibility(View.INVISIBLE);
                        }
                    }, 500);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_activity);

        data = (Data)getApplication();
        findView();
        bindButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
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
        ackButton = (Button) findViewById(R.id.ackButton);
        retryButton = (Button) findViewById(R.id.retryButton);
        formula = (TextView)findViewById(R.id.formula);
        answerText = (TextView)findViewById(R.id.answerText);
        mPopField = PopField.attach2Window(this);
        progressBar = (ProgressBar)findViewById(R.id.waitbar);
        waitLayout = (LinearLayout)findViewById(R.id.waitLayout);
    }

    private void bindButton() {
        ackButton.setOnClickListener(this);
        retryButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ackButton:
                Calculator calculator = new Calculator();
                String answer = calculator.process_cal(expression);
                if (answer == null) {
                    answerText.setText("运算表达式有误，请重新扫描");
                } else {
                    answerText.setText(String.valueOf(answer));
                }
                mPopField.popView(formula);
                break;
            case R.id.retryButton:
                Intent intent = new Intent();
                intent.setClass(CalculateActivity.this, CameraActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("MODE", this.getIntent().getExtras().getString("MODE"));
                intent.putExtras(bundle);
                startActivity(intent);
                this.finish();
                break;
        }
    }

    private void bitmapSplit() {
        String path = Environment.getExternalStorageDirectory() + "/result.png";
        File mFile = new File(path);

        if (mFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);

            BitmapProcess bitmapProcess = new BitmapProcess();
            boundrects = bitmapProcess.mBoundRect(bitmap);
            ArrayList<Vector> arrayList = bitmapProcess.allBitmap2Matrix(boundrects);
            recognition(arrayList);
        }
    }

    // 识别
    public void recognition(ArrayList<Vector> arrayList) {
        String formulaText = "";
        for(int i = 0; i < arrayList.size(); i++) {
            formulaText += data.predict(arrayList.get(i));
        }
        formula.setText(formulaText);

        if(!formulaText.endsWith("=")) {
            formulaText += "=";
        }

        expression = formulaText;
    }
}