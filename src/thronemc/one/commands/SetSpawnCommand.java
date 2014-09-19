package thronemc.one.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import thronemc.one.FileManager;
import thronemc.one.One;

public class SetSpawnCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args){
		Player p = (Player)sender;
		if(!p.hasPermission("one.setspawn")){
			p.sendMessage(One.PREFIX + "You dont have permissions to execute this command.");
			return true;
		}
		if(args.length == 1){
			if(args[0].equalsIgnoreCase("1")){
				FileManager.setSpawn(1, p.getLocation());
				p.sendMessage(One.PREFIX + "Spawn 1 set.");
			}else if(args[0].equalsIgnoreCase("2")){
				FileManager.setSpawn(2, p.getLocation());
				p.sendMessage(One.PREFIX + "Spawn 2 set.");
			}else{
				sender.sendMessage(One.PREFIX + "§o/setspawn <1|2>");
			}
			return true;
		}else{
			sender.sendMessage(One.PREFIX + "§o/setspawn <1|2>");
			return true;
		}
	}

}
