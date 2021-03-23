import java.io.FileNotFoundException;
import java.util.*;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class NamedEntities {
	final static int TEXT_COLUMN = 5;
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
				{{"Placces"}, {"LOCATION", "STATE_OR_PROVINCE"}}, // Persons
				};
		
		LinkedHashMap<String, Integer>[] nerPatternResults = collectNamedEntities(pipeline, table, nerPatterns);
		
		for (LinkedHashMap<String, Integer> pattern : nerPatternResults) {
			System.out.println(Util.trimMap(Util.sortMap(pattern),10));
		}
	}
	
	public static LinkedHashMap<String, Integer>[] collectNamedEntities(
			StanfordCoreNLP pipeline, 
			ArrayList<String[]> table, 
			String[][][] nerPatterns) {
		System.out.printf("Collecting named entities");
		LinkedHashMap<String, Integer>[] nerMap = new LinkedHashMap[nerPatterns.length];
		
		int line = 0;
		int patternIndex = 0;
        int totalLines = table.size() * nerPatterns.length;
        
        for (String[][] pattern : nerPatterns) {
			System.out.printf("Collecting %s\n", pattern[0][0]);
    		LinkedHashMap<String, Integer> namedEntities = new LinkedHashMap<String, Integer>();
    		
        	for (String[] s : table) {
    			if (s.length <= TEXT_COLUMN) continue;
    			
    			String text = Util.cleaningPipeline(s[TEXT_COLUMN]);
    			
    	        // create a document object
    	        CoreDocument document = pipeline.processToCoreDocument(text);
    	        
    	        for (CoreSentence cs : document.sentences()) {
    	        	List<String> nerTags = cs.nerTags();
    	        	List<String> tokens = cs.tokensAsStrings();
    	        	
    	        	for (int i = 0; i < nerTags.size(); i++) {
    	        		if (Util.isInArray(nerTags.get(i), pattern[1]))
    	        			Util.putOrInc(namedEntities, tokens.get(i));
    	        	}
    	        }
    	        
    	        Util.printProgressBar(line, totalLines, 40);
    	        
    	        line++;
    		}
			
			nerMap[patternIndex] = namedEntities;
			patternIndex++;
        }
		
		return nerMap;
	}
}
