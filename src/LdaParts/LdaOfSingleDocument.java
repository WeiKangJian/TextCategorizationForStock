package LdaParts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

public class LdaOfSingleDocument {
    public int mySqrt(int x) {
        int min=0;
        int max =x;
        int mid =(min+max)/2;
        if(x==1)
            return 1;
        while(!(mid*mid==x||(mid*mid<x&&(mid+1)*(mid+1)>x))){
            if(mid*mid<x)
                mid =(mid+max)/2;
            if(mid*mid>x)
                mid =(mid+min)/2;
        }
        return mid;
    }
    private static void test(int a,int b){
    	HashSet<String> h =new HashSet<>();
    }
	public static void main(String[] args) {
//		HashMap<String, Integer> num = null;
//		HashMap<String, Float> nums;
//		num.put("你好", 123);
//		String res ="这里|不光是|一家|餐厅|更是|博物馆|一个|怀念|邓丽君|小姐|地方";
//		String[] a =res.split("\\|");
//		System.out.println(a);
		File f = new File("10crossStockData/10crossStockData/spiderSegment.txt");
		try
        {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String temp;
            int i=1;
            while ((temp = br.readLine()) != null)
            {
        		File add =new File("lda/test"+i+".txt");
        		BufferedWriter bw = new BufferedWriter(new FileWriter(add));
        		bw.write(temp);
        		bw.close();
        		i++;
            }
            br.close();            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }	

	}

}
