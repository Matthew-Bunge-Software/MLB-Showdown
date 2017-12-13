package gameData;

import java.util.ArrayList;

/**
 * General summary stats of where the game is at the current point, primary score and inning.
 * In the future is liable to include other important information like innings charged to a current pitcher.
 * 
 * @author Matthew Bunge
 */

public class GameStat {

   private int outs;
   public int homeRuns;
   public int awayRuns;
   public boolean top;
   private boolean inningEnd;
   public int inning;
   public ArrayList<Integer> homeSpread;
   public ArrayList<Integer> awaySpread;
   
   /**
    * Creates a GameStat representing a brand new game instance.
    */
   public GameStat() {
      outs = 0;
      homeRuns = 0;
      awayRuns = 0;
      top = true;
      inning = 1;
      homeSpread = new ArrayList<>();
      awaySpread = new ArrayList<>();
      awaySpread.add(0);
   }
   
   /**
    * Gets the number of outs in the current frame.
    * 
    * @return The number of outs in the current frame.
    */
   public int getOuts() {
	   return outs;
   }
   
   /**
    * Increments the number of outs.
    */
   public void yerOut() {
	   outs++;
   }
   
   /**
    * Flips over the inning if a third out has been recorded.
    */
   public void update() {
	   if (outs == 3) {
		   if (top) {
			   top = false;
			   homeSpread.add(0);
		   } else {
			   inning++;
			   top = true;
			   awaySpread.add(0);
		   }
		   inningEnd = true;
		   outs = 0;
	   }
   }
   
   /**
    * Increments runs of the current team batting.
    */
   public void score() {
	   if (top) {
		   awayRuns++;
		   awaySpread.add(inning - 1, awaySpread.remove(inning - 1) + 1);
	   } else {
		   homeRuns++;
		   homeSpread.add(inning - 1, homeSpread.remove(inning - 1) + 1);
	   }
   }
   
   /**
    * Indicates whether it's time to turn over the ending.
    * Turns over the inning internally if it is time.
    * 
    * @return true if the inning is over, false otherwise.
    */
   public boolean inningEnd() {
	   if (inningEnd) {
		   inningEnd = false;
		   return true;
	   }
	   return false;
   }
   
   /**
    * 
    */
   public ArrayList<Integer> getHomeSpread() {
	   return homeSpread;
   }
   
   /**
    * 
    */
   public ArrayList<Integer> getAwaySpread() {
	   return awaySpread;
   }
}