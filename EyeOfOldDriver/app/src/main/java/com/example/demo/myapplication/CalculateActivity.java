package com.example.demo.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.IccOpenLogicalChannelResponse;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by HeWenjie on 2016/6/28.
 */
public class CalculateActivity extends Activity implements View.OnClickListener {

    private ImageView testImage;
    private Button ackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_activity);

        findView();
        bindButton();

        show();
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
        testImage = (ImageView)findViewById(R.id.testImage);
        ackButton = (Button)findViewById(R.id.ackButton);
    }

    private void bindButton() {
        ackButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ackButton:
                /*
                Intent intent = new Intent();
                intent.setClass(CalculateActivity.this, MainActivity.class);
                startActivity(intent);
                */
                this.finish();
                break;
        }
    }

    private void show() {
        String path = Environment.getExternalStorageDirectory() + "/formula.png";
        File mFile = new File(path);

        if(mFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            BitmapProcessing bitmapProcessing = new BitmapProcessing();
            Bitmap result = bitmapProcessing.getScanArea(bitmap);
            testImage.setImageBitmap(result);
            saveMyBitmap(result);
        }
    }

    private void saveMyBitmap(Bitmap bitmap) {
        String path = Environment.getExternalStorageDirectory() + "/result.png";
        File f = new File(path);
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
}
