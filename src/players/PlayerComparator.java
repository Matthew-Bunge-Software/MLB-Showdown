package players;

import java.util.Comparator;

public class PlayerComparator implements Comparator<PlayerData> {
	@Override
	public int compare(PlayerData arg0, PlayerData arg1) {
		return arg0.compareTo(arg1);
	}		
}