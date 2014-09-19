package thronemc.one.task;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import thronemc.one.One;

public class RoundTask {

	static int i;

	public RoundTask() {
		i = 8;
	}

	public static void newRound() {
		i = 8;
		One.ROUND = Bukkit.getScheduler().scheduleSyncRepeatingTask(One.o,
				new Runnable() {
					@Override
					public void run() {
						if(i < 4 && i != 0){
							for(Player p : Bukkit.getOnlinePlayers()){
								p.playSound(p.getLocation(), Sound.LEVEL_UP, 20F, 20F);
							}
						}
						if (i == 0) {
							Bukkit.broadcastMessage(One.PREFIX
									+ "§8New round starts.");
							Bukkit.getScheduler().cancelTask(One.ROUND);
							for(Player p : Bukkit.getOnlinePlayers()){
								p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 20F, 20F);
							}
							One.MOVE = true;
							return;
						}
						Bukkit.broadcastMessage(One.PREFIX
								+ "New round starts in " + i + " seconds.");
						i--;
					}
				}, 20L, 20L);
	}
}
