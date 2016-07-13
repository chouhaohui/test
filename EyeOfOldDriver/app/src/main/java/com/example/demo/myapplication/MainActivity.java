package com.example.demo.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import rb.popview.PopField;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button formulaButton;
    private Button equationButton;
    private Button sudokuButton;
    private Data data;

    private PopField mPopField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = (Data)getApplication();
        loadKNN();

        findView();
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
        formulaButton = (Button)findViewById(R.id.formulaButton);
        equationButton = (Button)findViewById(R.id.equationButton);
        sudokuButton = (Button)findViewById(R.id.sudokuButton);
    }

    private void bindButton() {
        formulaButton.setOnClickListener(this);
        equationButton.setOnClickListener(this);
        sudokuButton.setOnClickListener(this);
        mPopField = PopField.attach2Window(this);
    }

    public void onClick(View v) {
        Button button = (Button)findViewById(v.getId());
        /*
        // 保存原Button信息
        Button button = (Button)findViewById(v.getId());
        String text = button.getText().toString();
        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
        ColorStateList textColor = button.getTextColors();
        Drawable background = button.getBackground();
        float textSize = button.getTextSize();
        System.out.println(textSize);
        */

        final Intent intent = new Intent();
        intent.setClass(MainActivity.this, CameraActivity.class);
        Bundle bundle = new Bundle();
        switch(v.getId()) {
            case R.id.formulaButton:
                bundle.putString("MODE", "formula");
                break;
            case R.id.equationButton:
                bundle.putString("MODE", "equation");
                break;
            case R.id.sudokuButton:
                bundle.putString("MODE", "sudoku");
                break;
        }
        intent.putExtras(bundle);

        /*
        // 消失的Button还原
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Button new_btn = new Button(this);
        new_btn.setText(text);
        new_btn.setId(v.getId());
        new_btn.setLayoutParams(layoutParams);
        new_btn.setTextColor(textColor);
        new_btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        new_btn.setBackground(background);
        new_btn.setOnClickListener(this);
        mPopField.popView(button, new_btn, true);
        */
        mPopField.popView(button);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                MainActivity.this.finish();
            }
        }, 500);
    }

    // 提前加载KNN
    private void loadKNN() {
        String[] trainFiles = getTrainingFileName();
        Vector<Vector> dataset = new Vector<>();
        Vector class_name = new Vector();
        for(int i = 0; i < trainFiles.length; i++) {
            Vector vector = string2Vector(readFile(trainFiles[i]));
            Integer label = KNN.get_class(trainFiles[i]);
            dataset.add(vector);
            class_name.add(label);
        }
        data.setDataset(dataset);
        data.setClassNames(class_name);
    }

    public String[] getTrainingFileName() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("trainingset");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    private String readFile(String filename) {
        String string = null;
        try {
            InputStream is = getAssets().open("trainingset/" + filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            string = new String(buffer, "GB2312");
            string = string.replaceAll("\\s", ""); // 去掉原文件中存在的换行符
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

    // 将string转成vector存储
    private Vector string2Vector(String string) {
        Vector vector = new Vector();
        for(int i = 0; i < string.length(); i++) {
            vector.add(string.charAt(i));
        }
        return vector;
    }
}
