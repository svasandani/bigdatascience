import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Util {
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
	
	public static String[] removeStopwords(String[] textArray, String[] EXCLUDE) {
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
	
	public static String removeNonAlphanumeric(String text) {
		String cleanedString = "";
		
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			
			if (Character.isDigit(c) || Character.isLetter(c) || c == ' ')
				cleanedString += c;
		}
		
		return cleanedString;
	}
	
	public static String removePrefixedTokens(String text, char prefix) {		
		String cleanedString = "";
		
		boolean inToken = false;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			
			if (c == prefix) {
				if (!inToken) {
					inToken = true;
				}
			} else if (!(Character.isDigit(c) || Character.isLetter(c) || c == '_')) {
				if (inToken) inToken = false;
				else cleanedString += c;
			} else {
				if (!inToken) cleanedString += c;
			}
		}
		
		return cleanedString;
	}
	
	public static void printProgressBar(int current, int total, int size) {
		System.out.print("[");
		
		double percent = ((double) current) / total;
		int normalized = (int) (percent * size);
		
		for (int i = 0; i < normalized; i++) System.out.print("=");
		for (int i = 0; i < size - normalized; i++) System.out.print(" ");
		
		System.out.print("]\r");
		
	}
}
