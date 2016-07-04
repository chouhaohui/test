import java.util.*;
import java.io.*;

public class KNN {
    // 所有可能的字符
    private static char all_chars[]  = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '*', '/', '/', '(', ')'};
    private static Vector<Vector> dataset;                       // 储存训练集
    private static Vector<Integer> class_names;                  // 储存训练集对应的label
    private static int k = 3;                                    // 算法参数
    private static Vector<Integer> predictions;                  // 存放结果

    private static class sorting_item {
        public int value;
        public int label;
        public sorting_item(int v, int l) {                      //  排序用结构体
        	value = v;
        	label = l;
        }
    }
    
    private static Vector getFiles(String path){
    	Vector<String> files = new Vector();
        // get file list where the path has   
        File file = new File(path);   
        // get the folder list   
        File[] array = file.listFiles();   
          
        for(int i=0;i<array.length;i++){                          // 获取指定路径下文件名列表
            if(array[i].isFile()){   
                // only take file name   
                //System.out.println(array[i].getName());
                files.add(array[i].getName());
            }else if(array[i].isDirectory()){   
                getFiles(array[i].getPath());   
            }   
        }
        return files;
    }
        
    private static int get_class(String file_name) {
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

    private static void inputDataset() {
    	// 训练集所在的绝对路径(相对路径亦可）
        String filepath = "trainingset";
        Vector<String> filelist;
        filelist = getFiles(filepath);
        
        int filenum = filelist.size();

        FileReader infile = null;
        String filename;
        int a_pixel;
        Vector<Character> temp;
        
        try {
        	
        	for (int i = 0; i < filenum; i++) {                               // 读取训练集
        		
        		temp = new Vector();
                class_names.add(get_class(filelist.get(i)));
                filename = "trainingset/" + filelist.get(i);
                infile = new FileReader(filename);
                
                while ((a_pixel = infile.read()) != -1) {
                	if (a_pixel == 48 || a_pixel == 49)
                		temp.add((char) a_pixel);
                }

                dataset.add(temp);
            }
        	
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(infile != null) {
                try {
                    infile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    
    static class cmp implements Comparator {
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
    
    static void KNN(Vector test) {
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
    	
    	predictions.add(result);
    }

    static String processing() {
    	String filepath = "a_case";
    	Vector<String> testlist;
    	testlist = getFiles(filepath);
    	
    	int testnum = testlist.size();
    	String testname;
    	int a_pixel;
    	Vector<Character> a_test;
    	Vector<Integer> item_id = new Vector();
    	FileReader infile = null;
    	
    	try {
        	
    		item_id = new Vector();
    		for (int i = 0; i < testnum; i++) {
    			a_test = new Vector();
        		item_id.add(get_class(testlist.get(i)));                // 识别a_case文件夹内所需要识别的对象

        		testname = "a_case/" + testlist.get(i);
        		infile = new FileReader(testname);
                
                while ((a_pixel = infile.read()) != -1) {
                	if (a_pixel == 48 || a_pixel == 49)
                		a_test.add((char) a_pixel);
                }
        		
        		KNN(a_test);
        		a_test.clear();
        	}
        	
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(infile != null) {
                try {
                    infile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    	Vector<sorting_item> result = new Vector();
    	for (int i = 0; i < predictions.size(); i++) {
    		sorting_item a_item = new sorting_item(item_id.get(i), predictions.get(i));
    		result.add(a_item);
    	}
    	
    	Comparator my_cmp = new cmp();
    	Collections.sort(result, my_cmp);

    	String result_str = "";
    	for (int i = 0; i < result.size(); i++)
    		result_str += all_chars[result.get(i).label];
    	
    	return result_str;
    }

    public static void main(String[]args) {
    	dataset = new Vector();
    	class_names = new Vector();
    	predictions = new Vector();
    	
    	inputDataset();
    	System.out.println(processing());
    }
}
