package com.example.demo.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by HeWenjie on 2016/7/13.
 */
public class EquationActivity extends Activity implements View.OnClickListener, EquationAdapter.Callback {
    private ProgressBar progressBar;
    private LinearLayout waitLayout;
    private Data data;
    private Button addButton;
    private Button ackButton;
    private Button retryButton;
    private Button returnButton;
    private TextView ansText;
    private ArrayList<Bitmap> boundrects;
    private List<String> equationList;
    private ListView mList;
    private EquationAdapter equationAdapter;
    private boolean flag = false;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
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
        setContentView(R.layout.equation_activity);
        data = (Data)getApplication();
        findView();
        bindButton();
        newAllocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            if(!flag) {
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
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

    private void newAllocation() {
        equationList = new ArrayList<String>();
        Bundle bundle = this.getIntent().getExtras();
        String[] mString = bundle.getStringArray("EQUATIONLIST");
        if(mString != null) {
            for(int i = 0; i < mString.length; i++) {
                equationList.add(mString[i]);
            }
        }
        equationAdapter = new EquationAdapter(this, equationList, this);
        mList.setAdapter(equationAdapter);
    }

    private void findView() {
        ackButton = (Button)findViewById(R.id.ackButton);
        addButton = (Button)findViewById(R.id.addButton);
        retryButton = (Button)findViewById(R.id.retryButton);
        returnButton = (Button)findViewById(R.id.returnButton);
        progressBar = (ProgressBar)findViewById(R.id.waitbar);
        waitLayout = (LinearLayout)findViewById(R.id.waitLayout);
        mList = (ListView)findViewById(R.id.equationList);
        ansText = (TextView)findViewById(R.id.ans);
    }

    private void bindButton() {
        ackButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        retryButton.setOnClickListener(this);
        returnButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        DecimalFormat df = new DecimalFormat();
        switch (v.getId()) {
            case R.id.ackButton:
                EquationCalcaulator equationCalcaulator = new EquationCalcaulator();
                String[] equationStrings = new String[equationList.size()];
                for(int i = 0; i < equationList.size(); i++) {
                    equationStrings[i] = equationList.get(i);
                }
                double[] ans = equationCalcaulator.solution(equationStrings);
                String ansString = "";
                if (ans == null) {
                    ansString = "方程组无唯一解";
                } else {
                    ansString = "x = " + df.format(ans[0]) + ", y = " + df.format(ans[1]);
                }
                ansText.setText(ansString);
                break;
            case R.id.retryButton:
                Intent intent = new Intent();
                intent.setClass(EquationActivity.this, CameraActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("MODE", this.getIntent().getExtras().getString("MODE"));
                intent.putExtras(bundle);
                startActivity(intent);
                this.finish();
                break;
            case R.id.addButton:
                Bundle bundle1 = new Bundle();
                String[] mString = new String[equationList.size()];
                for(int i = 0; i < equationList.size(); i++) {
                    mString[i] = equationList.get(i);
                }
                bundle1.putStringArray("EQUATIONLIST", mString);
                bundle1.putString("MODE", this.getIntent().getExtras().getString("MODE"));
                Intent intent1 = new Intent();
                intent1.setClass(EquationActivity.this, CameraActivity.class);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                this.finish();
                break;
            case R.id.returnButton:
                Intent intent2 = new Intent();
                intent2.setClass(EquationActivity.this, MainActivity.class);
                startActivity(intent2);
                this.finish();
                break;
        }
    }

    private void bitmapSplit() {
        flag = true;
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
        if(arrayList.size() == 0) {
            ansText.setText("没有方程式，请重新扫描");
            return;
        }
        for(int i = 0; i < arrayList.size(); i++) {
            formulaText += data.predict(arrayList.get(i));
        }

        formulaText = findEqualsign(formulaText);

        equationList.add(formulaText);
        equationAdapter.notifyDataSetChanged();
    }

    // 找到expression中的等号
    private String findEqualsign(String expression) {
        if(expression.length() < 1) return null;
        char[] equation = new char[expression.length() - 1];
        int iter = 0;
        boolean flag = false; // 减号的标记
        for(int i = 0; i < expression.length(); i++) {
            if(expression.charAt(i) == '-') {
                if(!flag) {
                    flag = true;
                } else {
                    equation[iter] = '=';
                    iter++;
                    flag = false;
                }
            } else {
                if(flag) {
                    equation[iter] = '-';
                    iter++;
                }
                equation[iter] = expression.charAt(i);
                flag = false;
                iter++;
            }
        }
        return String.copyValueOf(equation);
    }

    public void click(View v) {
        int position = (Integer)v.getTag();
        equationList.remove(position);
        equationAdapter.notifyDataSetChanged();
    }
}
