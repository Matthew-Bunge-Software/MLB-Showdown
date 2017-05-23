package gameData;

/**
 * Maintains both totals of outs and runs scored in a game of MLB Showdown.
 * 
 * The current version is only a barebones implementation of a single team coming to bat an
 * infinite number of times for the purposes of the testing interface as it exists as of 
 * version .1. This is certain to be expanded in the future to contain game status in respect
 * to 2 separate teams.
 * 
 * Updates:
 * 
 * .2:
 * -Reworked the class to track runs individually for both teams
 * -Also tracks the current inning frame, and which team is up to bat
 * -Has method calls to internally flip over the inning
 * 
 * @author Matthew
 * @version .1
 */

public class GameStat {

   private int outs;
   public int homeRuns;
   public int awayRuns;
   public boolean top;
   private boolean inningEnd;
   public int inning;
   
   public GameStat() {
      outs = 0;
      homeRuns = 0;
      awayRuns = 0;
      top = true;
      inning = 1;
   }
   
   /**
    * Gets the number of outs in the current frame
    * 
    * @return The number of outs in the current frame
    */
   public int getOuts() {
	   return outs;
   }
   
   /**
    * Increments the number of outs
    */
   public void yerOut() {
	   outs++;
   }
   
   /**
    * Flips over the inning if a third out has been recorded
    */
   public void update() {
	   if (outs == 3) {
		   if (top) {
			   top = false;
		   } else {
			   inning++;
			   top = true;
		   }
		   inningEnd = true;
		   outs = 0;
	   }
   }
   
   /**
    * Increments runs of the current team batting
    */
   public void score() {
	   if (top) {
		   awayRuns++;
	   } else {
		   homeRuns++;
	   }
   }
   
   /**
    * Indcates whether it's time to turn over the ending
    * Turns over the inning internally if it is time
    * 
    * @return True if the ending is over, False otherwise
    */
   public boolean inningEnd() {
	   if (inningEnd) {
		   inningEnd = false;
		   return true;
	   }
	   return false;
   }
}