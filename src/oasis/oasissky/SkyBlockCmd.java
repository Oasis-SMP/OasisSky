package oasis.oasissky;

import oasis.oasissky.IslandIndicesUtil.Indices;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class SkyBlockCmd implements CommandExecutor {

	private OasisSky plugin;
	public SkyBlockCmd(OasisSky plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		World skyworld = plugin.getServer().getWorld("SkyBlock");
		Player player = null;
		if(sender instanceof Player){
			player = (Player) sender;
		}
		
		if (args.length==0) {
			if (plugin.getWorldGuard().getRegionManager(skyworld).getRegion(player.getName()) == null) {
				player.teleport(plugin.spawn);
				Indices points = plugin.util.getNextIslandIndices();
				plugin.getLogger().info(String.valueOf(points.getX()) + ", " + String.valueOf(points.getZ()));
				BlockVector pos1 = BukkitUtil.toVector(plugin.min.clone().add(points.getX()*151, 0, points.getZ()*151).getBlock());
				BlockVector pos2 = BukkitUtil.toVector(plugin.max.clone().add(points.getX()*151, 0, points.getZ()*151).getBlock());
				int x = plugin.spawn.clone().add(points.getX()*151, 0, points.getZ()*151).getBlockX();
				int y = plugin.spawn.clone().add(points.getX()*151, 0, points.getZ()*151).getBlockY();
				int z = plugin.spawn.clone().add(points.getX()*151, 0, points.getZ()*151).getBlockZ();
				String here = String.valueOf(x) + ", " + String.valueOf(y) + ", " + String.valueOf(z);
				plugin.getWorldGuard().getRegionManager(skyworld).addRegion(new ProtectedCuboidRegion(player.getName(),pos2,pos1));
				DefaultDomain domain = new DefaultDomain();
				domain.addPlayer(player.getName());
				plugin.getWorldGuard().getRegionManager(skyworld).getRegion(player.getName()).setOwners(domain);
				plugin.paste(plugin.spawn.clone().add(points.getX()*151, 0, points.getZ()*151), "sbp");
				Location loc = null;
				Location tploc = null;
				try {
					loc = DefaultFlag.SPAWN_LOC.parseInput(plugin.getWorldGuard(), sender, here);
					tploc = DefaultFlag.TELE_LOC.parseInput(plugin.getWorldGuard(), sender, here);
				} catch (InvalidFlagFormat e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				plugin.getWorldGuard().getRegionManager(skyworld).getRegion(player.getName()).setFlag(DefaultFlag.SPAWN_LOC, loc);
				plugin.getWorldGuard().getRegionManager(skyworld).getRegion(player.getName()).setFlag(DefaultFlag.TELE_LOC, tploc);
				try {
					plugin.getWorldGuard().getRegionManager(skyworld).save();
				} catch (ProtectionDatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Chest chest = (Chest) plugin.spawn.clone().add(points.getX()*151+3, 0, points.getZ()*151).getBlock().getState();
				chest.getInventory().addItem(new ItemStack(Material.DIRT,10));
				Vector vec = tploc.getPosition();
				player.teleport(BukkitUtil.toLocation(skyworld, vec));
				return true;
			} else {
				Location flag = plugin.getWorldGuard().getRegionManager(skyworld).getRegion(player.getName()).getFlag(DefaultFlag.TELE_LOC);
				Vector vec = flag.getPosition();
				player.teleport(BukkitUtil.toLocation(skyworld, vec));
				return true;
			}
		}
		return false;
	}

}
