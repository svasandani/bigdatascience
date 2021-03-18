import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;

import java.io.*;
import java.util.*;

public class POSTagger {

	final static int TEXT_COLUMN = 5;
	final static String[] EXCLUDE = {"\"", " ", "", "i","me","my","myself","we","our","ours","ourselves","you","your","yours","yourself","yourselves","he","him","his","himself","she","her","hers","herself","it","its","itself","they","them","their","theirs","themselves","what","which","who","whom","this","that","these","those","am","is","are","was","were","be","been","being","have","has","had","having","do","does","did","doing","a","an","the","and","but","if","or","because","as","until","while","of","at","by","for","with","about","against","between","into","through","during","before","after","above","below","to","from","up","down","in","out","on","off","over","under","again","further","then","once","here","there","when","where","why","how","all","any","both","each","few","more","most","other","some","such","no","nor","not","only","own","same","so","than","too","very","s","t","can","will","just","don","should","now"};
	
	public static void main(String[] args) throws FileNotFoundException {
		CSVParser c = new CSVParser("sentiment500.csv");
		ArrayList<String[]> table = c.parse();
//		c.preview(2);       
		
		StanfordCoreNLP pipeline = Util.buildPipeline("tokenize,ssplit,pos");
		
		ArrayList<String[]> nounTable = extractNouns(pipeline, table);
		
		NgramAnalysis n = new NgramAnalysis(nounTable);
		n.setTextColumn(0);
		
		System.out.println("Analyzing n-grams on nouns...");
		Map<String, Integer>[] nGrams = n.analyzeRange(1, 4, 20);
		
		for (Map<String, Integer> nGram : nGrams) {
			System.out.println(nGram);
		}
		
		// POS patterns
		
		String[][][] posPatterns = {
				{{"adjective noun"}, {"JJ", "JJR", "JJS"},{"NN","NNS","NNP","NNS"}}, // adjective noun
				{{"noun verb"}, {"NN","NNS","NNP","NNS"},{"VB","VBD","VBG","VBN","VBP","VBZ"}}, // noun verb
				{{"adverb verb"}, {"RB","RBR","RBS"},{"VB","VBD","VBG","VBN","VBP","VBZ"}} // adverb verb
				};
		
		LinkedHashMap<String, Integer>[] posPatternResults = collectPOSPatterns(pipeline, table, posPatterns);
		
		for (LinkedHashMap<String, Integer> pattern : posPatternResults) {
			System.out.println(Util.trimMap(Util.sortMap(pattern),10));
		}
	}
	
	public static LinkedHashMap<String, Integer>[] collectPOSPatterns(StanfordCoreNLP pipeline, ArrayList<String[]> table, String[][][] posPatterns) {
		System.out.println("Collecting POS patterns...");
		LinkedHashMap<String, Integer>[] posMap = new LinkedHashMap[posPatterns.length];
		
		int line = 0;
		int patternIndex = 0;
        int totalLines = table.size() * posPatterns.length;
		
		for (String[][] pattern : posPatterns) {
			System.out.printf("Collecting %s\n", pattern[0][0]);
			LinkedHashMap<String, Integer> patternMap = new LinkedHashMap<String, Integer>();
			
			for (String[] s : table) {
	        	if (s.length <= TEXT_COLUMN) continue;
				
				String text = cleaningPipeline(s[TEXT_COLUMN]);
				
		        // create a document object
		        CoreDocument document = pipeline.processToCoreDocument(text);
		        
		        for (CoreSentence cs : document.sentences()) {
		        	boolean inPattern = false;
		        	String tmp = "";
		        	
		        	for (CoreLabel token : cs.tokens()) {
		        		if (inPattern) {
		        			if (Util.isInArray(token.tag(), pattern[2])) { // if in part 2, add to map
		        				Util.putOrInc(patternMap, tmp + token.word());
		        				
		        				tmp = "";
		        				inPattern = false;
		        			} else if (Util.isInArray(token.tag(), pattern[1])) { // if in part 1, reset
		        				tmp = token.word() + " ";
		        			} else { // if not in any, turn off
		        				tmp = "";
		        				inPattern = false;
		        			}
		        		} else {
		        			if (Util.isInArray(token.tag(), pattern[1])) { // if in part 1, reset
		        				tmp = token.word() + " ";
		        				inPattern = true;
		        			}
		        		}
		        	}
		        }
		        
		        Util.printProgressBar(line, totalLines, 40);
		        
		        line++;
	        }
			
			posMap[patternIndex] = patternMap;
			patternIndex++;
		}
		
		return posMap;
	}
	
	public static ArrayList<String[]> extractNouns(StanfordCoreNLP pipeline, ArrayList<String[]> table) {
		System.out.println("Extracting nouns...");
		ArrayList<String[]> nounText = new ArrayList<String[]>();
        
        int line = 0;
        int totalLines = table.size();
        
        for (String[] s : table) {
        	if (s.length <= TEXT_COLUMN) continue;
			
			String text = cleaningPipeline(s[TEXT_COLUMN]);
			
	        // create a document object
	        CoreDocument document = pipeline.processToCoreDocument(text);
	        
	        String[] nounString = {""};
	        
	        for (CoreSentence cs : document.sentences()) {	        	
	        	for (CoreLabel token : cs.tokens()) {
	        		if (
	        		     (
		        			token.tag().equals("NNP") || 
		        			token.tag().equals("NN") || 
		        			token.tag().equals("NNS") || 
		        			token.tag().equals("NNPS")
		        		 )  && !token.word().contains("http")
	        				&& !token.word().contains("www"))
	        			nounString[0] += token.word() + " ";
	        	}
	        }
	        
	        nounText.add(nounString);
	        
	        Util.printProgressBar(line, totalLines, 40);
	        
	        line++;
        }
        
        return nounText;
	}
	
	public static String cleaningPipeline(String text) {
		return Util.removeNonAlphanumeric(
			       Util.removePrefixedTokens(
			           Util.removePrefixedTokens(
			               text
			           ,'@')
			       ,'#')
			   );
	}

}
