package com.example.demo.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button formulaButton;
    private Button sudokuButton;
    private Data data;

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
        sudokuButton = (Button)findViewById(R.id.sudokuButton);
    }

    private void bindButton() {
        formulaButton.setOnClickListener(this);
        sudokuButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, CameraActivity.class);
        Bundle bundle = new Bundle();
        switch(v.getId()) {
            case R.id.formulaButton:
                bundle.putString("MODE", "formula");
                break;
            case R.id.sudokuButton:
                bundle.putString("MODE", "sudoku");
        }
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
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
