package com.example.eyeofolddriver;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class Controller {
   int state = 0;  //state stand for the function
   private Controller() {}  
   
   //single exemple
   private static Controller controller = null;  
   public static Controller getInstance() {  
        if (controller == null) {    
            controller = new Controller();  
        }    
       return controller;  
   }  
   
   public void set_state(int s) {
	   state = s;
   }
   
   public String handle(String url) {
	   if (state == 1) {
	       /*输入图片地址，输入一个PhotoMessage对象*/
	       /*输入photoMessage对象，输入字符串*/
	       /*输入字符串，输出结果*/
	       String result = "";
	       return result;
	   }
	   return "	选择功能按钮 ";
   }
}
