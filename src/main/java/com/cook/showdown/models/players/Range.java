package com.cook.showdown.models.players;

/**
 * A range represents the numbers from a roll plus an adjustment that will produce
 * a certain outcome in MLB Showdown. Since MLB Showdown involves a lot of dice rolling,
 * this class exists to expedite the process of generating those results and simplifying
 * the construction of virtual player card result boxes.* 
 * 
 * @author Matthew Bunge
 */

public class Range {
   
   private int low;
   private int high;
   
   /**
    * Constructor.
    * 
    * Low and high being 0 are indicative of a range for which no outcome is valid
    * 
    * @param 	low		the lowest number that will produce a result
    * @param 	high	the highest number that will produce a result
    */
   public Range(int low, int high) {
      this.low = low;
      this.high = high;
   }
   
   /**
    * Verifies whether or not a given roll falls within the Range
    * 
    * @param 	roll	the dice roll to be verified
    * @return	true if the roll falls within the range, false if it doesn't
    * 			fall within the range, or if no valid range exists
    */
   public boolean inRange(int roll) {
      if (low != 0) {
         return (roll >= low && roll <= high);
      }
      return false;
   }
   
   /**
    * Returns a string representation of the Range
    * 
    * @return	a String representation of the Range in the form of
    * 			"low - high"
    */
   public String toString() {
	   if (low > 0 && high > 0) {
		   if (low == high) {
			   return "" + high;
		   } else {
			   if (high == Integer.MAX_VALUE) {
				   return low + "+";
			   } else {
				   return low + " - " + high;
			   }
		   }
	   } else {
		   return "-";
	   }
   }
   
   /**
    * Takes a string representation of a Range and creates a corresponding Range object
    * 
    * @param 	s	The String input from the players text file
    * @return	A Range representing the valid results for some play
    */
   public static Range parseRange(String s) {
	   if (s.equals("-")) {
		   return new Range(0, 0);
	   }
	   int startVal;
	   int endVal;
	   if(s.endsWith("+")) {
		   startVal = Integer.parseInt(s.substring(0, s.length() - 1));
		   return new Range(startVal, Integer.MAX_VALUE);
	   } else {
		   String[] ends = s.split("-");
		   startVal = Integer.parseInt(ends[0]);
		   if (ends.length > 1) {
			   endVal = Integer.parseInt(ends[1]);
			   if (endVal == 0) { endVal = startVal; }
 		   } else { // is length 1
			   endVal = startVal;
		   }
		   return new Range(startVal, endVal);
	   }
   }
   
}