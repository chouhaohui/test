package handlePhoto;

import java.util.ArrayList;

public class PhotoMessage {
   private ArrayList<int[][]> photolist = new ArrayList<int[][]>();
   private int size = 0;
   
   public PhotoMessage() {}
   
   public int get_size() {
	   return photolist.size();
   }
   
   public void add_photo(int[][] temp) {
	   photolist.add(temp);
   }
   
   public int[][] get_photo(int i) {
	   return photolist.get(i);
   }
}
