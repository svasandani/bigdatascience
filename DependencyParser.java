import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.trees.*;

import java.io.FileNotFoundException;
import java.util.*;


public class DependencyParser {
	final static int TEXT_COLUMN = 5;
	final static String[] EXCLUDE = {"\"", " ", "", "i","me","my","myself","we","our","ours","ourselves","you","your","yours","yourself","yourselves","he","him","his","himself","she","her","hers","herself","it","its","itself","they","them","their","theirs","themselves","what","which","who","whom","this","that","these","those","am","is","are","was","were","be","been","being","have","has","had","having","do","does","did","doing","a","an","the","and","but","if","or","because","as","until","while","of","at","by","for","with","about","against","between","into","through","during","before","after","above","below","to","from","up","down","in","out","on","off","over","under","again","further","then","once","here","there","when","where","why","how","all","any","both","each","few","more","most","other","some","such","no","nor","not","only","own","same","so","than","too","very","s","t","can","will","just","don","should","now"};
	
	public static void main(String[] args) throws FileNotFoundException {
		CSVParser c = new CSVParser("sentiment140.csv");
		ArrayList<String[]> table = c.parse();
//		c.preview(2);       
		
		StanfordCoreNLP pipeline = Util.buildPipeline("tokenize,ssplit,pos,depparse");
		
		// Dependency parse
		
		String[][][] depPatterns = {
				{{"nsubj root obj"}, {"nsubj", "root", "obj"}}, // nsubj root dobj
				};
		
		LinkedHashMap<String, Integer>[] depPatternResults = collectDependencyPatterns(pipeline, table, depPatterns);
		
		for (LinkedHashMap<String, Integer> pattern : depPatternResults) {
			System.out.println(Util.trimMap(Util.sortMap(pattern),10));
		}
	}
	
	public static LinkedHashMap<String, Integer>[] collectDependencyPatterns(
			StanfordCoreNLP pipeline, 
			ArrayList<String[]> table, 
			String[][][] depPatterns) {
		System.out.println("Collecting dependency patterns...");
		LinkedHashMap<String, Integer>[] depMap = new LinkedHashMap[depPatterns.length];
		
		int line = 0;
		int patternIndex = 0;
        int totalLines = table.size() * depPatterns.length;
		
		for (String[][] pattern : depPatterns) {
			System.out.printf("Collecting %s\n", pattern[0][0]);
			LinkedHashMap<String, Integer> patternMap = new LinkedHashMap<String, Integer>();
			
			int rootIndex = -1;
			
			for (int i = 0; i < pattern[1].length; i++) {
				if (pattern[1][i].equals("root")) rootIndex = i;
			}
			
			for (String[] s : table) {
	        	if (s.length <= TEXT_COLUMN) continue;
				
				String text = Util.cleaningPipeline(s[TEXT_COLUMN]);
				
		        // create a document object
		        CoreDocument document = pipeline.processToCoreDocument(text);
		        
		        for (CoreSentence cs : document.sentences()) {
		    	    SemanticGraph dependencyParse = cs.dependencyParse();

//		    	    System.out.println(cs);
//		    	    System.out.println(dependencyParse.getRoots().toString());
//		    	    System.out.println(dependencyParse);
		    	    
		    	    IndexedWord[] roots = {};
		    	    roots = dependencyParse.getRoots().toArray(roots);
		    	    
		    	    for (int i = 0; i < roots.length; i++) {
		    	    	int currentRootIndex = roots[i].index(); // adjust for 0-index

		    	    	// check for out of bounds errors
		    	    	if (
		    	    			// phrase is too long on left
		    	    			rootIndex > currentRootIndex - 1
		    	    			||
		    	    			// phrase is too long on right
		    	    			pattern[1].length - rootIndex > cs.tokens().size() - currentRootIndex + 1
		    	    		) continue;
		    	    	
		    	    	boolean inPattern = true;
		    	    	String[] tmpArray = new String[pattern[1].length];
		    	    	tmpArray[rootIndex] = roots[i].word();
		    	    	
		    	    	for (int j = 0; j < pattern[1].length && inPattern; j++) {
		    	    		if (j == rootIndex) continue;
		    	    		
		    	    		tmpArray[j] = dependencyParse.getNodeByIndex(currentRootIndex + j - rootIndex).word();
		    	    		
		    	    		SemanticGraphEdge edge = 
		    	    				dependencyParse
			    	    				.getEdge(
			    	    						dependencyParse.getNodeByIndex(currentRootIndex), 
			    	    						dependencyParse.getNodeByIndex(currentRootIndex + j - rootIndex)
			    	    						);
		    	    		
		    	    		if (!(edge != null && edge.getRelation().toString().equals(pattern[1][j])))
		    	    			inPattern = false;
		    	    	}
		    	    	
		    	    	String tmp = "";
		    	    	for (int j = 0; j < tmpArray.length; j++) tmp += tmpArray[j] 
		    	    			+ (j == tmpArray.length - 1 ? "" : " ");
		    	    	
		    	    	if (inPattern) {
		    	    		Util.putOrInc(patternMap, tmp);
		    	    	}
		    	    }
		        }
		        
		        Util.printProgressBar(line, totalLines, 40);
		        
		        line++;
	        }
			
			depMap[patternIndex] = patternMap;
			patternIndex++;
		}
		
		return depMap;
	}
}
