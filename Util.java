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
	
}
