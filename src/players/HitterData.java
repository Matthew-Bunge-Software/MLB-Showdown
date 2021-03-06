package players;

import java.util.*;
import gameData.StrategyCard;

/**
 * Implementation of the Hitter type in MLB Showdown
 * 
 * @author Matthew Bunge
 */

public class HitterData extends PlayerData {

	private Range singlePlus;
	private Range triple;
	private int speed;
	private String battingSide;

	/**
	 * Main Constructor for a Hitter
	 * 
	 * @param input
	 *            Scanner containing a single line that needs to be parsed to
	 *            produce the various hitter data points input is assumed to
	 *            have the tab character and newline character as a delimiter
	 */
	public HitterData(Scanner input) {
		String num = input.next().trim();
		if (num.isEmpty()) {
			setNum = Integer.parseInt(input.next().trim());
		} else {
			setNum = Integer.parseInt(num);
		}
		edition = input.next().trim();
		name = input.next().trim();
		input.nextLine();
		setTeam = input.next().trim();
		cost = Integer.parseInt(input.next().trim());
		year = input.next().trim();
		baseMod = Integer.parseInt(input.next().trim());
		speed = parseSpeed(input.next().trim());
		Arrays.fill(positions, -1);
		modifyPositions(input.next().trim());
		input.nextLine();
		String contested = input.next().trim();
		if (contested.length() > 1) {
			modifyPositions(contested);
			battingSide = input.next().trim();
		} else {
			battingSide = contested;
		}
		special = new HashSet<String>();
		if (input.hasNext()) {
			String[] specials = input.next().trim().split(" ");
			for (int i = 0; i < specials.length; i++) {
				special.add(specials[i]);
			}
		}
		input.nextLine();
		input.nextLine();
		strikeout = Range.parseRange(input.next().trim());
		groundout = Range.parseRange(input.next().trim());
		flyout = Range.parseRange(input.next().trim());
		walk = Range.parseRange(input.next().trim());
		single = Range.parseRange(input.next().trim());
		singlePlus = Range.parseRange(input.next().trim());
		twobase = Range.parseRange(input.next().trim());
		triple = Range.parseRange(input.next().trim());
		homer = Range.parseRange(input.next().trim());
		if (input.hasNextLine()) {
			input.nextLine();
		}
	}

	/**
	 * Constructor for a "Dummy" Hitter that actually represents a pitcher This
	 * class has a baseline of information needed to functionally operate as a
	 * hitter using MLB Showdown rules for pitchers hitting
	 * 
	 * @param p
	 *            The PitcherData used to make the dummy hitter
	 */
	public HitterData(PitcherData p) {
		name = p.toString();
		setNum = p.getSetNum();
		baseMod = Integer.MIN_VALUE;
		battingSide = p.getHand();
	}

	/**
	 * Returns whether or not the HitterData is in fact a dummy card for a
	 * pitcher
	 */
	public boolean isPitcher() {
		return (baseMod == Integer.MIN_VALUE);
	}

	/**
	 * Returns the numeric range of a single+ result
	 * 
	 * @return the Range resulting in a single+
	 */
	public Range getSinglePlus() {
		return singlePlus;
	}

	/**
	 * Returns the numeric range of a triple result
	 * 
	 * @return the Range resulting in a triple
	 */
	public Range getTriple() {
		return triple;
	}

	/**
	 * Returns the intger value of a players speed
	 * 
	 * @return the value of the player's speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Returns the side the batter takes at the plate
	 * 
	 * @return A string representation of the side of the plate the player hits
	 *         from
	 */
	public String getBattingSide() {
		return battingSide;
	}

	/**
	 * Verifies what result comes from a diceroll and emit the proper outcome to
	 * the token handler
	 * 
	 * @param dice
	 *            The value of the diceroll being compared against the card
	 */
	public void checkCard(int dice) {
		super.checkCard(dice);
		if (singlePlus.inRange(dice)) {
			StrategyCard.emit("1B+");
		} else if (triple.inRange(dice)) {
			StrategyCard.emit("3B");
		}
	}

	/**
	 * Parses out positions and fielding values from a passed in string
	 * 
	 * @param s The string representing a player position
	 */
	private void modifyPositions(String s) {
		if (s.equals("DH")) {
			return;
		}
		String[] split = s.split("\\+");
		int setVal = Integer.parseInt(split[1]);
		switch (split[0]) {
		case "C":
			positions[2] = setVal;
			break;
		case "1B":
			positions[3] = setVal;
			break;
		case "2B":
			positions[4] = setVal;
			break;
		case "3B":
			positions[5] = setVal;
			break;
		case "SS":
			positions[6] = setVal;
			break;
		case "CF":
			positions[8] = setVal;
			break;
		case "LF-RF":
			positions[9] = setVal;
			positions[7] = setVal;
			break;
		case "OF":
			positions[7] = setVal;
			positions[8] = setVal;
			positions[9] = setVal;
			break;
		case "IF":
			positions[3] = setVal;
			positions[4] = setVal;
			positions[5] = setVal;
			positions[6] = setVal;
		default:
			break;
		}
	}

	/**
	 * Parses the speed token to come up with a numeric value The reason for
	 * this method exisiting is that older cards may represent speed as a letter
	 * rather than a number
	 * 
	 * @param speed
	 *            A string representing the speed, either an int or A,B, or C
	 * @return The numeric value of speed
	 */
	private int parseSpeed(String speed) {
		switch (speed) {
		case "A":
			return 20;
		case "B":
			return 15;
		case "C":
			return 10;
		default:
			return Integer.parseInt(speed);
		}

	}

	/**
	 * Creates a String representation of the Hitter, mirroring what a card
	 * looks like in real life
	 * 
	 * @return A String representing the stats of the Hitter Card
	 */
	public String getCard() {
		String card = "";
		card += "Name:\t" + this + "\n";
		card += "On-Base:\t" + this.baseMod + "\n";
		card += "Speed:\t" + this.speed + "\n";
		card += "Bats:\t" + this.battingSide + "\n";
		card += "Positions:\t";
		for (int i = 0; i < this.positions.length; i++) {
			int val = this.positions[i];
			if (val >= 0) {
				card += Position.abbrFromInt(i) + " = " + val + "\n\t";
			}
		}
		if (card.endsWith("\t")) {
			card = card.substring(0, card.lastIndexOf("\t"));
		}
		card += "\n";
		card += "K:\t" + this.strikeout + "\n";
		card += "GB:\t" + this.groundout + "\n";
		card += "FB:\t" + this.flyout + "\n";
		card += "BB:\t" + this.walk + "\n";
		card += "1B:\t" + this.single + "\n";
		card += "1B+:\t" + this.singlePlus + "\n";
		card += "2B:\t" + this.twobase + "\n";
		card += "3B:\t" + this.triple + "\n";
		card += "HR:\t" + this.homer + "\n";
		return card;
	}
}