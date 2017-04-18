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
	
	public StrategyCard(String f) throws FileNotFoundException {
		Scanner file = new Scanner(new File(f));
		tokens = new ArrayList<String>();
		allCards = new HashSet<StrategyCard>();
		file.useDelimiter("\\t|\\n");
		while (file.hasNext()) {
			allCards.add(StrategyCard.maker(file.nextInt(), file.next(), file.next(), file.next(), file.next(), file.next()));
		}
		file.close();
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
		switch (s) {
			case "SO":
				return tokens.get(tokens.size() - 1) == "SO";
			case "BP":
				return tokens.get(tokens.size() - 1) == "BP";
		}
		throw new IllegalArgumentException("Thing passed that wasn't a token");
	}
	
	public static void printLog() {
		System.out.println(tokens);
	}

}
