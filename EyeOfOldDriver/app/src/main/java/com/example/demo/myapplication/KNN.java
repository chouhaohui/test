package com.example.demo.myapplication;

import java.util.*;
import java.io.*;

public class KNN {
    // 所有可能的字符
    private char all_chars[]  = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '*', '/', '/', '(', ')'};
    private Vector<Vector> dataset;                       // 储存训练集
    private Vector<Integer> class_names;                  // 储存训练集对应的label
    private int k = 3;                                    // 算法参数

	public KNN() {}

	public KNN(Vector<Vector> dataset_, Vector class_names_) {
		dataset = dataset_;
		class_names = class_names_;
	}

    private class sorting_item {
        public int value;
        public int label;
        public sorting_item(int v, int l) {                      //  排序用结构体
        	value = v;
        	label = l;
        }
    }

    public static int get_class(String file_name) {
    	int result = 0;
        int len = file_name.length();
        for (int i = 0; i < len; i++) {
            if (file_name.charAt(i) <= '9' && file_name.charAt(i) >= '0') {       // 根据文件名判断字符类别
                result = result * 10 + (file_name.charAt(i) - '0');
            } else {
                break;
            }
        }
        return result;
    }

	private class cmp implements Comparator {
	    public int compare(Object o1, Object o2) {
		    sorting_item e1=(sorting_item)o1;
		    sorting_item e2=(sorting_item)o2;
		
		    if(e1.value > e2.value) {
		       return 1;                                    // cmp
		    } else if (e1.value < e2.value) {
		       return -1;
		    } else {
		       return 0;
		    }
	    }
    }

    public char run(Vector test) {
    	Vector<sorting_item> distances = new Vector();
    	int dataset_size = dataset.size();
    	int dis = 0;
    	
    	for (int i = 0; i < dataset_size; i++) {
    		
    		for (int j = 0; j < 1024; j++)
    			if (test.get(j) != dataset.get(i).get(j))
    				dis++;                                      // calculate the distance
    		
    		sorting_item temp = new sorting_item(dis, class_names.get(i));
    		
    		distances.add(temp);
    		dis = 0;
    	}
    	
    	Comparator my_cmp = new cmp();
    	Collections.sort(distances, my_cmp);
    	
    	int probable_class_names[] = new int[18];
    	for (int i = 0; i < 18; i++)
    		probable_class_names[i] = 0;
    	
    	int a_class_name;
    	int max = 0;
    	int result = 0;
    	for (int i = 0; i < k; i++) {
    		a_class_name = distances.get(i).label;
    		probable_class_names[a_class_name]++;
    	}

    	for (int i = 0; i < 18; i++) {
    		if (probable_class_names[i] > max) {
    			max = probable_class_names[i];
    			result = i;
    		}
    	}

		return all_chars[result];
    }

	public void setDataset(Vector<Vector> dataset_) {
		dataset = dataset_;
	}

	public void setClassNames(Vector class_names_) {
		class_names = class_names_;
	}
}
