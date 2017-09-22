package players;

public enum Position {
	
	DesignatedHitter("Designated Hitter", "DH", 0),
	Pitcher("Pitcher", "P", 1),
	Catcher("Catcher", "C", 2),
	FirstBase("1st Baseman", "1B", 3),
	SecondBase("2nd Baseman", "2B", 4),
	ThirdBase("3rd Baseman", "3B", 5),
	Shortstop("Shortstop", "SS", 6),
	LeftField("Left Fielder", "LF", 7),
	CenterField("Center Fielder", "CF", 8),
	RightField("Right Fielder", "RF", 9)
	;
	
	private final String full;
	private final String abbr;
	private final int num;
	
	private Position(String full, String abbr, int num) {
		this.full = full;
		this.abbr = abbr;
		this.num = num;
	}
	
	public String getFull() {
		return full;
	}
	
	public String getAbbr() {
		return abbr;
	}
	
	public int getNum() {
		return num;
	}
	
	public static String abbrFromInt(int i) {
		for (Position p : Position.values()) {
			if (p.num == i) {
				return p.abbr;
			}
		}
		return null;
	}
	
	public static int intFromAbbr(String s) {
		for (Position p : Position.values()) {
			if (s != null && s.equals(p.abbr)) {
				return p.num;
			}
		}
		return 10;
	}
}
