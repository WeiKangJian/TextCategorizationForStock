package DFAndIGSelect;
import java.io.IOException;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;

class IGInfo{
    String item;
    int noCount;	/*“不相关”类别中出现词项Item的文档数*/
    int posCount;	/*“褒义”类别中出现词项Item的文档数*/
    int negCount;	/*“贬义”类别中出现词项Item的文档数*/
    double igValue;
    
    public IGInfo(String item,int noCount,int posCount, int negCount, double igValue){
        this.item = item;
        this.noCount = noCount;
        this.posCount = posCount;
        this.negCount = negCount;
        this.igValue = igValue;
    }
    public void setItem(String item){
    	this.item = item;
    }   
    public String getItem(){
        return this.item;
    }
    public void setNoCount(int noCount){
    	this.noCount = noCount;
    }
    public int getNoCount(){
        return this.noCount;
    }
    public void setPosCount(int posCount){
    	this.posCount = posCount;
    }
    public int getPosCount(){
        return this.posCount;
    }
    public void setNegCount(int negCount){
    	this.negCount = negCount;
    }
    public int getNegCount(){
        return this.negCount;
    }
    public void setIGValue(double igValue){
    	this.igValue = igValue;
    }
    public double getIGValue(){
        return this.igValue;
    }
}

class ItemIGInfo{
	String item;
	double igValue;
	public void setItem(String item){
    	this.item = item;
    }   
    public String getItem(){
        return this.item;
    }
    public void setIGValue(double igValue){
    	this.igValue = igValue;
    }
    public double getIGValue(){
        return this.igValue;
    }
}
public class IGTermSelect {
	
	/*IG算法*/
	public double IGcompute(double noCount, double posCount, double negCount, double docCount, double not_in, double good, double bad)
	{
		double smooth = 0.001;
		noCount += smooth;
		posCount += smooth;
		negCount += smooth;
		docCount += 6*smooth;
		not_in += 2*smooth;
		good += 2*smooth;
		bad += 2*smooth;
		
		double firstPart;
		double secondPart;
		double thirdPart;
		double result;
		double P_not_in = not_in/docCount;
		double P_good = good/docCount;
		double P_bad = bad/docCount;
		double dfCount = noCount + posCount + negCount;
		double P_dfCount = dfCount/docCount;
		double Pt_dfCount = (docCount-dfCount)/docCount;
		//if(Pt_dfCount == 0){Pt_dfCount = 1;}
		double P_noCount = noCount/dfCount;
		//if(P_noCount == 0){P_noCount = 1;}
		double P_posCount = posCount/dfCount;
		//if(P_posCount == 0){P_posCount = 1;}
		double P_negCount = negCount/dfCount;
		//if(P_negCount == 0){P_negCount = 1;}
		double Pt_noCount = (not_in-noCount)/(docCount-dfCount);
		//if(Pt_noCount == 0){Pt_noCount = 1;}
		double Pt_posCount = (good-posCount)/(docCount-dfCount);
		//if(Pt_posCount == 0){Pt_posCount = 1;}
		double Pt_negCount = (bad-negCount)/(docCount-dfCount);
		//if(Pt_negCount == 0){Pt_negCount = 1;}
				
		firstPart = P_not_in*(Math.log(P_not_in)/Math.log(2)) + P_good*(Math.log(P_good)/Math.log(2)) + P_bad*(Math.log(P_bad)/Math.log(2));  
		secondPart = P_dfCount*(P_noCount*(Math.log(P_noCount)/Math.log(2)) + P_posCount*(Math.log(P_posCount)/Math.log(2)) + P_negCount*(Math.log(P_negCount)/Math.log(2)));      
		thirdPart = Pt_dfCount*(Pt_noCount*(Math.log(Pt_noCount)/Math.log(2)) + Pt_posCount*(Math.log(Pt_posCount)/Math.log(2)) + Pt_negCount*(Math.log(Pt_negCount)/Math.log(2)));
				
	    result = -firstPart + secondPart + thirdPart;
	    
	    return result;
	}
	
