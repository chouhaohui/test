package com.example.demo.myapplication;

import android.app.Application;

import java.util.Vector;

/**
 * Created by HeWenjie on 2016/7/8.
 */
// 这个类专门用来预测
public class Data extends Application {
    private KNN knn;

    public void onCreate() {
        super.onCreate();
        knn = new KNN();
    }

    public void setDataset(Vector<Vector> dataset) {
        knn.setDataset(dataset);
    }

    public void setClassNames(Vector className) {
        knn.setClassNames(className);
    }

    public char predict(Vector test) {
        return knn.run(test);
    }
}
