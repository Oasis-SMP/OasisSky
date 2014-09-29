package oasis.oasissky;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class OasisSky extends JavaPlugin implements CommandExecutor {

	ConsoleCommandSender console;
	Location min;
	Location max;

	@Override
	public void onEnable(){
		this.

		console = this.getServer().getConsoleSender();

		getCommand("test");
		//getCommand("skyblock").setExecutor(new SkyBlockCmd(this));;

		setUpSkyBlock();

	}

	@Override
	public void onDisable(){

	}

	public void setUpSkyBlock(){
		if(getServer().getWorld("SkyBlock")==null){
			getMultiverseCore().getMVWorldManager().addWorld("SkyBlock", Environment.NORMAL, "34134525656", WorldType.NORMAL, false, "WorldGen:SkyBlockWorld");
			Bukkit.getLogger().info(ChatColor.AQUA + "SkyBlock world created and imported into Multiverse!");
			new BukkitRunnable(){

				@Override
				public void run() {
					min = new Location(getServer().getWorld("SkyBlock"),75,0,75);
					max = new Location(getServer().getWorld("SkyBlock"),-75,255,-75);

					BlockVector pos1 = BukkitUtil.toVector(min.getBlock());
					BlockVector pos2 = BukkitUtil.toVector(max.getBlock());
					getWorldGuard().getRegionManager(getServer().getWorld("SkyBlock")).addRegion(new ProtectedCuboidRegion("SkyBlock", pos2, pos1));
					getWorldGuard().getRegionManager(getServer().getWorld("SkyBlock")).addRegion(new GlobalProtectedRegion("__Global__"));
					Bukkit.getLogger().info(ChatColor.AQUA + "SkyBlock spawn region created!");
					Location location = new Location(getServer().getWorld("SkyBlock"),0,63,0);
					paste(location,"skyspawn");
					Bukkit.getLogger().info(ChatColor.AQUA + "Spawn for SkyBlock pasted!");
				}

			}.runTaskLater(this, 400L);
		} else {
			min = new Location(getServer().getWorld("SkyBlock"),75,0,75);
			max = new Location(getServer().getWorld("SkyBlock"),-75,255,-75);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

		if(cmd.getName().equalsIgnoreCase("test")){
			paste(((Player)sender).getLocation(),"test");
			return true;
		}

		return false;
	}

	public void paste(Location location,String name){
		File file = new File("plugins/WorldEdit/schematics/" + name + ".schematic");
		Vector v = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		SchematicFormat format = SchematicFormat.getFormat(file);
		EditSession es = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), 0x3b9ac9ff);

		CuboidClipboard cc = null;
		try {
			cc = format.load(file);
		} catch (IOException | DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			cc.paste(es, v, false);
		} catch (MaxChangedBlocksException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MultiverseCore getMultiverseCore() {
		Plugin plugin = getServer().getPluginManager().getPlugin("Multiverse-Core");

		if (plugin instanceof MultiverseCore) {
			return (MultiverseCore) plugin;
		}

		throw new RuntimeException("MultiVerse not found!");
	}

	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}
}
