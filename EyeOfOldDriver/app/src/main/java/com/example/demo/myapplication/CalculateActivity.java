package com.example.demo.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by HeWenjie on 2016/6/28.
 */
public class CalculateActivity extends Activity {

    private ImageView testImage;

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
    }

    private void bindButton() {

    }

    private void show() {
        String path = Environment.getExternalStorageDirectory() + "/formula.jpg";
        File mFile = new File(path);

        if(mFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            BitmapProcessing bitmapProcessing = new BitmapProcessing();
            Bitmap result = bitmapProcessing.getScanArea(bitmap);
            testImage.setImageBitmap(result);
        }
    }
}
