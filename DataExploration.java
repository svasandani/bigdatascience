import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

public class DataExploration {
	
	final static int TEXT_COLUMN = 5;

	public static void main(String[] args) throws FileNotFoundException {
		CSVParser c = new CSVParser("sentiment500.csv");
		ArrayList<String[]> table = c.parse();
//		c.preview(2);
		
		Map<String, Integer> hashtags = Util.trimMap(Util.sortMap(collectHashtags(table)), 20);
		Map<String, Integer> mentions = Util.trimMap(Util.sortMap(collectMentions(table)), 20);

		System.out.println(hashtags.toString());
		System.out.println(mentions.toString());
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
						Util.putOrInc(tokens, tmp);
						tmp = "";
					} else {
						tmp += c;
						inToken = true;
					}
				} else if (!(Character.isDigit(c) || Character.isLetter(c) || c == '_')) {
					if (inToken) {
						if (tmp.length() > 1) Util.putOrInc(tokens, tmp);
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
