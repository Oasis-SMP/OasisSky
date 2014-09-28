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

	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id){
		getLogger().info("[OasisSky] " + id);
		return new SkyBlockWorld();
	}

	public void setUpSkyBlock(){
		if(getServer().getWorld("SkyBlock")==null){
			new BukkitRunnable(){

				@Override
				public void run() {
					new WorldCreator("SkyBlock").environment(Environment.NORMAL).generator(new SkyBlockWorld()).type(WorldType.NORMAL).generateStructures(false).createWorld();
					Bukkit.getLogger().info(ChatColor.AQUA + "SkyBlock world created!");
					min = new Location(getServer().getWorld("SkyBlock"),75,0,75);
					max = new Location(getServer().getWorld("SkyBlock"),-75,255,-75);

					BlockVector pos1 = BukkitUtil.toVector(min.getBlock());
					BlockVector pos2 = BukkitUtil.toVector(max.getBlock());
					getWorldGuard().getRegionManager(getServer().getWorld("SkyBlock")).addRegion(new ProtectedCuboidRegion("SkyBlock", pos2, pos1));
					Bukkit.getLogger().info(ChatColor.AQUA + "SkyBlock spawn region created!");
					getMultiverseCore().getMVWorldManager().addWorld("SkyBlock", Environment.NORMAL, "34134525656", WorldType.NORMAL, false, "OasisSky:SkyBlockWorld");
					Bukkit.getLogger().info(ChatColor.AQUA + "SkyBlock imported into Multiverse!");
					File file = new File("bukkit.yml");
					FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
					fileConfig.set("worlds.SkyBlock.generator", "OasisSky:SkyBlockWorld");
					try {
						fileConfig.save(file);
						Bukkit.getLogger().info(ChatColor.AQUA + "SkyBlock world generator set in bukkit.yml!");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}.runTaskLater(this, 400L);
			
			new BukkitRunnable(){

				@Override
				public void run() {
					Location location = new Location(getServer().getWorld("SkyBlock"),0,63,0);
					paste(location,"skyspawn");
					Bukkit.getLogger().info(ChatColor.AQUA + "Spawn for SkyBlock pasted!");
				}

			}.runTaskLater(this, 600L);
		} else {
			new BukkitRunnable(){

				@Override
				public void run() {
					new WorldCreator("SkyBlock").environment(Environment.NORMAL).generator(new SkyBlockWorld()).type(WorldType.NORMAL).generateStructures(false).createWorld();
					min = new Location(getServer().getWorld("SkyBlock"),75,0,75);
					max = new Location(getServer().getWorld("SkyBlock"),-75,255,-75);
				}

			}.runTaskLater(this, 600L);
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
