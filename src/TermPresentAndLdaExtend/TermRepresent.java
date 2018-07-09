package TermPresentAndLdaExtend;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
/*
 * 第一步拓展LDA已经完成，将LDA生成的，DF中没有的特征词，存入两个HashMap中，第一个保存词和序号，第二个保存词和对应的概率，加到后面
 * 下一步将DF的数据归一化
 * 下一步用LDA计算词和相应概率
 * 
 */
//此模块的功能是将预处理后的训练集或待分类文本集，根据特征词典，使用tf*idf算法对文本进行特征表示
public class TermRepresent {
	//private String inputTextFile;//待处理的文本集文件
	//private String inputTermDic;//特征词典文件
	//private static String outputTermRepresent;//文本集的特征表示文件
	
	private static String[] docs;//读入的文本集
	private static String[] terms;//读入的特征词典
	private int numDocs=0;//读入的文本集中文本数
	private int numTerms=0;//读入的特征词典的特征词个数
	private int[][] termFreq;
	private float[][] termWeight;
	private int[] maxTermFreq;
	private int[] docFreq;
	float Max=0;
	float Min=0;
	
	
	private Dictionary wordsIndex=new Hashtable();//程序使用的特征词典
	private String[] trDocs;//文本集的特征表示；
	private HashMap<String, Integer> num =new HashMap<String, Integer>();
	private HashMap<String, Float> nums;
	private HashMap<String, Float> copy;
	//2基于特征词典对文本集进行特征表示放在另一个String[]中	
	//后面别忘了传进来   num 和     nums
	public String[] TermRepresentMain(String[] allDocs,String[] termDic){
		docs=allDocs;
		terms=termDic;
		numDocs=docs.length;
		numTerms=terms.length;
		maxTermFreq=new int[numDocs] ;
		docFreq=new int[numTerms] ;
		termFreq =new int[numTerms][] ;
//////////////测试HashMap////////////////////////////////////////////////////////////////
		try {
			nums=new LdaWords().getMap();
			copy =new HashMap<String, Float>(nums);
//			check(nums, termDic);
			int add =numTerms;
			////////////////////////初始化NUM
			for(String s:nums.keySet()){
				num.put(s, add);
				add++;
				System.out.println(s+" "+add);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		num.put("测试", numTerms);
//		nums.put("测试", (float) 0.056879);
//		num.put("测试2", numTerms+1);
//		nums.put("测试2", (float) 0.054718);
///////////////////////////////////////////////////////////////////////
		termWeight=new float[numTerms+num.size()][numDocs] ;
		//这里要扩充termWeigh的数组////////////////////////////////////////////////////////////
		//2.1将所有的特征词加入到Hashtable中
		for(int i=0; i < terms.length ; i++)			
		{
//			termWeight[i]=new float[numDocs] ;
			termFreq[i]=new int[numDocs] ;

			AddElement(wordsIndex, terms[i], i);			
		}
	//加入LDA 隐主题 特征参数//////////////////////////////////////////////////////////////////
		for(int j=0;j<docs.length;j++)
		{
			putLdaWords(num, j, nums);
		}
	//加入LDA 隐主题 特征参数////////////////////////////////////////////////////////////////////
		//2.2计算tf (特征词在某个文档中出现的次数)
		GenerateTermFrequency();
		//2.3计算weight,并存放在二维数组中		
		GenerateTermWeight();
		/*
		//将二维数组表示转为KNN所要求的格式
		System.out.println(termWeight.length);
		System.out.println(termWeight[0].length);
		String[] result=new String[docs.length];
		for(int i=0; i<termWeight[0].length;i++)
		{
			result[i]=docs[i].substring(0, 1);
			for(int j=0;j<termWeight.length;j++)
			{
				//if(termWeight[j][i]!=0)
					result[i]+=" "+termWeight[j][i];
			}
		}
		System.out.println(result.length);
		*/
		//将二维数组表示转为libsvm所要求的格式
		//归一//////////////////////////////////////////////////
		normalization();
		System.out.println(termWeight.length);
		System.out.println(termWeight[0].length);
		checkfirst(copy, termDic);
		String[] result=new String[docs.length];
		for(int i=0; i<termWeight[0].length;i++)
		{
			result[i]=docs[i].substring(0, 1);
			for(int j=0;j<terms.length;j++)
			{
				//J是特征词  i是文档中所有的词(每一行)
				if(termWeight[j][i]!=0)
					result[i]+=" "+j+":"+termWeight[j][i];
			}
		}
///////后面加入隐主题训练//////////////////////////////////////////////////////////////////
		for(int i=0; i<termWeight[0].length;i++){
			for(int j=terms.length;j<termWeight.length;j++){
				if(termWeight[j][i]!=0)
				result[i]+=" "+(j+1)+":"+0.5;
			}
		}
///////////////////////////////////////////////////////////////////////////
		System.out.println(result.length);
		
		//String[] result=new String[docs.length];
		
		return result; 
	}
	
	//2.1将全部特征词放入到Hashtable中
	private static Object AddElement(Dictionary collection, Object key, Object newValue)
	{
		Object element=collection.get(key);
		collection.put(key, newValue);
		return element;
	}
	
	//2.2计算tf (特征词在某个文档中出现的次数)
	private void GenerateTermFrequency()
	{
		for(int i=0; i < numDocs  ; i++)
		{								
			String curDoc=docs[i];
			Dictionary freq=GetWordFrequency(curDoc);
			Enumeration enums=freq.keys();
			
			while(enums.hasMoreElements()){
				String word=(String) enums.nextElement();
				int wordFreq=(Integer)freq.get(word);
				int termIndex=GetTermIndex(word);
                if(termIndex == -1)
                    continue;
				termFreq [termIndex][i]=wordFreq;
				docFreq[termIndex] ++;

				if (wordFreq > maxTermFreq[i]) maxTermFreq[i]=wordFreq;	
			}			
			maxTermFreq[i]=Integer.MIN_VALUE ;
		}
	}
	private Dictionary GetWordFrequency(String input)
	{
		String convertedInput=input.toLowerCase() ;	       
        String r="([|])";//每行代表的文档原始格式是：类别|词1|词2|...
        String[] words = convertedInput.split(r);
		Arrays.sort(words);
		
		String[] distinctWords=GetDistinctWords(words);
					
		Dictionary result=new Hashtable();
		for (int i=0; i < distinctWords.length; i++)
		{
			Object tmp;
			tmp=CountWords(distinctWords[i], words);
			result.put(distinctWords[i], tmp);
			
		}
		
		return result;
	}
	private static String[] GetDistinctWords(String[] input)
	{				
		if (input == null)			
			return new String[0];			
		else
		{
            List<String> list = new ArrayList<String>();
			
			for (int i=0; i < input.length; i++)
				if (!list.contains(input[i])) 			
					list.add(input[i]);
			String[] v=new String[list.size()];
			return (String[]) list.toArray(v);
		}
	}
	private int CountWords(String word, String[] words)
	{
		int itemIdx=Arrays.binarySearch(words, word);
		
		if (itemIdx > 0)			
			while (itemIdx > 0 && words[itemIdx].equals(word))				
				itemIdx--;				
					
		int count=0;
		while (itemIdx < words.length && itemIdx >= 0)
		{
			if (words[itemIdx].equals(word)) count++;				
			
			itemIdx++;
			if (itemIdx < words.length)				
				if (!words[itemIdx].equals(word)) break;					
			
		}
		
		return count;
	}
	private int GetTermIndex(String term)
	{
		Object index=wordsIndex.get(term);
		if (index == null) return -1;
		return (Integer)index;
	}
	//2.3计算weight
	private void GenerateTermWeight()
	{			
		for(int i=0; i < numTerms   ; i++)
		{
			for(int j=0; j < numDocs ; j++)	{			
				termWeight[i][j]=ComputeTermWeight (i, j);		
		//这里如果判断是0，即所选择的词在DF中没有出现，判断是否出现在LDA模型中，如果出现，加进去，完美
//			   if(termWeight[i][j]==0){
//				   putLdaWords(i,j);
//			   }
			}
		}
	}
//关键添加扫描每一行，如果出现，赋予数组值///////////////////////////////////////////////////////////////////////////////
	private void putLdaWords(HashMap<String, Integer> num,int j,HashMap<String, Float> nums){
		String flag=docs[j];
		Set<String> set =  nums.keySet();
		String mid[] = flag.split("\\|");
		for(int k=0;k<mid.length;k++){
	
			if(set.contains(mid[k])){
				termWeight[num.get(mid[k])][j]=nums.get(mid[k]);
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////
	private float ComputeTermWeight(int term, int doc)
	{
		float tf=GetTermFrequency (term, doc);
		float idf=GetInverseDocumentFrequency(term);
		return tf * idf;
	}
	private float GetTermFrequency(int term, int doc)
	{			
		int freq=termFreq [term][doc];
		int maxfreq=maxTermFreq[doc];			
		
		return ( (float) freq/(float)maxfreq );
	}
	private float GetInverseDocumentFrequency(int term)
	{
		int df=docFreq[term];
		//20110509添加
		if (df==0)
			return 0;
		else
		//20110509添加
		return Log((float) (numDocs) / (float) df );
	}
	private float Log(float num)
	{
		return (float) Math.log(num) ;//log2
	}
/////////////////////////////////////归一化/////////
	private void normalization(){
		//找出最大最小值
		for(int i=0;i<terms.length;i++){
		   for(int j=0;j<termWeight[0].length;j++){
                 Max =Math.max(Max, termWeight[i][j]);
                 Min =Math.min(Min, termWeight[i][j]);
		   }
		}
		float mid =Max-Min;
	//归一
		for(int i=0;i<terms.length;i++){
			   for(int j=0;j<termWeight[0].length;j++){
				   if(termWeight[i][j]!=0){
					   termWeight[i][j]=(termWeight[i][j]-Min)/mid;
				   }
			   }
			}
	}
////////////////////////////////////////////////////
	
//LDA去重///////////////////////////////////////
	private void check(HashMap<String, Float> map,String[] termsDic) {
		List<String> list =	Arrays.asList(termsDic);
        Iterator<Entry<String, Float>> it3 = map.entrySet().iterator();
        while(it3.hasNext()){
            Entry<String, Float> entry = it3.next();
            if(list.contains(entry.getKey())||entry.getValue()==0){
            	it3.remove();
            };
        }
	}
	//加强///////////////////////////////////////////////////
	private void checkfirst(HashMap<String, Float> map,String[] termsDic) {
		Set<String> set =map.keySet();
		for(int i=0;i<termsDic.length;i++){
			if(set.contains(termsDic[i])){
				for(int m=0;m<termWeight[i].length;m++){
					if(termWeight[i][m]!=0)
						//这一步不能改，文本表示后就不再是权重
					termWeight[i][m]=(float) (0.8);
				}
			}
		}
	}
}
