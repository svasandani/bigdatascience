import java.io.FileNotFoundException;
import java.util.*;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class NamedEntitiesPosNeg {
	final static int TEXT_COLUMN = 5;
	final static int SENTIMENT_COLUMN = 0;
	final static String[] EXCLUDE = {"\"", " ", "", "i","me","my","myself","we","our","ours","ourselves","you","your","yours","yourself","yourselves","he","him","his","himself","she","her","hers","herself","it","its","itself","they","them","their","theirs","themselves","what","which","who","whom","this","that","these","those","am","is","are","was","were","be","been","being","have","has","had","having","do","does","did","doing","a","an","the","and","but","if","or","because","as","until","while","of","at","by","for","with","about","against","between","into","through","during","before","after","above","below","to","from","up","down","in","out","on","off","over","under","again","further","then","once","here","there","when","where","why","how","all","any","both","each","few","more","most","other","some","such","no","nor","not","only","own","same","so","than","too","very","s","t","can","will","just","don","should","now"};
	
	public static void main(String[] args) throws FileNotFoundException {
		CSVParser c = new CSVParser("sentiment140.csv");
		ArrayList<String[]> table = c.parse();
//		c.preview(2);       
		
		StanfordCoreNLP pipeline = Util.buildPipeline("tokenize,ssplit,pos,lemma,ner");
		
		// Named entity recognition
		
		String[][][] nerPatterns = {
				{{"People"}, {"PERSON"}}, // People
				{{"Organizations"}, {"ORGANIZATION"}}, // Persons
				{{"Places"}, {"LOCATION", "STATE_OR_PROVINCE"}}, // Persons
				};
		
		LinkedHashMap<String, Integer>[] nerPatternResultsNeg = collectSentimentNamedEntities(pipeline, table, nerPatterns, "\"0\"");
		
		for (LinkedHashMap<String, Integer> pattern : nerPatternResultsNeg) {
			System.out.println(Util.trimMap(Util.sortMap(pattern),10));
		}
		
		LinkedHashMap<String, Integer>[] nerPatternResultsPos = collectSentimentNamedEntities(pipeline, table, nerPatterns, "\"4\"");
		
		for (LinkedHashMap<String, Integer> pattern : nerPatternResultsPos) {
			System.out.println(Util.trimMap(Util.sortMap(pattern),10));
		}
	}
	
	public static LinkedHashMap<String, Integer>[] collectSentimentNamedEntities(
			StanfordCoreNLP pipeline, 
			ArrayList<String[]> table, 
			String[][][] nerPatterns,
			String sentiment) {
		System.out.printf("Collecting named entities for sentiment %s\n", sentiment);
		LinkedHashMap<String, Integer>[] nerMap = new LinkedHashMap[nerPatterns.length];
		
		int line = 0;
        int totalLines = table.size();
        
        for (int i = 0; i < nerPatterns.length; i++) {
			System.out.printf("Generating map for %s\n", nerPatterns[i][0][0]);
    		nerMap[i] = new LinkedHashMap<String, Integer>();
        }
    		
    	for (String[] s : table) {
			if (s.length <= TEXT_COLUMN) continue;
			if (s.length <= SENTIMENT_COLUMN) continue;
			if (!s[SENTIMENT_COLUMN].equals(sentiment)) continue;
			
			String text = Util.cleaningPipeline(s[TEXT_COLUMN]);
			
	        // create a document object
	        CoreDocument document = pipeline.processToCoreDocument(text);
	        
	        for (CoreSentence cs : document.sentences()) {
	        	List<String> nerTags = cs.nerTags();
	        	List<String> tokens = cs.tokensAsStrings();
	        	
	        	for (int i = 0; i < nerTags.size(); i++) {
	        		for (int j = 0; j < nerMap.length; j++) {
	        			if (Util.isInArray(nerTags.get(i), nerPatterns[j][1]))
		        			Util.putOrInc(nerMap[j], tokens.get(i));
	        		}
	        	}
	        }
	        
	        Util.printProgressBar(line, totalLines, 40);
	        
	        line++;
		}
		
		return nerMap;
	}
}
