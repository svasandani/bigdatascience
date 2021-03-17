import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

public class NgramAnalysis {
	
	final static int TEXT_COLUMN = 5;
	final static String[] EXCLUDE = {"\"", " ", "", "i","me","my","myself","we","our","ours","ourselves","you","your","yours","yourself","yourselves","he","him","his","himself","she","her","hers","herself","it","its","itself","they","them","their","theirs","themselves","what","which","who","whom","this","that","these","those","am","is","are","was","were","be","been","being","have","has","had","having","do","does","did","doing","a","an","the","and","but","if","or","because","as","until","while","of","at","by","for","with","about","against","between","into","through","during","before","after","above","below","to","from","up","down","in","out","on","off","over","under","again","further","then","once","here","there","when","where","why","how","all","any","both","each","few","more","most","other","some","such","no","nor","not","only","own","same","so","than","too","very","s","t","can","will","just","don","should","now"};

	public static void main(String[] args) throws FileNotFoundException {
		CSVParser c = new CSVParser("sentiment140.csv");
		ArrayList<String[]> table = c.parse();
//		c.preview(2);
		
		Map<String, Integer> oneGrams = Util.trimMap(Util.sortMap(collectNgrams(table, 1)), 20);
		Map<String, Integer> twoGrams = Util.trimMap(Util.sortMap(collectNgrams(table, 2)), 20);
		Map<String, Integer> threeGrams = Util.trimMap(Util.sortMap(collectNgrams(table, 3)), 20);
		Map<String, Integer> fourGrams = Util.trimMap(Util.sortMap(collectNgrams(table, 4)), 20);

		System.out.println(oneGrams.toString());
		System.out.println(twoGrams.toString());
		System.out.println(threeGrams.toString());
		System.out.println(fourGrams.toString());
	}
	
	public static String[] removeStopwords(String[] textArray) {
		int excludedLength = 0;
		for (String str : textArray) {
			boolean excludeString = false;
			
			for (int k = 0; k < EXCLUDE.length; k++) {
				if (str.toLowerCase().equals(EXCLUDE[k])) excludeString = true;
			}
			
			if (!excludeString) {
				excludedLength++;
			}
		}
		
		String[] excludedTextArray = new String[excludedLength];
		int excludedIndex = 0;
		for (String str : textArray) {
			boolean excludeString = false;
					
			for (int k = 0; k < EXCLUDE.length; k++) {
				if (str.toLowerCase().equals(EXCLUDE[k])) excludeString = true;
			}
			
			if (!excludeString) {
				excludedTextArray[excludedIndex] = str;
				excludedIndex++;
			}
		}
		
		return excludedTextArray;
	}
	
	public static LinkedHashMap<String, Integer> collectNgrams(ArrayList<String[]> table, int n) {
		LinkedHashMap<String, Integer> nGrams = new LinkedHashMap<String, Integer>();
		
		for (String[] s : table) {
			if (s.length <= TEXT_COLUMN) continue;
			
			String[] textArray = s[TEXT_COLUMN].split(" ");
			
			// remove quotation marks from CSV
			textArray[0] = textArray[0].substring(1);
			textArray[textArray.length - 1] = textArray[textArray.length - 1]
					.substring(0, textArray[textArray.length - 1].length() - 1);
			
			String[] excludedTextArray = removeStopwords(textArray);
			
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
