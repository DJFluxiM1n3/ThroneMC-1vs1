package thronemc.one.task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;

import thronemc.one.One;
import thronemc.one.Status;

public class SignTask implements Runnable {
   	static Connection conn;
	
	public SignTask(){
		openConnection();
	}
	
	public synchronized static void openConnection(){
		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://vweb14.nitrado.net:3306/ni393765_1sql1",
					"ni393765_1sql1", "McDBase123");
		} catch (Exception e) {
		}
	}
	
	public synchronized static void closeConnection(){
		try {
			conn.close();
		} catch (Exception e) {
		}
	}
	
	public static void setMaxPlayers(int max){
		update(max, int.class, "max");
	}
	
	public static void setStatus(String status){
		update(status, String.class, "status");
	}
	
	public static void setMap(String map){
		update(map, String.class, "map");
	}
	
	public static void setOnlinePlayers(int online){
		update(online, int.class, "online");
	}
	
	public static void update(Object ob, Class<?> type, String column){
		try {
			PreparedStatement st = conn.prepareStatement("UPDATE `hubsigns` SET `" + column + "`=? WHERE name=?");
			if(type == int.class){
				st.setInt(1, (int) ob);
			}else if(type == String.class){
				st.setString(1, (String) ob);
			}
			st.setString(2, One.servername);
			st.execute();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public void run() {
		if(One.STATUS == Status.LOBBY){
			if(Bukkit.getMaxPlayers() != Bukkit.getOnlinePlayers().length){
				setStatus("Lobby");
				setMap(One.MAP);
				setOnlinePlayers(Bukkit.getOnlinePlayers().length);
				setMaxPlayers(Bukkit.getMaxPlayers());
			}else{
				setStatus("VIP");
				setMap(One.MAP);
				setOnlinePlayers(Bukkit.getOnlinePlayers().length);
				setMaxPlayers(Bukkit.getMaxPlayers());
			}
		}else if(One.STATUS == Status.INGAME){
			setStatus("Ingame");
			setMap(One.MAP);
			setOnlinePlayers(Bukkit.getOnlinePlayers().length);
			setMaxPlayers(Bukkit.getMaxPlayers());
		}else if(One.STATUS == Status.ENDING){
			setStatus("Restarting");
			setMap("");
			setOnlinePlayers(0);	
			setMaxPlayers(2);
		}
	}

}
