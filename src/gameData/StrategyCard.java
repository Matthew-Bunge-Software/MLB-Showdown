package gameData;

import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class StrategyCard {

	private static List<String> tokens;
	private static List<StrategyCard> allCards;

	private String name;
	private int num;
	private String year;
	private String offDefUtil;
	private String preCondition;
	private String postEffect;
	private String description;

	public StrategyCard() throws FileNotFoundException {
		this("DataFiles/04 Special Custom");
	}

	public static StrategyCard getRandomCard() {
		Random r = new Random();
		int rand = r.nextInt(allCards.size());
		return allCards.get(rand);
	}
	
	public static List<String> getTokens() {
		return tokens;
	}

	public StrategyCard(String f) throws FileNotFoundException {
		Scanner file = new Scanner(new File(f));
		tokens = new ArrayList<String>();
		allCards = new ArrayList<StrategyCard>();
		file.useDelimiter(Pattern.compile("\\t|\\r\\n"));
		while (file.hasNext()) {
			allCards.add(StrategyCard.maker(file.nextInt(), file.next(), file.next(), file.next(), file.next(),
					file.next(), file.next()));
		}
		file.close();
	}
	
	public String getUID() {
		return year + "#" + num;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUsage() {
		return offDefUtil;
	}

	public String getPre() {
		return preCondition;
	}

	public String getPost() {
		return postEffect;
	}

	private StrategyCard(int num, String name, String odu, String year, String pre, String post, String desc) {
		this.name = name;
		this.num = num;
		offDefUtil = odu;
		this.year = year;
		preCondition = pre;
		postEffect = post;
		this.description = desc;
	}

	public static void emit(String s) {
		tokens.add(s);
	}

	public static StrategyCard maker(int num, String name, String odu, String year, String pre, String post, String desc) {
		return new StrategyCard(num, name, odu, year, pre, post, desc);
	}

	public static boolean parsePrecondition(String s) {
		String[] pre = s.split("\\+");
		if (pre.length > 1) {
			if (tokens.size() > 1) {
				if (pre[1].equals("HC")) {
					if (!tokens.get(tokens.size() - 2).equals("HC")) {
						return false;
					}
				} else if (pre[1].equals("PC")) {
					if (!tokens.get(tokens.size() - 2).equals("PC")) {
						return false;
					} 
				}
			} else {
				return false;
			}
		}
		String[] alternates = pre[0].split("\\^");
		if (alternates.length > 1) {
			for (String alternate : alternates) {
				if (StrategyCard.parsePrecondition(alternate)) {
					return true;
				}
			}
			return false;
		}
		switch (pre[0]) {
		case "SO": // Strikeout result
			return tokens.get(tokens.size() - 1).equals("SO");
		case "RRS": // Before a reroll of a swing
			return tokens.get(tokens.size() - 1).equals("RRS");
		case "RRP": // Before a reroll of a pitch
			return tokens.get(tokens.size() - 1).equals("RRP");
		case "FO": // Flyout result
			return tokens.get(tokens.size() - 1).equals("FO");
		case "BP": // Before the Pitch
			return tokens.get(tokens.size() - 1).equals("BP");
		case "IBB": // After intentional walk
			return tokens.get(tokens.size() - 1).equals("IBB");
		case "BB": // After a normal walk
			return tokens.get(tokens.size() - 1).equals("BB");
		case "BDP": // Before double play attempt
			return tokens.get(tokens.size() - 1).equals("BDP");
		case "ADP":	// After double play attempt
			return tokens.get(tokens.size() - 1).equals("ADP");
		case "1B": // After a single
			return tokens.get(tokens.size() - 1).equals("1B");
		case "1B+": // After 1B+
			return tokens.get(tokens.size() - 1).equals("1BP");
		case "2B": // After a double
			return tokens.get(tokens.size() - 1).equals("2B");
		case "3B": // After 3B
			return tokens.get(tokens.size() - 1).equals("3B");
		case "HR": // After HR
			return tokens.get(tokens.size() - 1).equals("HR");
		case "A": // Any
			return true;
		case "AO": // Any out
			String token = tokens.get(tokens.size() - 1);
			return token.equals("PU") || token.equals("FO") || token.equals("SO") || token.equals("GO");
		}
		throw new IllegalArgumentException(s + "isn't a legal token");
	}

	public static void printLog() {
		System.out.println(tokens);
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
