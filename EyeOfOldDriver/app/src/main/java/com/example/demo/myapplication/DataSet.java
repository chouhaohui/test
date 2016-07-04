import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.math.*;

public class DataSet {
    private int width;
    private int height;
    private int[][] pixels_init; //原始图片的矩阵
    private int arr[][];
    private int[][] pixels;  //二值化图片的0/1矩阵
    private int[][] dataSet = new int[32][32];  //最终得到的数据集矩阵
    Boolean count_flag;  //用来判断数据的所占的行列比
                         //flag = true  说明行数大于列数
                         //flag = false 说明列数大于行数
    private BufferedImage src;
    public void myRead(String input) throws IOException {
    	try{
    		File file =  new File(input);
    		src = ImageIO.read(file);
    		width = src.getWidth();
    		height = src.getHeight();
//    		System.out.println(width);
//    		System.out.println(height);
    		arr = new int[width][height];
    		pixels_init = new int[width][height];
    		pixels = new int[width][height];
    		pixels = filter(src);
//    		for(int i = 0; i < width; i++){
//                for(int j = 0; j < height; j++){
//                    int rgb = src.getRGB(i, j);
//                    pixels_init[i][j] = rgb;
//                    
//                    //getRGB()返回默认的RGB颜色模型(十进制)
//                	arr[i][j] = getImageRgb(rgb);//该点的灰度值
//                }
//            }
//    		
//    		int FZ=130; //阈值
//    		for (int i = 0; i < width; i++) {
//    			for (int j = 0; j < height; j++) {
//    				if(getGray(arr,i,j,width,height)>FZ){
//    					pixels[i][j] = -1;
//    				}else{
//    					pixels[i][j] = -16777216;
//    				}
//    			}
//    		}
    		
    	} catch (IOException e){
    		e.printStackTrace();
    	}
    }
    
    private static int getImageRgb(int i) {
    	String argb = Integer.toHexString(i);// 将十进制的颜色值转为十六进制
    	// argb分别代表透明,红,绿,蓝 分别占16进制2位
    	int r = Integer.parseInt(argb.substring(2, 4),16);//后面参数为使用进制
    	int g = Integer.parseInt(argb.substring(4, 6),16);
    	int b = Integer.parseInt(argb.substring(6, 8),16);
    	int result=(int)((r+g+b)/3);
    	return result;
    }
    
