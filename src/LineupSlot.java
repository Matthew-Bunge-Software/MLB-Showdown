import players.HitterData;

/**
 * This class is meant to simulate a baseball lineup in the game MLB Showdown. 
 * In a lineup, each player occupies a spot in the order, 1st through 9th, and
 * plays a position in the field, the 8 hitting field positions and either a DH
 * or a Pitcher depending on AL or NL. This version of MLB Showdown at the moment
 * is only meant to support AL Rules.
 * 
 * @author Matthew
 * @version .1
 */

public class LineupSlot {
   
   private HitterData player;
   public int position; //Correspond to batter's position
   public LineupSlot next;
   
   /**
    * Normal constructor for the lineup slot object. By default next is null.
    * 
    * @param 	p	The Hitter taking the place in the lineup slot
    * @param 	i	The position being played by the player in the slot
    */
   public LineupSlot(HitterData p, int i) {
      player = p;
      position = i;
      next = null;
   }
   
   /**
    * Returns the player occupying the lineup slot
    * 
    * @return	The player occupying the lineup slot
    */
   public HitterData getPlayer() {
	   return player;
   }
   
   /**
    * Sets a new position to be played by the player in the current lineup slot
    * 
    * @param 	p	The new position being played by the person in the lineup slot
    */
   public void setPosition(int p) {
	   position = p;
   }
   
   public void setPlayer(HitterData h) {
	   player = h;
   }
   
   /**
    * Null Constructor
    */
   public LineupSlot() {
   }
}