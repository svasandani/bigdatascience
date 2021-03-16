import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

public class DataExploration {
	
	final static int TEXT_COLUMN = 5;

	public static void main(String[] args) throws FileNotFoundException {
		CSVParser c = new CSVParser("sentiment140.csv");
		ArrayList<String[]> table = c.parse();
//		c.preview(2);
		
		Map<String, Integer> hashtags = trimMap(sortMap(collectHashtags(table)), 10);
		Map<String, Integer> mentions = trimMap(sortMap(collectMentions(table)), 10);

		System.out.println(hashtags.toString());
		System.out.println(mentions.toString());
	}
	
	public static void putOrInc(HashMap<String, Integer> map, String key) {
		if (map.containsKey(key)) map.put(key, map.get(key) + 1);
		else map.put(key, 1);
	}
	
	// https://stackoverflow.com/a/19671853
	public static LinkedHashMap<String, Integer> sortMap(LinkedHashMap<String, Integer> unsortedMap) {
		LinkedHashMap<String, Integer> sortedMap = (LinkedHashMap<String, Integer>)
			     unsortedMap.entrySet().stream()
			    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
			    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		return sortedMap;
	}
	
	// https://stackoverflow.com/a/42879527
	public static Map<String, Integer> trimMap(LinkedHashMap<String, Integer> untrimmedMap, int length) {
		LinkedHashMap<String, Integer> trimmedMap = (LinkedHashMap<String, Integer>) 
				  untrimmedMap.entrySet().stream()
				  .limit(length)
				  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		return trimmedMap;
	}
	
	public static LinkedHashMap<String, Integer> collectHashtags(ArrayList<String[]> table) {
		System.out.println("Collecting hashtags...");
		
		return collectPrefixedTokens(table, '#');
	}
	
	public static LinkedHashMap<String, Integer> collectMentions(ArrayList<String[]> table) {
		System.out.println("Collecting mentions...");
		
		return collectPrefixedTokens(table, '@');
	}
	
	public static LinkedHashMap<String, Integer> collectPrefixedTokens(ArrayList<String[]> table, char prefix) {		
		LinkedHashMap<String, Integer> tokens = new LinkedHashMap<String, Integer>();
		
		for (String[] s : table) {
			if (s.length <= TEXT_COLUMN) continue;
			
			String text = s[TEXT_COLUMN];
			
			String tmp = "";
			boolean inToken = false;
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				
				if (c == prefix) {
					if (inToken) {
						putOrInc(tokens, tmp);
						tmp = "";
					} else {
						tmp += c;
						inToken = true;
					}
				} else if (!(Character.isDigit(c) || Character.isLetter(c) || c == '_')) {
					if (inToken) {
						putOrInc(tokens, tmp);
						tmp = "";
						inToken = false;
					}
				} else {
					if (inToken) tmp += c;
				}
			}
		}
		
		return tokens;
	}
}

class CSVParser {
	private String filename;
	private ArrayList<String[]> table;
	
	public CSVParser(String filename) {
		System.out.println("New parser from " + filename);
		this.filename = filename;
	}
	
	public void preview() {
		for (String[] s : this.table) {
			System.out.println(String.join(",   ", s));
		}
	}
	
	public void preview(int rows) {
		int displayedRows = 0;
		
		for (String[] s : this.table) {
			if (displayedRows == rows) return;
			
			System.out.println(String.join(",   ", s));
			
			displayedRows++;
		}
	}
	
	public ArrayList<String[]> getTable() {
		return this.table;
	}
	
	public ArrayList<String[]> parse() throws FileNotFoundException {
		System.out.println("Parsing CSV...");
		
		Scanner input = new Scanner(new File(this.filename));
		
		ArrayList<String[]> table = new ArrayList<String[]>();
		
		while(input.hasNextLine()) {
			table.add(this.parseLine(input.nextLine()));
		}
		
		this.table = table;
		
		return table;
	}
	
	private String[] parseLine(String line) {
		String[] naive = line.split(",");
		
		int naiveIndex = 0;
		int nonNaiveIndex = 0;
		boolean quotes = false;
		while (naiveIndex < naive.length) {
			String token = naive[naiveIndex];
			
			if (token.length() == 0) {
				naiveIndex++;
				nonNaiveIndex++;
				continue;
			}
			
			if (!quotes) {
				if (token.charAt(0) == '\"') {
					naive[nonNaiveIndex] = token;
					if (token.charAt(token.length() - 1) != '\"') {
						quotes = true;
					} else nonNaiveIndex++;
				} else {
					naive[nonNaiveIndex] = token;
					nonNaiveIndex++;
				}
			} else {
				naive[nonNaiveIndex] += token;
				if (token.charAt(token.length() - 1) == '\"') {
					nonNaiveIndex++;
				}
			}
			
			naiveIndex++;
		}
		
		String[] converted = new String[nonNaiveIndex];
		
		for (int i = 0; i < nonNaiveIndex; i++) {
			converted[i] = naive[i];
		}
		
		return converted;
	}
}