	public String[] TermDictionaryMain(String[] InputDocs)throws IOException{
		String row="";
		ArrayList<ArrayList<String>> itsArray = new ArrayList<ArrayList<String>>();
		double not_in = 0;
		double good = 0;
		double bad = 0;
		int not_in_Items = 0;
		int good_Items = 0;
		int bad_Items = 0;
		int topK = 1000;
		//将String[]中的词项逐个分离并存储在ArrayList<ArrayList<String>> itsArray中
		for(int i = 0; i<InputDocs.length; i++) 
		{	
			if(InputDocs[i].length()<3)
				continue;
			ArrayList<String> itsBuf = new ArrayList<String>();
			itsBuf.add(InputDocs[i].substring(0,1));
			row = InputDocs[i].substring(2);
			int mark = 0;
			for(int j = 0 ; j < row.length() ; j++)
			{
				String temp = row.charAt(j)+"";
				if(temp.equals("|"))
				{
					itsBuf.add(row.substring(mark, j));
					mark = j + 1;
				}
			}
			itsBuf.add(row.substring(mark));
			if(InputDocs[i].substring(0,2).equals("0|"))
			{
				not_in = not_in + 1;
				not_in_Items = not_in_Items + itsBuf.size() - 1;
			}
			if(InputDocs[i].substring(0,2).equals("1|"))
			{
				good = good + 1;
				good_Items = good_Items + itsBuf.size() - 1;
			}
			if(InputDocs[i].substring(0,2).equals("2|"))
			{
				bad = bad + 1;
				bad_Items = bad_Items + itsBuf.size() - 1;
			}
			itsArray.add(itsBuf);
		}
		/*for(int text1 = 0; text1 < itsArray.size(); text1++)
		{
			System.out.println(itsArray.get(text1));
		}*/
		
		//获取每个词项针对每一类别的DF
		HashMap<String, IGInfo> DFsList = new HashMap<String, IGInfo>(); 
		ArrayList<String> DFsave = new ArrayList<String>();
		double docCount = itsArray.size();
		for(int i=0; i<itsArray.size(); i++)
		{
			ArrayList<String> DFsSubFileList = itsArray.get(i);
		    ArrayList<String> list = new ArrayList<String>();
		    String txtclass = "";
			for(int j=0; j<DFsSubFileList.size(); j++)
			{
			    if(j == 0)
			    {
			    	txtclass = DFsSubFileList.get(j);
			    	continue;
			    }
			    			    
			    if(!list.contains(DFsSubFileList.get(j)))
		    	{
			    	if(!DFsList.containsKey(DFsSubFileList.get(j)))
				    {
			    		list.add(DFsSubFileList.get(j));
			    		if(txtclass.equals("0"))
				    	{
				    		IGInfo buf = new IGInfo(DFsSubFileList.get(j), 1, 0, 0, 0.0);
				    		DFsList.put(DFsSubFileList.get(j), buf);
				    	}
				    	if(txtclass.equals("1"))
				    	{
				    		IGInfo buf = new IGInfo(DFsSubFileList.get(j), 0, 1, 0, 0.0);
				    		DFsList.put(DFsSubFileList.get(j), buf);
				    	}
				    	if(txtclass.equals("2"))
				    	{
				    		IGInfo buf = new IGInfo(DFsSubFileList.get(j), 0, 0, 1, 0.0);
				    		DFsList.put(DFsSubFileList.get(j), buf);
				    	}
				    	DFsave.add(DFsSubFileList.get(j));//take elements from hashmap
				    }
				    else
				    {
				    	if(txtclass.equals("0"))
				    	{
				    		IGInfo buf = DFsList.get(DFsSubFileList.get(j));
					    	int value = buf.getNoCount();
						    value = value + 1;
						    buf.setNoCount(value);
						    DFsList.put(DFsSubFileList.get(j), buf);
				    	}
				    	if(txtclass.equals("1"))
				    	{
				    		IGInfo buf = DFsList.get(DFsSubFileList.get(j));
					    	int value = buf.getPosCount();
						    value = value + 1;
						    buf.setPosCount(value);
						    DFsList.put(DFsSubFileList.get(j), buf);
				    	}
				    	if(txtclass.equals("2"))
				    	{
				    		IGInfo buf = DFsList.get(DFsSubFileList.get(j));
					    	int value = buf.getNegCount();
						    value = value + 1;
						    buf.setNegCount(value);
						    DFsList.put(DFsSubFileList.get(j), buf);
				    	}
				    }
		    	}
			 }
		}
		//计算每个词项的IG值并排序
		//ArrayList<ItemIGInfo> sortList = new ArrayList<ItemIGInfo>();
		LinkedList<ItemIGInfo> sortList = new LinkedList<ItemIGInfo>();
		for(int k=0; k<DFsave.size(); k++)
		{
			IGInfo buf = DFsList.get(DFsave.get(k));
			double value = IGcompute(buf.getNoCount(),buf.getPosCount(),buf.getNegCount(),docCount,not_in,good,bad);
			buf.setIGValue(value);
		    DFsList.put(DFsave.get(k), buf);
			///*****************
		    System.out.println(buf.getItem()+ ":" + buf.getNoCount()+ ":" + buf.getPosCount()+ ":" + buf.getNegCount() + ":" + buf.getIGValue());
			///********************
			ItemIGInfo buf_1 = new ItemIGInfo();
			buf_1.setItem(DFsave.get(k));
			buf_1.setIGValue(value);
			if(k == 0)
			{
				sortList.add(buf_1);
				continue;
			}
			int sort = k - 1;
			
			while(sort >= 0 && buf_1.getIGValue() > sortList.get(sort).getIGValue())
			{
				sort--;
			}
			sortList.add(sort+1, buf_1);
		}
		
		/*System.out.println("不相关类别词项数 ：" + not_in_Items);
		System.out.println("褒义类别词项数 ：" + good_Items);
		System.out.println("贬义类别词项数 ：" + bad_Items);
		System.out.println("总词项数（无重复） ：" + DFsave.size());*/
		
		//提取被选择的特征词放入String[]中
		topK =1500;
		String[] OutputDocs=new String[topK];
		for(int select = 0; select < topK; select++)
		{
			OutputDocs[select] = sortList.get(select).getItem();
		}
		
		return OutputDocs;
	}
}
