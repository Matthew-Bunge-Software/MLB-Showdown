package players;

/**
 * An enumeration of all positions in a baseball lineup / on a baseball field.
 * Each item holds the full name of the position, the abbreviation, and the
 * number found on a baseball scorecard. Useful for translation
 * 
 * @author Matthew Bunge
 *
 */

public enum Position {

	DesignatedHitter("Designated Hitter", "DH", 0), Pitcher("Pitcher", "P", 1), Catcher("Catcher", "C", 2), FirstBase(
			"1st Baseman", "1B", 3), SecondBase("2nd Baseman", "2B", 4), ThirdBase("3rd Baseman", "3B",
					5), Shortstop("Shortstop", "SS", 6), LeftField("Left Fielder", "LF",
							7), CenterField("Center Fielder", "CF", 8), RightField("Right Fielder", "RF", 9);

	private final String full;
	private final String abbr;
	private final int num;

	/**
	 * Constructor of a position taking the full name, abbreviation, and number
	 * 
	 * @param full
	 *            The full name of the position
	 * @param abbr
	 *            The abbreviation of the position
	 * @param num
	 *            The number of the position on a scorecard
	 */
	private Position(String full, String abbr, int num) {
		this.full = full;
		this.abbr = abbr;
		this.num = num;
	}

	/**
	 * Gets the full name of the position
	 * 
	 * @return A string representing the full name of the position
	 */
	public String getFull() {
		return full;
	}

	/**
	 * Gets the abbreviation of the position
	 * 
	 * @return A string representing the abbreviation of the position
	 */
	public String getAbbr() {
		return abbr;
	}

	/**
	 * Gets the number of the position
	 * 
	 * @return A int representing the number of the position
	 */
	public int getNum() {
		return num;
	}

	/**
	 * Translates an int representing a position to that positions abbreviation
	 * 
	 * @param i
	 *            The int value of the position
	 * @return The abbreviation of the position relating to the passed int, null
	 *         if passed int does not correspond to an actual position
	 */
	public static String abbrFromInt(int i) {
		for (Position p : Position.values()) {
			if (p.num == i) {
				return p.abbr;
			}
		}
		return null;
	}

	/**
	 * Translates an abbreviation representing a position to that positions
	 * numeric value
	 * 
	 * @param s
	 *            The String abbreviation of the position
	 * @return The number of the position related to the passed String, 10 if
	 *         passed abbreviation does not correspond to an actual position (10
	 *         is not a real position in baseball)
	 */
	public static int intFromAbbr(String s) {
		for (Position p : Position.values()) {
			if (s != null && s.equals(p.abbr)) {
				return p.num;
			}
		}
		return 10;
	}
}
