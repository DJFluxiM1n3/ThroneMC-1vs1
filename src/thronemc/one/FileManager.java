package thronemc.one;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class FileManager {
	
	public static FileConfiguration cfg = One.o.getConfig();

	public static Location getSpawn(int i){
		try {
			String world = cfg.getString("spawn."+i+".world");
			double x = cfg.getDouble("spawn."+i+".x");
			double y = cfg.getDouble("spawn."+i+".y");
			double z = cfg.getDouble("spawn."+i+".z");
			double yaw = cfg.getDouble("spawn."+i+".yaw");
			double pitch = cfg.getDouble("spawn."+i+".pitch");
			
			Location loc = new Location(Bukkit.getWorld(world),x ,y ,z);
			
			loc.setYaw((float) yaw);
			loc.setPitch((float) pitch);
			return loc;
		} catch (Exception e) {
		}
		return null;
	}
	
	public static void setSpawn(int i, Location loc){
		String world = loc.getWorld().getName();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		double yaw = loc.getYaw();
		double pitch = loc.getPitch();
		
		cfg.set("spawn."+i+".world", world);
		cfg.set("spawn."+i+".x", x);
		cfg.set("spawn."+i+".y", y);
		cfg.set("spawn."+i+".z", z);
		cfg.set("spawn."+i+".yaw", yaw);
		cfg.set("spawn."+i+".pitch", pitch);
		One.o.saveConfig();
	}
}
