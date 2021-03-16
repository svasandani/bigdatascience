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
		
		String text = "";
		
		for (String[] s : table) {
			text += s[TEXT_COLUMN];
		}
		
		HashMap<String, Integer> hashtags = trimHashmap(sortHashmap(collectHashtags(text)), 10);
		HashMap<String, Integer> mentions = trimHashmap(sortHashmap(collectMentions(text)), 10);

		System.out.println(hashtags.toString());
		System.out.println(mentions.toString());
	}
	
	public static void putOrInc(HashMap<String, Integer> map, String key) {
		if (map.containsKey(key)) map.put(key, map.get(key) + 1);
		else map.put(key, 1);
	}
	
	// https://stackoverflow.com/a/19671853
	public static HashMap<String, Integer> sortHashmap(HashMap<String, Integer> unsortedMap) {
		HashMap<String, Integer> sortedMap = 
			     unsortedMap.entrySet().stream()
			    .sorted(Entry.comparingByValue())
			    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
			                              (e1, e2) -> e1, LinkedHashMap::new));
		
		return sortedMap;
	}
	
	// https://stackoverflow.com/a/42879527
	public static HashMap<String, Integer> trimHashmap(HashMap<String, Integer> untrimmedMap, int length) {
		HashMap<String, Integer> trimmedMap = (HashMap<String, Integer>) untrimmedMap.entrySet().stream()
				  .limit(length)
				  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		return trimmedMap;
	}
	
	public static HashMap<String, Integer> collectHashtags(String text) {
		System.out.println("Collecting hashtags...");
		
		return collectPrefixedTokens(text, '#');
	}
	
	public static HashMap<String, Integer> collectMentions(String text) {
		System.out.println("Collecting mentions...");
		
		return collectPrefixedTokens(text, '@');
	}
	
	public static HashMap<String, Integer> collectPrefixedTokens(String text, char prefix) {		
		HashMap<String, Integer> tokens = new HashMap<String, Integer>();
		
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
		
		return tokens;
	}
}

class CSVParser {
	private String filename;
	private ArrayList<String[]> table;
	
	public CSVParser(String filename) {
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
