package gameData;

import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.util.List;

/* Effects to be tracked:
 * 		Card Result				SO
 * 		IBB Result				IBB
 * 		Chart Used				P/B
 * 		Before Pitch			WAIT
 * 		Anytime
 * 		Before/After DoublePlayAttempt	DPA->DPS/DPF
 * 		Reroll					RR
 */

public class StrategyCard {

	private static List<String> tokens;
	private static Set<StrategyCard> allCards;

	private String name;
	private int num;
	private String year;
	private String offDefUtil;
	private String preCondition;
	private String postEffect;

	public StrategyCard() throws FileNotFoundException {
		this("04 Special Custom");
	}

	public static StrategyCard getRandomCard() {
		return allCards.iterator().next();
	}
	
	public static List<String> getTokens() {
		return tokens;
	}

	public StrategyCard(String f) throws FileNotFoundException {
		Scanner file = new Scanner(new File(f));
		tokens = new ArrayList<String>();
		allCards = new HashSet<StrategyCard>();
		file.useDelimiter("\\t|\\n");
		while (file.hasNext()) {
			allCards.add(StrategyCard.maker(file.nextInt(), file.next(), file.next(), file.next(), file.next(),
					file.next()));
		}
		file.close();
	}
	
	public String getUID() {
		return year + "#" + num;
	}

	public String getPre() {
		return preCondition;
	}

	public String getPost() {
		return postEffect;
	}

	private StrategyCard(int num, String name, String odu, String year, String pre, String post) {
		this.name = name;
		this.num = num;
		offDefUtil = odu;
		this.year = year;
		preCondition = pre;
		postEffect = post;
	}

	public static void emit(String s) {
		tokens.add(s);
	}

	public static StrategyCard maker(int num, String name, String odu, String year, String pre, String post) {
		return new StrategyCard(num, name, odu, year, pre, post);
	}

	public static boolean parsePrecondition(String s) {
		String[] pre = s.split("\\+");
		if (pre.length > 1) {
			if (pre[1].equals("HC")) {
				if (!tokens.get(tokens.size() - 2).equals("HC")) {
					return false;
				}
			} else if (pre[1].equals("PC")) {
				if (!tokens.get(tokens.size() - 2).equals("PC")) {
					return false;
				}
			}
		}
		switch (pre[0]) {
		case "SO": // Strikeout result
			return tokens.get(tokens.size() - 1).equals("SO");
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
		case "1B+": // After 1B+
			return tokens.get(tokens.size() - 1).equals("1BP");
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
		throw new IllegalArgumentException("Thing passed that wasn't a token");
	}

	public static void printLog() {
		System.out.println(tokens);
	}

}
