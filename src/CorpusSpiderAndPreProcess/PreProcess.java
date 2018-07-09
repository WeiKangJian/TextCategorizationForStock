package CorpusSpiderAndPreProcess;


import java.io.IOException;
import java.io.StringReader;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

//此模块的功能是将初始的训练集文件或待分类文本集中的每个文本进行切词，去停用词等预处理
public class PreProcess {

//对初始的训练集预处理之后形成的训练集放在另一个String[]中
public String[] preProcessMain(String[] InputDocs)throws IOException {
	
	String[] OutputDocs=new String[InputDocs.length];
	String row="";
	String t = null;
	int i=0;
	while(i<InputDocs.length) {
		row+=InputDocs[i].substring(0, 1);	//写 类别
		t=InputDocs[i].substring(2);
		IKSegmentation ikSeg = new IKSegmentation(new StringReader(t) ,true);

		Lexeme l = null;
		while( (l = ikSeg.next()) != null)
		{
			//将CJK_NORMAL类的词写入目标文件
			if(l.getLexemeType() == Lexeme.TYPE_CJK_NORMAL)	
			{
				//后续在此添加判断此词是否为停用词，若不是则写入目标文件中
				row+='|' + l.getLexemeText();
			}
		}
		OutputDocs[i]=row;
		i++;
		row="";
		System.out.println("1");
	}
	return OutputDocs;
}
}
