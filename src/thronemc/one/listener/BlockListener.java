package thronemc.one.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import thronemc.one.One;
import thronemc.one.Status;

public class BlockListener implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		Material m = e.getBlock().getType();
		if (m == Material.WEB || m == Material.FIRE) {
			e.setCancelled(false);
			return;
		} else {
			e.setCancelled(true);
			return;
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		if (One.STATUS != Status.INGAME) {
			e.setCancelled(true);
			return;
		}
		if (e.getBlock().getType().equals(Material.TNT)) {
			e.getBlock().setTypeId(0);
			e.getBlock().getWorld()
					.spawn(e.getBlock().getLocation(), TNTPrimed.class)
					.setFuseTicks(30);
			e.getPlayer().getItemInHand()
					.setAmount(e.getPlayer().getItemInHand().getAmount() - 1);
			e.getPlayer().updateInventory();
			return;
		} else if (e.getBlock().getType().equals(Material.FIRE)
				|| e.getBlock().getType().equals(Material.WEB)) {
			e.setCancelled(false);
			return;
		} else {
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onFade(BlockFadeEvent e) {
		for (World w : Bukkit.getWorlds()) {
			if (e.getBlock().getWorld() == w) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onRain(WeatherChangeEvent e) {
		if (!e.isCancelled()) {
			if (e.toWeatherState()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		e.setCancelled(true);
		e.getEntity().setHealth(0D);
	}

	@EventHandler
	public void onHangingInteract(PlayerInteractEntityEvent e) {
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onLeaveDeacy(LeavesDecayEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockDestroy(EntityExplodeEvent e) {
		e.blockList().clear();
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e) {
		if (e.getCause() != IgniteCause.FLINT_AND_STEEL){
			e.setCancelled(true);
		}else{
			final Block b = e.getBlock();
			Bukkit.getScheduler().runTaskLater(One.o, new Runnable() {

				@Override
				public void run() {
					b.setType(Material.AIR);
				}

			}, 10 * 20L);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onFireDamage(BlockSpreadEvent e){
		if (e.getBlock().getTypeId() != 2 || (e.getBlock().getTypeId() != 3)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent e){
		e.getBlock().setType(e.getBlock().getType());
		e.setCancelled(true);
	}
}
