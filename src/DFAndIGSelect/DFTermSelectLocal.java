package DFAndIGSelect;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;


public class DFTermSelectLocal {
//private Dictionary wordsIndex=new Hashtable();//第1类使用的特征词典
//private Dictionary wordsIndex1=new Hashtable();//第2类使用的特征词典
	
	//2选择使用特征选择方法建立特征词典放在另一个String[]中
    //public String[] TermDictionaryMain(String[] initTerms){
    	public String[] TermDictionaryMain(String[] initTerms){	
		////////
    	//依次读入每一个类别
    	System.out.println("训练集的大小："+initTerms.length);
    	String label=initTerms[0].substring(0, 1);
    	System.out.println(label);
    	List<String> allUniques = new ArrayList<String>(); 
    	//用于存放每个类别的不重复的词个数，每个类别表示是0，1，2...
    	ArrayList<Integer> perLabelTermsNum=new ArrayList<Integer>();
    	int startTermsNum=0;//记录原始的词个数
    	int i=0;
    	while (i<initTerms.length)
    	{	
    		//将各类的每个词保证不重复的读入到一个list中		
			List<String> uniques = new ArrayList<String>(); 
			String r="([|])";//每行代表的文档原始格式是：类别|词1|词2|...
			
    		//对于同属于一个类，将其所有的词去重 放入uniques 
			System.out.println("类别："+initTerms[i].substring(0, 1));
    		while (label.compareTo(initTerms[i].substring(0, 1))==0)
    		{  		    			
    				String[] terms = initTerms[i].split(r);
    				startTermsNum+=terms.length;
    				for (int j=1; j < terms.length; j++)
    					if (!uniques.contains(terms[j]))				
    						uniques.add(terms[j]) ;	    			
    			
    				if(i<initTerms.length-1)
    					i++;
    				else
    					break;
    		}
    		allUniques.addAll(uniques);//将每个类的不重复的词统一加入到一个list中（allUniques）
    		perLabelTermsNum.add(uniques.size());//同时记录各个类中的词个数，以便于按类别取词
    		//继续取下一个类别的词
    		if(i<initTerms.length-1)
    			label=initTerms[i].substring(0, 1);	
    		else 
    			break;
    			
    	}
    	System.out.println("all labels:"+perLabelTermsNum.size());
    	System.out.println("startNumterms:"+startTermsNum);
    	System.out.println("endNumterms:"+allUniques.size());
    	int m=0; 
    	int start=0;//各类词的开始位置
    	int k=0;//文档顺序
    	
    	int[] docFreq=new int[allUniques.size()]; //记录所有词的文档频率，根据其所处位置可以确定所属类 
    	int termIndex;//docFreq[termIndex]
    	while(m<perLabelTermsNum.size()){
    		int termsNum=perLabelTermsNum.get(m);
    		System.out.println(m+":"+termsNum);
    		//统计各类中的词在所属类中的DF(即局部DF)
    		//首先将一个类的所有不重复的词放入一个数据字典中
        	Dictionary<Object, Object> wordsIndex=new Hashtable<Object, Object>();//用此数据结构来存储词，以方便找词        	
        	for(int n=start; n <start+termsNum ; n++)			
    		{
    			AddElement(wordsIndex, allUniques.get(n), n);			
    		}
        	System.out.println("dic"+m+":"+wordsIndex.size());
        	
        	//扫描该类的所有文档，对于一个文档中的每个词，在词典找到此词的位置，DF加一
        	while(m==Integer.parseInt(initTerms[k].substring(0, 1)))//是当前类的文档
        	{
        		String curDoc=initTerms[k];
    			String r="([|])";//每行代表的文档原始格式是：类别|词1|词2|...
    			String[] terms = curDoc.split(r);			
    			for (int j=1; j < terms.length; j++)
    			{
    				//int termIndex=GetTermIndex(terms[j]);
    				Object index=wordsIndex.get(terms[j]);
    				if (index != null) {    					
    					termIndex=(Integer)index;    				
    					docFreq[termIndex] ++;
    				}
    			}
    			if(k<initTerms.length-1)
    				k++;
    			else break;
        	}
        	
        	//继续下一个类别
        	start=start+termsNum;
    		m++;
    	}
    	//取超过某个阈值的所有词形成新的特征词典
    	List<String> DFterms= new ArrayList<String>();
    	int threshold=1;//DF的阈值
    	int firstNums=Integer.parseInt(perLabelTermsNum.get(0).toString());
    	//将第一类的所有的超过阈值的词直接加入到词典中
    	for (int j=0; j <firstNums; j++)
		{
			if (docFreq[j]>threshold) 
				DFterms.add(allUniques.get(j));		
		}
    	System.out.println("DFterms0:"+DFterms.size());
    	
    	//将剩余类的所有的超过阈值的词不重复的加入到词典中
    	for (int j=firstNums; j <allUniques.size(); j++)
		{
			if (docFreq[j]>threshold) 
				if (!DFterms.contains(allUniques.get(j)))
				DFterms.add(allUniques.get(j));		
		}
    	System.out.println("DFterms:"+DFterms.size());    	
	
		String[] DFtermsDic=new String[DFterms.size()];
//		DFterms.remove("东方");
//		DFterms.remove("财富");
		return (String[]) DFterms.toArray(DFtermsDic);		
	}
    
	private Object AddElement(Dictionary<Object, Object> collection, Object key, Object newValue)
	{
		Object element=collection.get(key);
		collection.put(key, newValue);
		return element;
	}
	
}
