package oasis.oasissky;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class SkyBlockCmd implements CommandExecutor {

	private OasisSky plugin;
	public SkyBlockCmd(OasisSky plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if(sender instanceof Player){
			player = (Player) sender;
		}
		if(plugin.getWorldGuard().getRegionManager(player.getWorld()).getRegion(player.getName())==null){
//			plugin.getWorldGuard().getRegionManager(player.getWorld()).getRegion("test")
		}
		return false;
	}

}
