package TermPresentAndLdaExtend;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import LdaParts.*;;

public class LdaWords {
	public HashMap<String, Float> getMap() throws IOException{
        Corpus corpus = Corpus.load("D:/lda");
        // 2. Create a LDA sampler
        LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
        // 3. Train it
        ldaGibbsSampler.gibbs(2);
        // 4. The phi matrix is a LDA model, you can use LdaUtil to explain it.
        double[][] phi = ldaGibbsSampler.getPhi();
        Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), 8);
        return  normalizationldaWords(topicMap);
	}
	
	
    private HashMap<String, Float> normalizationldaWords(Map<String, Double>[] maps){
    	HashMap<String, Float> res =new HashMap<String, Float>();
    	for(int i=0;i<maps.length;i++){
    		double max =0;
    		double min =1;
    		Collection<Double> list = maps[i].values();
    		for(double m:list){
    			max =Math.max(max,m);
    			min =Math.min(min,m);
    		}
    		double mid =max-min;
    		System.out.println(max+" "+min+" "+mid);
    		for(String name:maps[i].keySet()){
    			maps[i].replace(name,0.3);
    			res.put(name, (float)maps[i].get(name).floatValue());
    		}
    	}
    	return res;
    }
}
