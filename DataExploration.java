import java.io.*;
import java.util.*;
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
		
		HashMap<String, Integer> hashtags = collectHashtags(text);
		HashMap<String, Integer> mentions = collectMentions(text);
		
		System.out.println(hashtags.toString());
	}
	
	public static void putOrInc(HashMap<String, Integer> map, String key) {
		if (map.containsKey(key)) map.put(key, map.get(key) + 1);
		else map.put(key, 1);
	}
	
	public static HashMap<String, Integer> collectHashtags(String text) {
		HashMap<String, Integer> hashtags = new HashMap<String, Integer>();
		
		String tmp = "";
		boolean inHashtag = false;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			
			if (c == '#') {
				if (inHashtag) {
					putOrInc(hashtags, tmp);
					tmp = "";
				} else {
					tmp += c;
					inHashtag = true;
				}
			} else if (!(Character.isDigit(c) || Character.isLetter(c) || c == '_')) {
				if (inHashtag) {
					putOrInc(hashtags, tmp);
					tmp = "";
					inHashtag = false;
				}
			} else {
				if (inHashtag) tmp += c;
			}
		}
		
		return hashtags;
	}
	
	public static HashMap<String, Integer> collectMentions(String text) {
		return null;
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
