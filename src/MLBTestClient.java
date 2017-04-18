import java.util.*;

import gameData.Field;
import gameData.GameStat;
import gameData.LineupManager;
import players.HitterData;
import players.PitcherData;

import java.io.*;

public class MLBTestClient {
   
   public static void main(String[] args) throws FileNotFoundException {
      Random r = new Random();
      Set<HitterData> hitterPool = new HashSet<HitterData>();
      Set<PitcherData> pitcherPool = new HashSet<PitcherData>();
      Field grass = new Field();
      Scanner input = new Scanner(new File("04BaseHitters.txt"));
      Scanner input2 = new Scanner(new File("04BasePitchers.txt"));
      PitcherData Washburn = new PitcherData(new Scanner(new File("PitcherTest.txt")));
      
      //Creates draft pool of hitters from txt
      
      while (input.hasNext()) {
         try {
            HitterData test = new HitterData(input);
            hitterPool.add(test);
            //ystem.out.println(test + " has been added to the bench");
         } catch (NullPointerException e) {
            System.out.println("Wrong number of values in txt");
         } catch (IllegalArgumentException f) {
            System.out.println("Incorrect input in txt");
         }          
         input.nextLine();
      }
      
      //Creates draft pool of pitchers from txt
      
      while (input2.hasNext()) {
         try {
            PitcherData test = new PitcherData(input2);
            pitcherPool.add(test);
            //System.out.println(test + " has been added to the bullpen");
         } catch (NullPointerException e) {
            System.out.println("Wrong number of values in txt");
         } catch (IllegalArgumentException f) {
            System.out.println("Incorrect input in txt");
         }          
         input2.nextLine();
      }
      LineupManager far = new LineupManager(hitterPool);
      LineupManager team2 = new LineupManager(hitterPool);
      
      
      GameStat chuck = new GameStat();
      Scanner stall = new Scanner(System.in);
      int inning = 1;
      while (true) {
         stall.nextLine();
         int roll = roll(r) + Washburn.checkInnings(inning);
         if (Washburn.getBaseMod() + roll >= far.getCurrent().getBaseMod()) {
            System.out.println("Pitcher's Advantage");
            chuck = Washburn.checkCard(grass, roll(r), far.getCurrent(), chuck, team2);
         } else { 
            System.out.println(far.getCurrent() + " is in his head today");
            chuck = far.getCurrent().checkCard(grass, roll(r), chuck, team2);
         }
         System.out.println("They have scored " + chuck.runs + " runs");
         System.out.println("There are " + chuck.outs + " outs");
         if (chuck.outs % 3 == 0 && chuck.outs != 0) {
            grass.clear();
            chuck.outs = 0;
            inning++;
         }
         far.next();
         grass.getState();
         System.out.println("It is the " + inning + " inning");
      }
      
      
      /*
      for (int i = 1; i < 50; i++) {
         System.out.println(far.getCurrent());
         far.next();
      }
      System.out.println(bullpen);
      */

      
     /* System.out.println("The first batter up is: " + far.getCurrent());
      int dice = 20;
      System.out.println(dice);
      int run = 0;
      if (far.getCurrent().getHomer().inRange(dice)) {
         run += grass.homer();
      }
      if (far.getCurrent().getDouble().inRange(dice)) {
         run += grass.twoBase(far.getCurrent());
      }
      System.out.println(run);
      far.next();
      System.out.println("The new batter is: " + far.getCurrent());
      dice = 20;
      if (far.getCurrent().getHomer().inRange(dice)) {
         run += grass.homer();
      }
      System.out.println(run);
    */
   }
      
   private static int roll(Random r) {
      return r.nextInt(20) + 1;
   }
            
      //Assorted Tests//
      
      //HitterData test = new HitterData(input);
      //bullpen.add(test);
      //System.out.println(bullpen.contains(test));
      //PitcherData test = new PitcherData(input);
      //input.nextLine();
      //PitcherData zitoTest = new PitcherData(input);
      //bullpen.add(test);
      //bullpen.add(zitoTest);
      //System.out.println(bullpen.contains(test));
      //System.out.print(bullpen.contains(zitoTest));  
}