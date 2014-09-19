package thronemc.one.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import thronemc.ThroneAPI;

public class StatsCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args){
		Player p = (Player)sender;
		p.sendMessage("");
		p.sendMessage(" §7» §a§l1vs1 §r§aStats");
		p.sendMessage("");
		p.sendMessage(" §ePoints: §b" + ThroneAPI.getInstance().getOneStats().getPoints(p));
		p.sendMessage(" §eKills: §b" + ThroneAPI.getInstance().getOneStats().getKills(p));
		p.sendMessage(" §eDeaths: §b" + ThroneAPI.getInstance().getOneStats().getDeaths(p));
		p.sendMessage(" §eWins: §b" + ThroneAPI.getInstance().getOneStats().getWins(p));
		p.sendMessage(" §eGames played: §b" + ThroneAPI.getInstance().getOneStats().getGamesPlayed(p));
		p.sendMessage("");
		return true;
	}
}
