package com.example.demo.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 * Created by HeWenjie on 2016/7/7.
 */
public class BitmapProcess {

    public BitmapProcess() {

    }

    // 轮廓检测
    public ArrayList<MatOfPoint> mFindContours(Bitmap bitmap) {
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        // 转成灰度并模糊化降噪
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(mat, mat, new Size(3, 3));

        // Canny检测边缘
        Imgproc.Canny(mat, mat, 50, 200);


        // 膨胀处理
        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));
        Imgproc.dilate(mat, mat, dilateElement);


        // 寻找轮廓
        ArrayList<MatOfPoint> contours = new ArrayList<>();

        Mat hierarchy = new Mat();
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        return contours;
    }

    // 获取轮廓的外接矩形
    public ArrayList<Bitmap> mBoundRect(Bitmap bitmap) {
        ArrayList<MatOfPoint> contours = mFindContours(bitmap);
        ArrayList<Bitmap> boundrects = new ArrayList<>();

        // 二值化
        bitmap = binarization(bitmap);

        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        int minWidth = 0x4f4f4f;
        ArrayList<Rect> rectList = new ArrayList<>();
        for(int i = 0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            rectList.add(rect);
            if(rect.width < minWidth) minWidth = rect.width;
        }

        // 调整顺序
        rectList = adjustment(rectList);

        for(int i = 0; i < rectList.size(); i++) {
            Rect rect = rectList.get(i);
            Bitmap result = Bitmap.createBitmap(bitmap, (int)rect.tl().x, (int)rect.tl().y,
                    (int)(rect.br().x - rect.tl().x), (int)(rect.br().y - rect.tl().y));
            boundrects.add(result);
        }
        // 切割处理，将被分在同一块的字符切割
        //boundrects = bitmapSplit(boundrects, minWidth);
        return boundrects;
    }

    // 调整切割字符顺序的函数
    private ArrayList<Rect> adjustment(ArrayList<Rect> rectList) {
        ArrayList<Rect> rectArrayList = new ArrayList<>();
        int length = rectList.size();
        for(int i = 0; i < length; i++) {
            int index = getMinIndex(rectList);
            rectArrayList.add(rectList.get(index));
            rectList.remove(index);
        }

        return rectArrayList;
    }

    private int getMinIndex(ArrayList<Rect> rectList) {
        int index = 0;
        int min = 0x4f4f4f;
        for(int i = 0; i < rectList.size(); i++) {
            if(min > rectList.get(i).x) {
                index = i;
                min = rectList.get(i).x;
            }
        }
        return index;
    }

    // 二值化
    public Bitmap binarization(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int area = width * height;
        int gray[][] = new int[width][height];
        int average = 0;// 灰度平均值
        int graysum = 0;
        int graymean = 0;
        int grayfrontmean = 0;
        int graybackmean = 0;
        int pixelGray;
        int front = 0;
        int back = 0;
        int[] pix = new int[width * height];
        img.getPixels(pix, 0, width, 0, 0, width, height);
        for (int i = 1; i < width; i++) { // 不算边界行和列，为避免越界
            for (int j = 1; j < height; j++) {
                int x = j * width + i;
                int r = (pix[x] >> 16) & 0xff;
                int g = (pix[x] >> 8) & 0xff;
                int b = pix[x] & 0xff;
                pixelGray = (int) (0.3 * r + 0.59 * g + 0.11 * b);// 计算每个坐标点的灰度
                gray[i][j] = (pixelGray << 16) + (pixelGray << 8) + (pixelGray);
                graysum += pixelGray;
            }
        }
        graymean = (int) (graysum / area);// 整个图的灰度平均值
        average = graymean;
        for (int i = 0; i < width; i++) // 计算整个图的二值化阈值
        {
            for (int j = 0; j < height; j++) {
                if (((gray[i][j]) & (0x0000ff)) < graymean) {
                    graybackmean += ((gray[i][j]) & (0x0000ff));
                    back++;
                } else {
                    grayfrontmean += ((gray[i][j]) & (0x0000ff));
                    front++;
                }
            }
        }
        if(front == 0 || back == 0) return img;
        int frontvalue = (int) (grayfrontmean / front);// 前景中心
        int backvalue = (int) (graybackmean / back);// 背景中心
        float G[] = new float[frontvalue - backvalue + 1];// 方差数组
        int s = 0;
        for (int i1 = backvalue; i1 < frontvalue + 1; i1++)// 以前景中心和背景中心为区间采用大津法算法（OTSU算法）
        {
            back = 0;
            front = 0;
            grayfrontmean = 0;
            graybackmean = 0;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (((gray[i][j]) & (0x0000ff)) < (i1 + 1)) {
                        graybackmean += ((gray[i][j]) & (0x0000ff));
                        back++;
                    } else {
                        grayfrontmean += ((gray[i][j]) & (0x0000ff));
                        front++;
                    }
                }
            }
            grayfrontmean = (int) (grayfrontmean / front);
            graybackmean = (int) (graybackmean / back);
            G[s] = (((float) back / area) * (graybackmean - average)
                    * (graybackmean - average) + ((float) front / area)
                    * (grayfrontmean - average) * (grayfrontmean - average));
            s++;
        }
        float max = G[0];
        int index = 0;
        for (int i = 1; i < frontvalue - backvalue + 1; i++) {
            if (max < G[i]) {
                max = G[i];
                index = i;
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int in = j * width + i;
                if (((gray[i][j]) & (0x0000ff)) < (index + backvalue)) {
                    pix[in] = Color.rgb(0, 0, 0);
                } else {
                    pix[in] = Color.rgb(255, 255, 255);
                }
            }
        }

        Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        temp.setPixels(pix, 0, width, 0, 0, width, height);
        return temp;
    }

    public ArrayList<Vector> allBitmap2Matrix(ArrayList<Bitmap> boundrects) {
        ArrayList<Vector> arrayList = new ArrayList<>();
        for(int i = 0; i < boundrects.size(); i++) {
            Bitmap bitmap = bitmap2SquareBitmap(boundrects.get(i));
            Vector matrix = bitmap2Matrix(bitmap);

            System.out.println("---------");
            for(int a = 0; a < 32; a ++) {
                String s = "";
                for(int b = 0; b < 32; b++) {
                    s += matrix.get(a * 32 + b);
                }
                System.out.println(s);
            }
            System.out.println("---------");

            arrayList.add(matrix);
        }
        return arrayList;
    }

    // 将bitmap转换成32*32的Bitmap
    public Bitmap bitmap2SquareBitmap(Bitmap bitmap) {
        // 腐蚀处理
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));
        Imgproc.erode(mat, mat, erodeElement);
        Utils.matToBitmap(mat, bitmap);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 选取长宽较大的数值作为新的边长
        int sideLength = width > height ? width : height;

        // 初始化newBitmap的颜色
        int []colors = new int[sideLength * sideLength];
        for(int i = 0; i < sideLength * sideLength; i++) {
            colors[i] = Color.rgb(255, 255, 255);
        }
        // 创建新的bitmap
        Bitmap newBitmap = Bitmap.createBitmap(colors, sideLength, sideLength, bitmap.getConfig()).copy(bitmap.getConfig(), true);
        Canvas canvas = new Canvas(newBitmap);
        // 将图片居中
        int x_shift = (sideLength - width) / 2;
        int y_shift = (sideLength - height) / 2;

        canvas.drawBitmap(bitmap, x_shift, y_shift, null);

        return newBitmap;
    }

    // 将Bitmap转成32*32矩阵
    public Vector bitmap2Matrix(Bitmap bitmap) {
        // 先将Bitmap转换成32*32的bitmap
        bitmap = zoom(bitmap);

        Vector matrix = new Vector();
        for(int j = 0; j < bitmap.getHeight(); j++) {
            for(int i = 0; i < bitmap.getWidth(); i++) {
                if(bitmap.getPixel(i, j) == -1) {
                    matrix.add('0'); // 像素点是白色，矩阵点为0
                } else {
                    matrix.add('1'); // 像素点是黑色，矩阵点为1
                }
            }
        }
        return matrix;
    }

    // Bitmap缩放到32*32Bitmap
    private Bitmap zoom(Bitmap bitmap) {
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.resize(mat, mat, new Size(32, 32));
        Bitmap resizeBitmap = Bitmap.createBitmap(32, 32, bitmap.getConfig());
        Utils.matToBitmap(mat, resizeBitmap);
        return resizeBitmap;
    }
}
