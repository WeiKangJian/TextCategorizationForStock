/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2015/1/29 17:22</create-date>
 *
 * <copyright file="TestCorpus.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package LdaParts;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author hankcs
 */
public class TestCorpus extends TestCase
{
//    public void testAddDocument() throws Exception
//    {
//        List<String> doc1 = new ArrayList<String>();
//        doc1.add("hello");
//        doc1.add("word");
//        List<String> doc2 = new ArrayList<String>();
//        doc2.add("hankcs");
//        Corpus corpus = new Corpus();
//        corpus.addDocument(doc1);
//        corpus.addDocument(doc2);
//        System.out.println(corpus);
//    }

    public void testAll() throws Exception
    {
        // 1. Load corpus from disk
        Corpus corpus = Corpus.load("lda");
        // 2. Create a LDA sampler
        LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
        // 3. Train it
        ldaGibbsSampler.gibbs(3);
        // 4. The phi matrix is a LDA model, you can use LdaUtil to explain it.
        double[][] phi = ldaGibbsSampler.getPhi();
        Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), 6);
        normalization(topicMap);
        LdaUtil.explain(topicMap);
        // 5. TODO:Predict. I'm not sure whether it works, it is not stable.
//        int[] document = Corpus.loadDocument("D:/mini/军事_510.txt", corpus.getVocabulary());
//        double[] tp = LdaGibbsSampler.inference(phi, document);
//        Map<String, Double> topic = LdaUtil.translate(tp, phi, corpus.getVocabulary(), 10);
//        LdaUtil.explain(topic);
    }
    public void normalization(Map<String, Double>[] maps){
    	for(int i=0;i<maps.length;i++){
    		double max =0;
    		double min =1;
    		Collection<Double> list = maps[i].values();
    		for(double m:list){
    			max =Math.max(max,m);
    			min =Math.min(min,m);
    		}
    		double mid =max-min;
    		System.out.println(max+" "+min+""+mid);
    		for(String name:maps[i].keySet()){
    			maps[i].replace(name,(maps[i].get(name)-min)/mid);
    		}
    	}
    }
}