    //自己加周围8个灰度值再除以9，算出其相对灰度值 
    public static int  getGray(int gray[][], int x, int y, int w, int h){  
    	int rs = gray[x][y]  
    			+ (x == 0 ? 255 : gray[x - 1][y])  
    			+ (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])  
    			+ (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1])  
    			+ (y == 0 ? 255 : gray[x][y - 1])  
    			+ (y == h - 1 ? 255 : gray[x][y + 1])  
    			+ (x == w - 1 ? 255 : gray[x + 1][ y])  
    			+ (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])  
    			+ (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);  
    	return rs / 9;  
    }
    
    
    public int[][] filter(BufferedImage src) { 
        // 图像灰度化  
        int[][] inPixels = new int[width][height];
        int[][] outPixels = new int[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int rgb = src.getRGB(i, j);
                inPixels[i][j] = rgb;
            }
        }
        for(int i = 0; i < width; i++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for(int j = 0; j < height; j++) {
                ta = (inPixels[i][j] >> 24) & 0xff;
                tr = (inPixels[i][j] >> 16) & 0xff;
                tg = (inPixels[i][j] >> 8) & 0xff;
                tb = inPixels[i][j] & 0xff;
                int gray= (int)(0.299 *tr + 0.587*tg + 0.114*tb);
                inPixels[i][j]  = (ta << 24) | (gray << 16) | (gray << 8) | gray;
            }
        }
        // 获取直方图
        int[] histogram = new int[256];
        for(int i = 0; i < width; i++) {
            int tr = 0;
            for(int j = 0; j < height; j++) {
                tr = (inPixels[i][j] >> 16) & 0xff;
                histogram[tr]++;
            }
        }
        // 图像二值化 - OTSU 阈值化方法
        double total = width * height;
        double[] variances = new double[256];
        for(int i=0; i<variances.length; i++)
        {
            double bw = 0;
            double bmeans = 0;
            double bvariance = 0;
            double count = 0;
            for(int t=0; t<i; t++)
            {
                count += histogram[t];
                bmeans += histogram[t] * t;
            }
            bw = count / total;
            bmeans = (count == 0) ? 0 :(bmeans / count);
            for(int t=0; t<i; t++)
            {
                bvariance += (Math.pow((t-bmeans),2) * histogram[t]);
            }
            bvariance = (count == 0) ? 0 : (bvariance / count);
            double fw = 0;
            double fmeans = 0;
            double fvariance = 0;
            count = 0;
            for(int t=i; t<histogram.length; t++)
            {
                count += histogram[t];
                fmeans += histogram[t] * t;
            }
            fw = count / total;
            fmeans = (count == 0) ? 0 : (fmeans / count);
            for(int t=i; t<histogram.length; t++)
            {
                fvariance += (Math.pow((t-fmeans),2) * histogram[t]);
            }
            fvariance = (count == 0) ? 0 : (fvariance / count);
            variances[i] = bw * bvariance + fw * fvariance;
        }

        // find the minimum within class variance
        double min = variances[0];
        int threshold = 0;  //阈值
        for(int m=1; m<variances.length; m++)
        {
            if(min > variances[m]){
                threshold = m;
                min = variances[m];
            }
        }
        // 二值化  
        System.out.println("final threshold value : " + threshold);
        for(int i = 0; i < width; i++) {
        	for(int j = 0; j < height; j++) {
                int gray = (inPixels[i][j] >> 8) & 0xff;
                if(gray > threshold)
                {
                    gray = 255;
                    outPixels[i][j]  = (0xff << 24) | (gray << 16) | (gray << 8) | gray;
                }
                else
                {
                    gray = 0;
                    outPixels[i][j]  = (0xff << 24) | (gray << 16) | (gray << 8) | gray;
                }
            }
        }
        return outPixels;
    }
    
    
    public int[][] dataScaling(int[][] tempArray){
    	int[][] toProcess = processdata(tempArray);
    	int length = toProcess.length;
    	//System.out.println(length);
    	int[][] dataProcess = new int[length][length];
    	if(count_flag){
        	for(int j = 0; j < length; j++){
        		for(int i = length - 1; i >= 0; i--){
        			dataProcess[i][j] = toProcess[j][length-i-1];
        		}
        	}
    	} else {
    		for(int i = length - 1; i >= 0; i--){
        		for(int j = 0; j < length; j++){
        			dataProcess[i][j] = toProcess[length-i-1][j];
        		}
        	}
    	}
    	int size = dataProcess.length;
    	double scale = (double)size / 32.0;
    	for(int x = 0; x < 32; x++){
    		for(int y = 0; y < 32; y++){
    			double srcX = x * scale;
    			int int_x = (int)srcX;
    			double decimal_x = srcX - (double)int_x;
    			double srcY = y * scale;
    			int int_y = (int)srcY;
    			double decimal_y = srcY - (double)int_y;
    			
    			//图像缩放算法
    			int temp1 = 0, temp2 = 0, temp3 = 0, temp4 = 0;
    			if(int_x < 0 || int_x >= size || int_y < 0 || int_y >= size) temp1 = 0;
    			else temp1 = dataProcess[int_x][int_y];
    			if(int_x < 0 || int_x >= size || int_y+1 < 0 || int_y+1 >= size) temp2 = 0;
    			else temp2 = dataProcess[int_x][int_y+1];
    			if(int_x+1 < 0 || int_x+1 >= size || int_y < 0 || int_y >= size) temp3 = 0;
    			else temp3 = dataProcess[int_x+1][int_y];
    			if(int_x+1 < 0 || int_x+1 >= size || int_y+1 < 0 || int_y+1 >= size) temp4 = 0;
    			else temp4 = dataProcess[int_x+1][int_y+1];
    			int result = (int)Math.round((1-decimal_x)*(1-decimal_y)*temp1 + (1-decimal_x)*decimal_y*temp2
    					+ decimal_x*(1-decimal_y)*temp3  + decimal_x*decimal_y*temp4);
    			if(result > -8388608) dataSet[x][y] = -1;
    			else dataSet[x][y] = -16777216;
    		}
    	}

/*缩放后的居中处理*/
    	int set[][] = new int[dataSet.length][dataSet.length];
    	for(int i = 0; i < set.length; i++){
    		for(int j = 0; j < set.length; j++){
    			set[i][j] = dataSet[i][j];
    		}
    	}
    	int col_count = 0;  //记录数据像素所占的列数
    	int start_col = 0;
    	int end_col = 0;
    	boolean flag = false;  //用来判断跳出循环
    	
    	//处理列
    	flag = false;
    	for(int i = 0; i < set.length; i++){
    		for(int j = 0; j < set[0].length; j++){
    			if((set[i][j] & 0x000000ff) == 0){
    				start_col = i;
    				flag = true;
    				break;
    			}
    		}
    		if(flag == true) break;
    	}
    	flag = false;
    	for(int i = set.length - 1; i >= 0; i--){
    		for(int j = 0; j < set[0].length; j++){
    			if((set[i][j] & 0x000000ff) == 0){
    				end_col = i;
    				flag = true;
    				break;
    			}
    		}
    		if(flag == true) break;
    	}
    	col_count = end_col - start_col + 1;
    	int midpoint = dataSet.length / 2;
    	int distance1 = 0;
    	int distance2 = 0;
    	if(col_count % 2 != 0){
    		distance1 = col_count / 2;
    		distance2 = col_count / 2 + 1;
    	} else {
    		distance1 = distance2 = col_count / 2;
    	}
    	for(int i = 0; i < 32; i++){
    		for(int j = 0; j < 32; j++){
    			if((i >= midpoint - distance1) && (i < midpoint + distance2)){
    				dataSet[i][j] = set[start_col+i-(midpoint - distance1)][j];
    			} else {
    				dataSet[i][j] = -1;
    			}
    		}
    	}
/**/
    	return dataProcess;
    }
    
    public void myWrite(String output1, String output2, String output3, int[][] tempArray) throws IOException {
    	String prefix = output1.substring(0, output1.lastIndexOf("/"));
    	int[][] dataProcess = dataScaling(tempArray);

/*recordResult1.txt表示最终得到的0/1矩阵*/
    	BufferedImage des1 = new BufferedImage(32, 32, BufferedImage.TYPE_BYTE_GRAY);
    	for(int i = 0; i < 32; i++){
            for(int j = 0; j < 32; j++){
                int value = dataSet[32-i-1][j];
                des1.setRGB(i, j, value);
            }
        }
    	PrintWriter pw1 = new PrintWriter(prefix+"/recordResult1.txt");
    	for (int j = 0; j < dataSet[0].length; j++) {
    		for (int i  = 0; i < dataSet.length ; i++) {
    			pw1.print(1 - (des1.getRGB(i, j)& 0x000000ff) / 255 + "");
    		}
    		if(j != dataSet[0].length - 1) pw1.println();
    	}
    	pw1.close();
/**/

///*recordResult2.txt表示中间得到的0/1矩阵*/
//    	BufferedImage des2 = new BufferedImage(dataProcess.length, dataProcess[0].length, BufferedImage.TYPE_BYTE_GRAY);
//    	for(int i = 0; i < dataProcess.length; i++){
//            for(int j = 0; j < dataProcess[0].length; j++){
//                int value = dataProcess[dataProcess.length-i-1][j];
//                des2.setRGB(i, j, value);
//                //System.out.println(1 - (des.getRGB(i, j)& 0x000000ff) / 255);
//            }
//        }
//    	PrintWriter pw2 = new PrintWriter(prefix+"/recordResult2.txt");
//    	for (int j = 0; j < dataProcess[0].length; j++) {
//    		for (int i  = 0; i < dataProcess.length ; i++) {
//    			pw2.print(1 - (des2.getRGB(i, j)& 0x000000ff) / 255 + "");
//    		}
//    		if(j != dataProcess[0].length - 1) pw2.println();
//    	}
//    	pw2.close();
///**/
//    	
///*recordResult3.txt表示最初得到的0/1矩阵*/    	
//    	BufferedImage des3 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//    	for(int i = 0; i < width; i++){
//            for(int j = 0; j < height; j++){
//                int value = tempArray[i][j];
//                des3.setRGB(i, j, value);
//                //System.out.println(1 - (des.getRGB(i, j)& 0x000000ff) / 255);
//            }
//        }
//    	PrintWriter pw3 = new PrintWriter(prefix+"/recordResult3.txt");
//    	for (int j = 0; j < tempArray[0].length; j++) {
//    		for (int i  = 0; i < tempArray.length ; i++) {
//    			pw3.print(1 - (des3.getRGB(i, j)& 0x000000ff) / 255 + "");
//    		}
//    		if(j != tempArray[0].length - 1) pw3.println();
//    	}
//    	pw3.close();
///**/

    	try{
            File outFile1 = new File(output1);
            ImageIO.write(des1, "png", outFile1);
//            File outFile2 = new File(output2);
//            ImageIO.write(des2, "png", outFile2);
//            File outFile3 = new File(output3);
//            ImageIO.write(des3, "png", outFile3);
		} catch (IOException e){
			e.printStackTrace();
		}
    }

    public void myWriteArithmetic(String output1, int[][] tempArray, int number) throws IOException {
    	int[][] dataProcess = dataScaling(tempArray);
    	
/*recordResult1.txt表示最终得到分割字符的0/1矩阵*/
    	BufferedImage des1 = new BufferedImage(32, 32, BufferedImage.TYPE_BYTE_GRAY);
    	for(int i = 0; i < 32; i++){
            for(int j = 0; j < 32; j++){
                int value = dataSet[32-i-1][j];
                des1.setRGB(i, j, value);
            }
        }
    	PrintWriter pw1 = new PrintWriter("./output_images/"+number+".txt");
    	for (int j = 0; j < dataSet[0].length; j++) {
    		for (int i  = 0; i < dataSet.length ; i++) {
    			pw1.print(1 - (des1.getRGB(i, j)& 0x000000ff) / 255 + "");
    		}
    		if(j != dataSet[0].length - 1) pw1.println();
    	}
    	pw1.close();
/**/
    	
    	try{
            File outFile1 = new File(output1);
            ImageIO.write(des1, "png", outFile1);
		} catch (IOException e){
			e.printStackTrace();
		}
    }
    
    public int[][] processdata(int[][] _pixels){
    	//System.out.println(pixels[9][11] & 0x000000ff);
    	int[][] dataProcess;  //待处理的数据矩阵
    	int row_count = 0;  //记录数据像素所占的行数
    	int col_count = 0;  //记录数据像素所占的列数
    	int start_row = 0;
    	int end_row = 0;
    	int start_col = 0;
    	int end_col = 0;
    	boolean flag = false;  //用来判断跳出循环
    	
    	//处理行
    	for(int j = 0; j < _pixels[0].length; j++){
    		for(int i = 0; i < _pixels.length; i++){
    			if((_pixels[i][j] & 0x000000ff) == 0){
    				start_row = j;
    				flag = true;
    				break;
    			}
    		}
    		if(flag == true) break;
    	}
    	flag = false;
    	for(int j = _pixels[0].length - 1; j >= 0; j--){
    		for(int i = 0; i < _pixels.length; i++){
    			if((_pixels[i][j] & 0x000000ff) == 0){
    				end_row = j;
    				flag = true;
    				break;
    			}
    		}
    		if(flag == true) break;
    	}
    	
    	//处理列
    	flag = false;
    	for(int i = 0; i < _pixels.length; i++){
    		for(int j = 0; j < _pixels[0].length; j++){
    			if((_pixels[i][j] & 0x000000ff) == 0){
    				start_col = i;
    				flag = true;
    				break;
    			}
    		}
    		if(flag == true) break;
    	}
    	flag = false;
    	for(int i = _pixels.length - 1; i >= 0; i--){
    		for(int j = 0; j < _pixels[0].length; j++){
    			if((_pixels[i][j] & 0x000000ff) == 0){
    				end_col = i;
    				flag = true;
    				break;
    			}
    		}
    		if(flag == true) break;
    	}
    	System.out.println(start_row + " " + end_row + " " + start_col + " " + end_col);
    	row_count = end_row - start_row + 1;
    	col_count = end_col - start_col + 1;
    	int size = 0;
    	if(row_count >= col_count){
    		count_flag = true;
    		size = row_count;
    		System.out.println(size);
    	} else {
    		count_flag = false;
    		size = col_count;
    		System.out.println(size);
    	}
    	dataProcess = new int[size][size];
    	if(count_flag){
        	int midpoint = size / 2;
        	int distance1 = 0;
        	int distance2 = 0;
        	if(col_count % 2 != 0){
        		distance1 = col_count / 2;
        		distance2 = col_count / 2 + 1;
        	} else {
        		distance1 = distance2 = col_count / 2;
        	}
        	for(int j = 0; j < size; j++){
        		for(int i = 0; i < size; i++){
        			if((i >= midpoint - distance1) && (i < midpoint + distance2)){
        				dataProcess[j][i] = _pixels[start_col+i-(midpoint - distance1)][start_row+j];
        			} else {
        				dataProcess[j][i] = -1;
        			}
        		}
        	}
    	} else{
    		int midpoint = size / 2;
        	int distance1 = 0;
        	int distance2 = 0;
        	if(row_count % 2 != 0){
        		distance1 = row_count / 2;
        		distance2 = row_count / 2 + 1;
        	} else {
        		distance1 = distance2 = row_count / 2;
        	}
        	for(int i = 0; i < size; i++){
        		for(int j = 0; j < size; j++){
        			if((j >= midpoint - distance1) && (j < midpoint + distance2)){
        				//dataProcess[j][i] = pixels[start_col+i-(midpoint - distance1)][start_row+j];
        				dataProcess[i][j] = _pixels[start_col+i][start_row+j-(midpoint-distance1)];
        			} else {
        				dataProcess[i][j] = -1;
        			}
        		}
        	}
    	}
    	return dataProcess;
    }
    
    //获取数据集
