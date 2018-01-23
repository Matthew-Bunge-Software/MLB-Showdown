package testCase;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import gameData.GameStat;

public class GameStatTest {

	private GameStat gamestat;
	
	@Before
	public void setUp() {
		gamestat = new GameStat();
	}
	
	@Test
	public void testInitialState() {
		assertEquals(0, gamestat.getHomeRuns());
		assertEquals(0, gamestat.getAwayRuns());
		assertEquals(0, gamestat.getHomeHits());
		assertEquals(0, gamestat.getAwayHits());
		assertEquals(1, gamestat.getInning());
		assertEquals(0, gamestat.getOuts());
		assertTrue(gamestat.getHalf());
		assertEquals(1, gamestat.getAwaySpread().size());
		int initialAwayRuns = gamestat.getAwaySpread().get(0);
		assertEquals(0, initialAwayRuns);
		assertTrue(gamestat.getHomeSpread().isEmpty());
	}
	
	@Test
	public void testYerOut() {
		assertEquals(0, gamestat.getOuts());
		gamestat.yerOut();
		assertEquals(1, gamestat.getOuts());
		gamestat.yerOut();
		assertEquals(2, gamestat.getOuts());
		gamestat.yerOut();
		assertEquals(3, gamestat.getOuts());
		gamestat.yerOut();
		assertEquals(3, gamestat.getOuts());
	}
	
	@Test
	public void testUpdate() {
		gamestat.yerOut();
		gamestat.update();
		assertEquals(1, gamestat.getOuts());
		assertEquals(1, gamestat.getInning());
		assertTrue(gamestat.getHalf());
		gamestat.yerOut();
		gamestat.yerOut();
		gamestat.update();
		assertEquals(0, gamestat.getOuts());
		assertEquals(1, gamestat.getInning());
		assertFalse(gamestat.getHalf());
		gamestat.yerOut();
		gamestat.yerOut();
		gamestat.yerOut();
		gamestat.update();
		assertEquals(0, gamestat.getOuts());
		assertEquals(2, gamestat.getInning());
		assertTrue(gamestat.getHalf());
	}
}
