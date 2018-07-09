package CorpusSpiderAndPreProcess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MySpider {
	private static final String url1 = "http://guba.eastmoney.com/list,300059_";
	private static final String url2 = ".html";

	public static void preprocess(String dir) {
		// 预处理部分
		InputOutput rw = new InputOutput();
		String its[] = rw.readInput(dir + "spider.txt");
		PreProcess p = new PreProcess();
		try {
			String docus[] = p.preProcessMain(its);
			new File(dir+"spiderSegment.txt");
			PrintStream pr2 = new PrintStream(
					new FileOutputStream(new File(dir+"spiderSegment.txt")));
			for (int i = 0; i < docus.length; i++) {
				pr2.println(docus[i]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
//		 爬取数据部分
//		 PrintStream pr =new PrintStream(new FileOutputStream(new
//		 File("D:/DayOfStock/6.28-6.30.txt")));
//		 int k=0;
//		 for(int num=10;num<=12;num++){
//		 try {
//		 //获取整个网站的根节点，也就是html开头部分一直到结束
//		 Document document = Jsoup.connect(url1+num+url2).get();
//		 Elements a = document.select("a[title]");
//		 Elements b =document.select("a[rel]");
//		 a.removeAll(b);
//		 for(int i=0;i<a.size();i++){
//		 Element el =a.get(i);
//		 pr.println(++k+"|"+el.text());
//		 }
//		 System.out.println(a);
//		 } catch (IOException e) {
//		 // TODO Auto-generated catch block
//		 e.printStackTrace();
//		 }
//		 }
//		 pr.close();

	}
}
