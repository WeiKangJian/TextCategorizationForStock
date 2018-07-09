package TrainAndCategorization;


import java.io.IOException;

import CorpusSpiderAndPreProcess.InputOutput;
import CorpusSpiderAndPreProcess.MySpider;
import DFAndIGSelect.DFTermSelectLocal;
import DFAndIGSelect.IGTermSelect;
import TermPresentAndLdaExtend.TermRepresent;
import SVMNecessaryPart.*;





public class TrainClassifierMain {
	public static void main(String[] args) throws IOException{
		String dir ="10crossStockData/10crossStockData1/";
		String trainFile=dir+"spiderSegment.txt";
		//预处理
		MySpider.preprocess(dir);
		InputOutput rw=new InputOutput();
		
		String[] docs=rw.readInput(trainFile);


//		3.2使用局部DF
		DFTermSelectLocal td=new DFTermSelectLocal();
		
		
		//1对训练集的词集合选择使用特征选择方法建立特征词典放在一个String[]（terms）中
		String[] terms=td.TermDictionaryMain(docs);		
		
		//2将特征词典输出到文件,这个文件是训练阶段输出的第一个必需的文件
		String termDicFile=trainFile.substring(0,trainFile.lastIndexOf("/")+1)+"termDic.txt";
		rw.writeOutput(terms, termDicFile);
		
		/////////////////////////////////////////////////////////////////////////////////
		//子功能4：训练集的特征表示（将训练集的词集合String[]（docs）基于特征词典String[]（terms）
		//进行文本表示放在一个String[]（trDocs）中）
		TermRepresent tr=new TermRepresent();
		
		//2基于特征词典对文本集进行特征表示放在一个String[](trDocs)中
		String[] trDocs=tr.TermRepresentMain(docs,terms);
				
		//3将训练集的特征表示写入到文件，这个文件的内容是svm要求的数据格式
		String trFile=trainFile.substring(0,trainFile.lastIndexOf("."))+"SegmentTR.txt";
		rw.writeOutput(trDocs, trFile);
		
		//1基于训练集产生range文件
		String rangeFile=trFile+".range";
		String argv[]={"-l","0","-s",rangeFile,trFile};
		SVMScale s = new SVMScale();
		s.run(argv);
		//2使用range文件对训练集进行归一化处理
		String scaleFile=trFile+".scale";
		String argv1[]={"-t",scaleFile,"-r",rangeFile,trFile};
		s.run(argv1);
		////////////////////////////////////////////////////////////////////////////////
		//子功能6：建立分类模型
		String modelFile=scaleFile+".model";
		String argv2[]={"-s","0","-c","1","-t","0",scaleFile,modelFile};
		SVMTrain train = new SVMTrain();		
		train.run(argv2);
	}
}
