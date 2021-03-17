import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class CSVParser {
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