package CorpusSpiderAndPreProcess;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InputOutput {
	//读入文档（此文档的每一行代表一个独立的文档），将每一行放入字符串数组中
	public String[] readInput(String inputFileName)
    {
        //将inputFileName中的内容读入到一个字符串数组中
		List<String> ret = new ArrayList<String>();        
        try
        {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName)));
            
            String temp;
            while ((temp = br.readLine()) != null)
            {
                ret.add(temp);
            }
            br.close();            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        String[] fileString=new String[ret.size()];
        return (String[]) ret.toArray(fileString);
    }
	public void writeOutput(String[] outputContent,String outputFileName)
    {
       //将outputContent中的内容写入文件outputFileName中
		File f = new File(outputFileName);
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for(int i=0;i<outputContent.length;i++)
			{
				bw.write(outputContent[i]);
				bw.newLine();				
			}
			bw.close();
		}
		catch (Exception ex)
	    {
	            ex.printStackTrace();
	    }		
    }
}