//    public void getDataSet() throws IOException {
//    	String path = "./Data";
//		File imagefile=new File(path);
//		File[] tempList = imagefile.listFiles();
//		for (int i = 0; i < tempList.length; i++) {
//			if (tempList[i].isDirectory()){
//				String testname = tempList[i].getName();
//				String prefix = "./output_images_" + testname;
//				File file = new File(prefix);
//				file.mkdirs();
//				File[] fileList = tempList[i].listFiles();
//				for(int j = 0; j < fileList.length; j++){
//					if(!fileList[j].isDirectory()){
//						String imgName = fileList[j].getName();
//						String name = imgName.substring(0,imgName.lastIndexOf("."));
//						String _path = prefix + "/" + name;
//						File imgFile = new File(_path);
//						imgFile.mkdirs();
//						myRead("./Data/"+testname+"/"+imgName);
//						myWrite(_path+"/"+name+"_out1.png",_path+"/"+name+"_out2.png",_path+"/"+name+"_out3.png", pixels);
//					}					
//				}
//			}
//		}
//    }
    
    //对列进行分割，得到单个的字符
    public void StringSplit(String input) throws IOException {
    	File file = new File("./output_images");
		file.mkdir();
    	myRead(input);
    	String inputName = input.substring(0,input.lastIndexOf("."));
    	
    /*recordResult3.txt表示最初得到完整图片的0/1矩阵*/
    	BufferedImage des3 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    	for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int value = pixels[i][j];
                des3.setRGB(i, j, value);
                //System.out.println(1 - (des.getRGB(i, j)& 0x000000ff) / 255);
            }
        }
    	PrintWriter pw3 = new PrintWriter("./output_images/recordResult3.txt");
    	for (int j = 0; j < pixels[0].length; j++) {
    		for (int i  = 0; i < pixels.length ; i++) {
    			pw3.print(1 - (des3.getRGB(i, j)& 0x000000ff) / 255 + "");
    		}
    		if(j != pixels[0].length - 1) pw3.println();
    	}
    	pw3.close();
    /**/
    	try{
            File outFile3 = new File("./output_images/recordResult3.png");
            ImageIO.write(des3, "png", outFile3);
		} catch (IOException e){
			e.printStackTrace();
		}
    	
    	int start_col = 0;
    	int index = 0;
    	int number = 1;   //字符计数器
    	while(index < width){
    		int col_num = 0;
    		boolean flag = false;  //用来判断跳出循环
        	//处理列
        	for(int i = start_col; i < width; i++){
        		for(int j = 0; j < height; j++){
        			if((pixels[i][j] & 0x000000ff) == 0){
        				start_col = i;
        				index = i;
        				flag = true;
        				break;
        			}
        			if(j == height - 1) index++;
        		}
        		if(flag == true) break;
        	}
        	if(flag){
        		flag = false;
            	for(int i = start_col; i <width; i++){
            		for(int j = 0; j < height; j++){
            			if((pixels[i][j] & 0x000000ff) == 0){
            				col_num++;
            				break;
            			}
            			if(j == height - 1) flag = true;
            		}
            		if(flag == true) {
            			index = i;
            			break;
            		}
            	}
            	if(col_num > 0){
            		int[][] temp = new int[col_num][height];  //单个字符的0/1矩阵
            		for(int i = 0; i < col_num; i++){
            			for(int j = 0; j < height; j++){
            				temp[i][j] = pixels[start_col+i][j];
            			}
            		}
            		myWriteArithmetic("./output_images/"+inputName+"_out1_"+number+".png", temp, number);
            		number++;
            	}
            	start_col = index;
        	}
    	}
    }
    
    public static void main(String[] args) throws IOException {
    	System.out.println(-1 & 0x000000ff);
    	System.out.println(-16777216 & 0x000000ff);
//    	File file = new File("./output_images");
//		file.mkdir();
    	
//    	DataSet testDataSet = new DataSet();
//    	testDataSet.getDataSet();
    	
    	DataSet testSplit = new DataSet();
    	testSplit.StringSplit("5.png");
    }
}
