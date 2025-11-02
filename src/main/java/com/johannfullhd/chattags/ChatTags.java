package com.johannfullhd.chattags;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.johannfullhd.chattags.commands.TagCommand;
import com.johannfullhd.chattags.listeners.ChatListener;
import com.johannfullhd.chattags.managers.TagManager;

/**
 * ChatTags - A comprehensive chat tag customization plugin
 * 
 * @author Johann-FullHD
 * @version 1.0.0
 */
public class ChatTags extends JavaPlugin {
    
    private TagManager tagManager;
    
    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Initialize managers
        tagManager = new TagManager(this);
        
        // Register commands
        getCommand("tag").setExecutor(new TagCommand(this, tagManager));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new ChatListener(tagManager), this);
        
        // Apply appearance for already online players (e.g., after /reload)
        for (Player p : getServer().getOnlinePlayers()) {
            tagManager.applyTagAppearance(p);
        }
        
        // Log successful startup
        getLogger().info("ChatTags has been enabled successfully!");
        getLogger().info("Version: " + getDescription().getVersion());
    }
    
    @Override
    public void onDisable() {
        // Save all player data
        if (tagManager != null) {
            tagManager.saveAllTags();
        }
        
        getLogger().info("ChatTags has been disabled. All data saved.");
    }
    
    /**
     * Gets the TagManager instance
     * 
     * @return TagManager instance
     */
    public TagManager getTagManager() {
        return tagManager;
    }
}