package thronemc.one.listener;

import net.minecraft.server.v1_7_R3.EnumClientCommand;
import net.minecraft.server.v1_7_R3.PacketPlayInClientCommand;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import thronemc.ThroneAPI;
import thronemc.one.FileManager;
import thronemc.one.One;
import thronemc.one.Status;
import thronemc.one.task.RoundTask;
import thronemc.one.task.SignTask;
import thronemc.utils.BarAPI;
import thronemc.utils.FireworkAPI;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		e.setJoinMessage(null);
		e.getPlayer().setLevel(0);
		e.getPlayer().setHealth(20D);
		e.getPlayer().getInventory().clear();
		e.getPlayer().setGameMode(GameMode.ADVENTURE);
		e.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
		Bukkit.getScheduler().runTaskLater(One.o, new Runnable(){

			@Override
			public void run() {
				Bukkit.broadcastMessage(One.PREFIX + p.getDisplayName()
						+ " joined the game.");
				
			}
			
		}, 20L);
		if (Bukkit.getOnlinePlayers().length <= 1) {
			e.getPlayer().teleport(FileManager.getSpawn(1));
			BarAPI.displayText(e.getPlayer(),
					"§6§oLobby - §aWaiting for a second Player!", 1);
		} else {
			SignTask.setStatus("Ingame");
			e.getPlayer().teleport(FileManager.getSpawn(2));
			Bukkit.getScheduler().runTaskLater(One.o, new Runnable(){

				@Override
				public void run() {
					for (Player all : Bukkit.getOnlinePlayers()) {
						setupInventory(all);
						BarAPI.removeText(all);
						ThroneAPI.getInstance().getOneStats().addGamePlayed(all);
						Player p1 = Bukkit.getOnlinePlayers()[0];
						Player p2 = Bukkit.getOnlinePlayers()[1];
						BarAPI.displayText(all,
								"§a" + p1.getDisplayName() + " §7" + p1.getLevel() + " §8:§7 "
										+ p2.getLevel() + " §a" + p2.getDisplayName(), 1);
					}
					
				}
				
			}, 20L);
			Bukkit.getScheduler().runTaskLater(One.o, new Runnable() {

				@Override
				public void run() {
					Bukkit.broadcastMessage(One.PREFIX + "Time is over.");
					for (Player all : Bukkit.getOnlinePlayers())
						all.kickPlayer("");
					Bukkit.shutdown();
				}

			}, 15 * 60 * 20L);
			RoundTask.newRound();
			One.STATUS = Status.INGAME;
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		e.setQuitMessage(One.PREFIX + e.getPlayer().getDisplayName()
				+ " left the game.");
		if (One.STATUS != Status.INGAME)
			return;
		Bukkit.getScheduler().runTaskLater(One.o, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers())
					win(p);
			}
		}, 20L);
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		e.setLeaveMessage(One.PREFIX + e.getPlayer().getName()
				+ " left the game.");
		if (One.STATUS != Status.INGAME)
			return;
		Bukkit.getScheduler().runTaskLater(One.o, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers())
					win(p);
			}
		}, 20L);
	}

	public void win(Player p) {
		One.STATUS = Status.ENDING;
		Bukkit.getWorld("world").setPVP(false);
		FireworkAPI.spawnFirework(FileManager.getSpawn(1));
		FireworkAPI.spawnFirework(FileManager.getSpawn(2));
		Bukkit.broadcastMessage(One.PREFIX + p.getName() + " won the game.");
		ThroneAPI.getInstance().getOneStats().addWin(p);
		ThroneAPI.getInstance().getCoinSystem().addCoins(p, 10);
		BarAPI.removeText(p);
		BarAPI.displayText(p, "§aYou have won.", 1);
		Bukkit.broadcastMessage(One.PREFIX
				+ "§4Server will restart in 10 secounds.");
		Bukkit.getScheduler().runTaskLater(One.o, new Runnable() {
			@Override
			public void run() {
				Bukkit.shutdown();
			}
		}, 10 * 20L);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(FileManager.getSpawn(2));
		final Player p = e.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(One.o, new Runnable() {
			@Override
			public void run() {
				if (One.STATUS == Status.INGAME)
					setupInventory(p);
			}
		}, 20L);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		final Player p = e.getEntity();
		final Player k = p.getKiller();
		One.w = k;
		ThroneAPI.getInstance().getOneStats().addDeath(p);
		ThroneAPI.getInstance().getOneStats().addKill(k);
		ThroneAPI.getInstance().getCoinSystem().addCoins(k, 1);
		final Damageable kh = (Damageable) k;
		Double h = kh.getHealth();
		e.setKeepLevel(true);
		e.setDroppedExp(0);
		e.getDrops().clear();
		e.setDeathMessage(null);
		k.setLevel(k.getLevel() + 1);
		k.setHealth(20D);
		k.setFireTicks(0);
		if (k.getLevel() == 3) {
			win(k);
			One.MOVE = true;
		} else {
			Bukkit.getScheduler().runTaskLater(One.o, new Runnable() {
				@Override
				public void run() {
					RoundTask.newRound();
				}
			}, 20L);
		}
		p.sendMessage(One.PREFIX + "Your killer was on " + Math.round(h)
				+ " hearts.");
		for (Player all : Bukkit.getOnlinePlayers()) {
			BarAPI.removeText(all);
			Player p1 = Bukkit.getOnlinePlayers()[0];
			Player p2 = Bukkit.getOnlinePlayers()[1];
			BarAPI.displayText(all, "§a" + p1.getDisplayName() + " §7" + p1.getLevel()
					+ " §8:§7 " + p2.getLevel() + " §a" + p2.getDisplayName(), 1);
		}
		Bukkit.getScheduler().runTaskLater(One.o, new Runnable() {
			@Override
			public void run() {
				PacketPlayInClientCommand packet = new PacketPlayInClientCommand(
						EnumClientCommand.PERFORM_RESPAWN);
				CraftPlayer cp = (CraftPlayer) p;
				cp.getHandle().playerConnection.a(packet);
				k.teleport(FileManager.getSpawn(1));
				setupInventory(k);
				BarAPI.removeText(p);
				One.MOVE = false;
				Player p1 = Bukkit.getOnlinePlayers()[0];
				Player p2 = Bukkit.getOnlinePlayers()[1];
				BarAPI.displayText(p,
						"§a" + p1.getDisplayName() + " §7" + p1.getLevel() + " §8:§7 "
								+ p2.getLevel() + " §a" + p2.getDisplayName(), 1);
			}
		}, 20L);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (One.MOVE)
			return;
		if ((e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom()
				.getBlockZ() == e.getTo().getBlockZ()))
			return;
		e.getPlayer().teleport(e.getPlayer().getLocation());
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		e.setCancelled(true);
		e.setFoodLevel(20);
	}

	public static void setupInventory(Player p) {
		Inventory i = p.getInventory();
		i.clear();
		i.setItem(0, new ItemStack(Material.STONE_SWORD, 1));
		i.setItem(1, new ItemStack(Material.FISHING_ROD, 1));
		i.setItem(2, new ItemStack(Material.BOW, 1));
		i.setItem(3, new ItemStack(Material.ARROW, 12));

		ItemStack flint = new ItemStack(Material.FLINT_AND_STEEL, 1);
		flint.setDurability((short) 63);
		i.setItem(8, flint);

		p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
		p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
		p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
		p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if (One.STATUS != Status.LOBBY) {
			e.disallow(Result.KICK_OTHER, "You are not able to join now.");
		}
	}
}
