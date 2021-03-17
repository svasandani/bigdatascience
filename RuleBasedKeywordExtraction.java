import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;

import java.io.*;
import java.util.*;

public class RuleBasedKeywordExtraction {

	final static int TEXT_COLUMN = 5;
	final static String[] EXCLUDE = {"\"", " ", "", "i","me","my","myself","we","our","ours","ourselves","you","your","yours","yourself","yourselves","he","him","his","himself","she","her","hers","herself","it","its","itself","they","them","their","theirs","themselves","what","which","who","whom","this","that","these","those","am","is","are","was","were","be","been","being","have","has","had","having","do","does","did","doing","a","an","the","and","but","if","or","because","as","until","while","of","at","by","for","with","about","against","between","into","through","during","before","after","above","below","to","from","up","down","in","out","on","off","over","under","again","further","then","once","here","there","when","where","why","how","all","any","both","each","few","more","most","other","some","such","no","nor","not","only","own","same","so","than","too","very","s","t","can","will","just","don","should","now"};
	
	public static void main(String[] args) throws FileNotFoundException {
		CSVParser c = new CSVParser("sentiment140.csv");
		ArrayList<String[]> table = c.parse();
//		c.preview(2);        
		
		ArrayList<String[]> nounTable = extractNouns(table);
		
		NgramAnalysis n = new NgramAnalysis(nounTable);
		n.setTextColumn(0);
		
		System.out.println("Analyzing n-grams on nouns...");
		Map<String, Integer>[] nGrams = n.analyzeRange(1, 4, 20);
		
		for (Map<String, Integer> nGram : nGrams) {
			System.out.println(nGram);
		}
	}
	
	public static ArrayList<String[]> extractNouns(ArrayList<String[]> table) {
		System.out.println("Extracting nouns...");
		ArrayList<String[]> nounText = new ArrayList<String[]>();
		
		// set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos");
        // build pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        int line = 0;
        int totalLines = table.size();
        
        for (String[] s : table) {
        	if (s.length <= TEXT_COLUMN) continue;
			
			String text = cleaningPipeline(s[TEXT_COLUMN]);
			
	        // create a document object
	        CoreDocument document = pipeline.processToCoreDocument(text);
	        
	        String[] nounString = {""};
	        
	        for (CoreSentence cs : document.sentences()) {
	        	List<String> posTags = cs.posTags();
	        	
	        	for (CoreLabel token : cs.tokens()) {
	        		if ((token.tag().equals("NNP") || token.tag().equals("NN")) 
	        				&& !token.word().contains("http")
	        				&& !token.word().contains("www"))
	        			nounString[0] += token.word() + " ";
	        	}
	        }
	        
	        nounText.add(nounString);
	        
	        Util.printProgressBar(line, totalLines);
	        
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
