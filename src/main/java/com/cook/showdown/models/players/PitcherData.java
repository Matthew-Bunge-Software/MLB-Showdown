package com.cook.showdown.models.players;

import java.util.*;

import com.cook.showdown.gamedata.StrategyCard;

public class PitcherData extends PlayerData {

	private Range popout;
	private int innings;
	private String role;
	private String hand;

	/**
	 * Main Constructor for a Pitcher
	 * 
	 * @param input
	 *            Scanner containing a single line that needs to be parsed to
	 *            produce the various pitcher data points input is assumed to
	 *            have the tab character and newline character as a delimiter
	 */
	public PitcherData(Scanner input) {
		Arrays.fill(positions, -1);
		positions[1] = 0;
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
		innings = Integer.parseInt(input.next().trim());
		role = input.next().trim();
		input.nextLine();
		hand = input.next().trim();
		special = new HashSet<String>();
		if (input.hasNext()) {
			String[] specials = input.next().trim().split(" ");
			for (int i = 0; i < specials.length; i++) {
				special.add(specials[i]);
			}
		}
		input.nextLine();
		if (input.next().trim().equals("SO")) {
			popout = Range.parseRange("-");
			input.nextLine();
		} else {
			input.nextLine();
			popout = Range.parseRange(input.next().trim());
		}
		strikeout = Range.parseRange(input.next().trim());
		groundout = Range.parseRange(input.next().trim());
		flyout = Range.parseRange(input.next().trim());
		walk = Range.parseRange(input.next().trim());
		single = Range.parseRange(input.next().trim());
		input.next();
		twobase = Range.parseRange(input.next().trim());
		input.next();
		homer = Range.parseRange(input.next().trim());
		if (input.hasNextLine()) {
			input.nextLine();
		}
	}

	/**
	 * Returns the role a pitcher holds
	 * 
	 * @return String representing the pitcher's role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Returns the hand the pitcher throws with
	 * 
	 * @return String, either "R" or "L", representing the hand the pitcher throws with
	 */
	public String getHand() {
		return hand;
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
		if (popout.inRange(dice)) {
			StrategyCard.emit("PU");
		}
	}

	// Will probably need to find a new way to track innings pitched so far
	public int checkInnings(int soFar) {
		if (soFar > innings) {
			return innings - soFar;
		}
		return 0;
	}
	
	/**
	 * Implements the abstract method isPitcher from PlayerData
	 * 
	 * This method always returns true for PitcherData
	 */
	public boolean isPitcher() {
		return true;
	}

	/**
	 * Creates a String representation of the Pitcher, mirroring what a card
	 * looks like in real life
	 * 
	 * @return A String representing the stats of the Hitter Card
	 */
	public String getCard() {
		String card = "";
		card += "Name:\t" + this + "\n";
		card += "Control:\t" + this.baseMod + "\n";
		card += "Role:\t" + this.role + "\n";
		card += "Throws:\t" + this.hand + "\n";
		card += "Innings:\t" + this.innings + "\n";
		card += "Positions:\tPitcher\n\n";
		card += "PU:\t" + this.popout + "\n";
		card += "K:\t" + this.strikeout + "\n";
		card += "GB:\t" + this.groundout + "\n";
		card += "FB:\t" + this.flyout + "\n";
		card += "BB:\t" + this.walk + "\n";
		card += "1B:\t" + this.single + "\n";
		card += "2B:\t" + this.twobase + "\n";
		card += "HR:\t" + this.homer + "\n";
		return card;

	}

}