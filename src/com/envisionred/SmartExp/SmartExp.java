package com.envisionred.SmartExp;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import MetricsDependencies.Metrics;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;



/**
 *
 * @author EnvisionRed
 */
public class SmartExp extends JavaPlugin{
    SmartExp plugin = this;
    PluginDescriptionFile pdfile = this.getDescription();
    private FileConfiguration blockConfig = null;
    private File blocksFile = null;
    private FileConfiguration mobConfig = null;
    private File mobsFile = null;
@Override
public void onDisable() {
    Logger log = plugin.getLogger();
    log.info("EnvisionRed's SmartExp disabled :(");
    
}
@Override
public void onEnable() {
Logger log = plugin.getLogger();
    log.info("EnvisionRed's SmartExp Enabled :D");
    Enableconfigs();
    StartMetrics();
    getServer().getPluginManager().registerEvents(new MobEvents(this), this);
    getServer().getPluginManager().registerEvents(new BlockEvents(this), this);
}
@Override
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("exp")) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.GREEN + "SmartExp version " + pdfile.getVersion() + " by" + ChatColor.DARK_RED + " EnvisionRed");
            sender.sendMessage(ChatColor.GREEN + "Do " + ChatColor.AQUA + "/exp help " + ChatColor.GREEN + "to see help for the plugin.");
            return true;
        }
        if (args.length >= 1) {
        if (args[0].equalsIgnoreCase("help")) {
            Player player = (Player) sender;
            ShowHelp(player);            
            return true;
        }
        if (args[0].equalsIgnoreCase("check")) {
            if (!sender.hasPermission("SmartExp.check")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
            }
            if (sender.hasPermission("SmartExp.check.other")) {
               if (args.length > 1) {
                   Player player = (this.getServer().getPlayer(args[1]));
                   if (player == null) {
                       sender.sendMessage(ChatColor.RED + "Invalid player: " + args[1]);
                       return true;
                   }
                   float xp = player.getExp();
            int level = player.getLevel();
            int nextlevel = level + 1;
            float xpNew = xp * (100);
            int xpPercent = Math.round(xpNew);
            String playerName = player.getName();
            sender.sendMessage(ChatColor.BLUE  + playerName + ChatColor.GREEN + " is currently level " 
                    + ChatColor.RED + level + ChatColor.GREEN 
                    + " and " + ChatColor.AQUA + xpPercent + "%"
                    + ChatColor.GREEN + " of the way to level " 
                    + ChatColor.RED + nextlevel);
            return true;
               } 
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can not be sent from the console.");
            return true;
            }
            Player player = (Player) sender;
            float xp = player.getExp();
            int level = player.getLevel();
            int nextlevel = level + 1;
            float xpNew = xp * (100);
            int xpPercent = Math.round(xpNew);
            player.sendMessage(ChatColor.GREEN + "You are currently level " 
                    + ChatColor.RED + level + ChatColor.GREEN 
                    + " and " + ChatColor.AQUA + xpPercent + "%"
                    + ChatColor.GREEN + " of the way to level " 
                    + ChatColor.RED + nextlevel);
            return true;
        
        }
       if (args[0].equalsIgnoreCase("reload")) {
           if (!sender.hasPermission("SmartExp.reload")) {
               sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
               return true;
           }
           this.reloadConfig();
           sender.sendMessage(ChatColor.GREEN + "SmartExp config reloaded.");
           return true;
       } 
        return true;
    }
    }
    return false;
}



//Methods for OnEnable
public void Enableconfigs() {
            File mainconfigFile = new File(this.getDataFolder() + "/config.yml");
            blocksFile = new File(this.getDataFolder() + "/Blocks.yml");
            mobsFile = new File(this.getDataFolder()+ "/Mobs.yml");
            int cfgversion = this.getConfig().getInt("seriously-do-not-change-this");
if(!mainconfigFile.exists())
         {
         this.saveDefaultConfig();
        }
if(!blocksFile.exists()) {
	this.getBlocksConfig().options().copyDefaults(true);
	this.saveBlocksConfig();
}
if(!mobsFile.exists()) {
	this.getMobsConfig().options().copyDefaults(true);
	this.saveMobsConfig();
}
  //check if the config version is different
            if (cfgversion != 5) {
                mainconfigFile.delete();
                this.saveDefaultConfig();
            }    
}

public void StartMetrics() {
    Logger log = plugin.getLogger();
    boolean noMetrics = this.getConfig().getBoolean("opt-out-metrics", false);
        if (this == null) {
            log.warning("Plugin is null so metrics failed to start");
        }
   else if (noMetrics == true) {
        log.info("Metrics will not start because you have opted out.");
    } else {
        try {
               Metrics metrics = new Metrics(this);
           metrics.start();
           log.info("Metrics started successfully for SmartExp");
        }catch (IOException x) {
            log.warning("Metrics failed to start for SmartExp");
        }
    }
}
public void ShowHelp(Player player) {
    if (player.hasPermission("SmartExp.check")) {
    player.sendMessage(ChatColor.AQUA + "/exp check:" + ChatColor.GREEN + " Shows you your Exp stats.");
    }
    if (player.hasPermission("SmartExp.reload")) {
    player.sendMessage(ChatColor.AQUA + "/exp reload:" + ChatColor.GREEN + " Reloads the configuration");
    }
    else{
        player.sendMessage(ChatColor.RED + "Sorry, you don't have permission for "
                + "any of the commands for SmartExp, so no use showing the help page to you.");
    }
}
public void reloadBlocksConfig() {
    if (blocksFile == null) {
    blocksFile = new File(plugin.getDataFolder(), "Blocks.yml");
    }
    blockConfig = YamlConfiguration.loadConfiguration(blocksFile);
    InputStream blocksConfigStream = plugin.getResource("Blocks.yml");
    if (blocksConfigStream != null) {
        YamlConfiguration blocksDefault = YamlConfiguration.loadConfiguration(blocksConfigStream);
        blockConfig.setDefaults(blocksDefault);
    }
}
public void reloadMobsConfig() {
    if (mobsFile == null) {
    mobsFile = new File(plugin.getDataFolder(), "Mobs.yml");
    }
    mobConfig = YamlConfiguration.loadConfiguration(mobsFile);
    InputStream mobsConfigStream = plugin.getResource("Mobs.yml");
    if (mobsConfigStream != null) {
        YamlConfiguration mobsDefault = YamlConfiguration.loadConfiguration(mobsConfigStream);
        mobConfig.setDefaults(mobsDefault);
    }
}
public FileConfiguration getBlocksConfig() {
    if (blockConfig == null) {
        this.reloadBlocksConfig();
    }
    return blockConfig;
}
public FileConfiguration getMobsConfig() {
    if (mobConfig == null) {
        this.reloadMobsConfig();
    }
    return mobConfig;
}
public void saveBlocksConfig() {
    Logger log = this.getLogger();
    if (blockConfig == null || blocksFile == null) {
        return;
    }
    try {
        blockConfig.save(blocksFile);
    } catch (IOException x) {
        log.severe("Could not save the blocks config file.");
    }
}
public void saveMobsConfig() {
    Logger log = this.getLogger();
    if (mobConfig == null || mobsFile == null) {
        return;
    }
    try {
        mobConfig.save(mobsFile);
    } catch (IOException x) {
        log.severe("Could not save the mobs config file.");
    }
}

}
