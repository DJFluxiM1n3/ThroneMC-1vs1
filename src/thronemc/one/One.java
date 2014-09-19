package thronemc.one;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import thronemc.one.commands.SetSpawnCommand;
import thronemc.one.commands.StatsCommand;
import thronemc.one.listener.BlockListener;
import thronemc.one.listener.PlayerListener;
import thronemc.one.task.SignTask;

public class One extends JavaPlugin{
	
	public static String PREFIX = "§7[§a1vs1§7] ";
	public static String MAP = "";
	public static String servername = "";
	
	public static Status STATUS;
	
	public static int ROUND;
	public static One o;
	
	public static Player w;
	public static Player l;
	
	public static boolean MOVE;
	
	@Override
	public void onEnable(){
		o = this;
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new BlockListener(), this);
		pm.registerEvents(new PlayerListener(), this);
		initConfig();
		getCommand("setspawn").setExecutor(new SetSpawnCommand());
		getCommand("stats").setExecutor(new StatsCommand());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(o, new SignTask(), 20L, 2 * 20L);
		
		STATUS = Status.LOBBY;
		MOVE = false;
	}
	
	@Override
	public void onDisable(){
		Bukkit.getScheduler().cancelTasks(this);
		SignTask.setStatus("Restarting");
		for(Player p : Bukkit.getOnlinePlayers()) p.kickPlayer("");
	}
	
	public void initConfig(){
        this.getConfig().addDefault("Mapname", "UNKNOWEN");
		this.getConfig().addDefault("Servername", "FFA0");
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		servername = getConfig().getString("Servername");
		MAP = getConfig().getString("Mapname");
	}
}
