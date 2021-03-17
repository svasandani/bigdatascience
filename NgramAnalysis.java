import java.io.*;
import java.util.*;

public class NgramAnalysis {
	
	private int TEXT_COLUMN = 5;
	private String[] EXCLUDE = {"\"", " ", "", "i","me","my","myself","we","our","ours","ourselves","you","your","yours","yourself","yourselves","he","him","his","himself","she","her","hers","herself","it","its","itself","they","them","their","theirs","themselves","what","which","who","whom","this","that","these","those","am","is","are","was","were","be","been","being","have","has","had","having","do","does","did","doing","a","an","the","and","but","if","or","because","as","until","while","of","at","by","for","with","about","against","between","into","through","during","before","after","above","below","to","from","up","down","in","out","on","off","over","under","again","further","then","once","here","there","when","where","why","how","all","any","both","each","few","more","most","other","some","such","no","nor","not","only","own","same","so","than","too","very","s","t","can","will","just","don","should","now"};
	private String filename;
	private ArrayList<String[]> table;
	
	public NgramAnalysis(String filename) throws FileNotFoundException {
		this.filename = filename;
		
		CSVParser c = new CSVParser(filename);
		this.table = c.parse();
	}
	
	public NgramAnalysis(ArrayList<String[]> table) {
		this.table = table;
	}
	
	public void setTextColumn(int textColumn) {
		this.TEXT_COLUMN = textColumn;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		CSVParser c = new CSVParser("sentiment500.csv");
		ArrayList<String[]> table = c.parse();
//		c.preview(2);
		
		NgramAnalysis me = new NgramAnalysis(table);
		
		Map<String, Integer>[] nGrams = me.analyzeRange(1, 4, 20);

		for (Map<String, Integer> nGram : nGrams) {
			System.out.println(nGram);
		}
	}
	
	public Map<String, Integer>[] analyzeRange(int start, int end, int limit) {
		if (end <= start) return null;
		
		Map<String, Integer>[] range = new Map[end - start + 1];
		
		for (int i = start; i <= end; i++) {
			range[i - start] = analyzeTop(i, limit);
		}
		
		return range;
	}
	
	public Map<String, Integer> analyzeTop(int n, int limit) {
		return Util.trimMap(Util.sortMap(collectNgrams(table, n)), limit);
	}
	
	public LinkedHashMap<String, Integer> collectNgrams(ArrayList<String[]> table, int n) {
		System.out.printf("Collecting %s-grams...\n", n);
		
		LinkedHashMap<String, Integer> nGrams = new LinkedHashMap<String, Integer>();
		
		for (String[] s : table) {
			if (s.length <= TEXT_COLUMN) continue;
			
			String[] textArray = s[TEXT_COLUMN].split(" ");
			
			// remove quotation marks from CSV
			if (textArray[0].length() > 0 && textArray[0].charAt(0) == '\"') 
				textArray[0] = textArray[0].substring(1);
			if (textArray[textArray.length - 1].length() > 0 
			    && textArray[textArray.length - 1]
					 .charAt(textArray[textArray.length - 1].length() - 1) == '\"') 
				textArray[textArray.length - 1] = textArray[textArray.length - 1]
					.substring(0, textArray[textArray.length - 1].length() - 1);
			
			String[] excludedTextArray = Util.removeStopwords(textArray, EXCLUDE);
			
			if (excludedTextArray.length < n) continue;
			
			for (int i = 0; i < excludedTextArray.length - (n - 1); i++) {
				String ngram = "";
				
				for (int j = 0; j < n; j++) {
					ngram += excludedTextArray[i + j] + (j == n - 1 ? "" : " ");
				}
				
				Util.putOrInc(nGrams, ngram);
			}
		}
		
		return nGrams;
	}
}
