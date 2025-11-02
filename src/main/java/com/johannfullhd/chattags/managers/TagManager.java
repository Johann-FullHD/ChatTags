package com.johannfullhd.chattags.managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.johannfullhd.chattags.ChatTags;
import com.johannfullhd.chattags.models.PlayerTag;

/**
 * Manages player tags, including storage and retrieval
 */
public class TagManager {
    
    private final ChatTags plugin;
    private final Map<UUID, PlayerTag> playerTags;
    private File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, Long> lastChangeAt = new ConcurrentHashMap<>();

    public TagManager(ChatTags plugin) {
        this.plugin = plugin;
        this.playerTags = new HashMap<>();
        setupDataFile();
        loadAllTags();
    }
    
    /**
     * Sets up the data file for storing player tags
     */
    private void setupDataFile() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create playerdata.yml", e);
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    /**
     * Loads all player tags from the data file
     */
    private void loadAllTags() {
        ConfigurationSection players = dataConfig.getConfigurationSection("players");
        if (players == null) {
            return;
        }
        
        for (String uuidString : players.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                String tagText = players.getString(uuidString + ".text", "");
                String colorName = players.getString(uuidString + ".color", "GRAY");
                boolean enabled = players.getBoolean(uuidString + ".enabled", false);
                
                ChatColor color = ChatColor.valueOf(colorName);
                PlayerTag tag = new PlayerTag(tagText, color, enabled);
                playerTags.put(uuid, tag);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid data for player " + uuidString);
            }
        }
        
        plugin.getLogger().info("Loaded " + playerTags.size() + " player tags");
    }
    
    /**
     * Saves all player tags to the data file
     */
    public void saveAllTags() {
        for (Map.Entry<UUID, PlayerTag> entry : playerTags.entrySet()) {
            UUID uuid = entry.getKey();
            PlayerTag tag = entry.getValue();
            
            dataConfig.set("players." + uuid + ".text", tag.getTagText());
            dataConfig.set("players." + uuid + ".color", tag.getTagColor().name());
            dataConfig.set("players." + uuid + ".enabled", tag.isEnabled());
        }
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save playerdata.yml", e);
        }
    }
    
    /**
     * Gets a player's tag
     * 
     * @param player The player
     * @return The player's tag
     */
    public PlayerTag getTag(Player player) {
        return playerTags.computeIfAbsent(player.getUniqueId(), k -> new PlayerTag());
    }
    
    /**
     * Sets a player's tag text
     * 
     * @param player The player
     * @param text The tag text
     * @return true if successful
     */
    public boolean setTagText(Player player, String text) {
        // Validate tag text
        if (!isValidTagText(text)) {
            return false;
        }
        PlayerTag tag = getTag(player);
        tag.setTagText(text);
        applyTagAppearance(player);
        markChanged(player);
        saveAllTags();
        return true;
    }
    
    /**
     * Sets a player's tag color
     * 
     * @param player The player
     * @param color The tag color
     * @return true if successful
     */
    public boolean setTagColor(Player player, ChatColor color) {
        if (!isValidTagColor(color)) {
            return false;
        }
        PlayerTag tag = getTag(player);
        tag.setTagColor(color);
        applyTagAppearance(player);
        markChanged(player);
        saveAllTags();
        return true;
    }
    
    /**
     * Toggles a player's tag on/off
     * 
     * @param player The player
     * @return The new enabled state
     */
    public boolean toggleTag(Player player) {
        PlayerTag tag = getTag(player);
        tag.setEnabled(!tag.isEnabled());
        applyTagAppearance(player);
        saveAllTags();
        return tag.isEnabled();
    }
    
    /**
     * Clears a player's tag, removing the text and disabling the tag
     * 
     * @param player The player
     */
    public void clearTag(Player player) {
        PlayerTag tag = getTag(player);
        tag.setTagText("");
        tag.setEnabled(false);
        applyTagAppearance(player);
        markChanged(player);
        saveAllTags();
    }

    // Cooldown helpers
    public boolean canChangeTag(Player player) {
        if (player.hasPermission("chattags.admin") || player.hasPermission("chattags.bypass.cooldown")) {
            return true;
        }
        long last = lastChangeAt.getOrDefault(player.getUniqueId(), 0L);
        long cooldownMs = getChangeCooldownSeconds() * 1000L;
        return System.currentTimeMillis() - last >= cooldownMs;
    }

    public long getRemainingCooldownSeconds(Player player) {
        long last = lastChangeAt.getOrDefault(player.getUniqueId(), 0L);
        long cooldownMs = getChangeCooldownSeconds() * 1000L;
        long remaining = (last + cooldownMs - System.currentTimeMillis() + 999) / 1000;
        return Math.max(0, remaining);
    }

    public void markChanged(Player player) {
        lastChangeAt.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public int getChangeCooldownSeconds() {
        return plugin.getConfig().getInt("tag-settings.change-cooldown-seconds", 30);
    }

    /**
     * Applies the tag appearance to the player's tab list name
     * 
     * @param player The player
     */
    public void applyTagAppearance(Player player) {
        PlayerTag tag = getTag(player);
        String prefix = tag.getFormattedTag();
        String baseName = player.getName();
        String listName = prefix.isEmpty() ? baseName : prefix + " " + baseName;
        try {
            player.setPlayerListName(listName);
        } catch (Exception ignored) {
            player.setPlayerListName(baseName);
        }
        applyScoreboardTeam(player, tag);
    }

    private void applyScoreboardTeam(Player player, PlayerTag tag) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        String raw = player.getUniqueId().toString().replace("-", "");
        String teamName = ("ct_" + raw).substring(0, Math.min(16, ("ct_" + raw).length()));
        Team team = sb.getTeam(teamName);
        if (team == null) {
            try {
                team = sb.registerNewTeam(teamName);
            } catch (IllegalArgumentException ignored) {
                team = sb.getTeam(teamName);
            }
        }
        if (team == null) return;
        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
        ChatColor color = (!tag.isEnabled() || tag.getTagText().isEmpty()) ? ChatColor.WHITE : tag.getTagColor();
        try { team.setColor(color); } catch (Throwable ignored) { /* older APIs */ }
        String pfx = (!tag.isEnabled() || tag.getTagText().isEmpty()) ? "" : tag.getFormattedTag() + " ";
        try { team.setPrefix(pfx); } catch (Throwable ignored) { /* 16-char limit on older */ }
    }
    
    /**
     * Validates tag text
     * 
     * @param text The text to validate
     * @return true if valid
     */
    private boolean isValidTagText(String text) {
        int maxLength = plugin.getConfig().getInt("tag-settings.max-length", 16);
        int minLength = plugin.getConfig().getInt("tag-settings.min-length", 1);
        
        if (text.length() < minLength || text.length() > maxLength) {
            return false;
        }
        
        // Check for disallowed characters
        String allowedPattern = plugin.getConfig().getString("tag-settings.allowed-pattern", "[a-zA-Z0-9_\\s]+");
        if (!text.matches(allowedPattern)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates tag color
     * 
     * @param color The color to validate
     * @return true if valid
     */
    private boolean isValidTagColor(ChatColor color) {
        return color.isColor();
    }
    
    /**
     * Parses a color from a string
     * 
     * @param colorString The color string
     * @return The ChatColor, or null if invalid
     */
    public ChatColor parseColor(String colorString) {
        try {
            ChatColor color = ChatColor.valueOf(colorString.toUpperCase());
            return isValidTagColor(color) ? color : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Gets the maximum tag length from config
     * 
     * @return Maximum tag length
     */
    public int getMaxTagLength() {
        return plugin.getConfig().getInt("tag-settings.max-length", 16);
    }
    
    /**
     * Gets the minimum tag length from config
     * 
     * @return Minimum tag length
     */
    public int getMinTagLength() {
        return plugin.getConfig().getInt("tag-settings.min-length", 1);
    }

    public int clearAllTags() {
        int count = 0;
        for (Map.Entry<UUID, PlayerTag> e : playerTags.entrySet()) {
            PlayerTag t = e.getValue();
            if (!t.getTagText().isEmpty() || t.isEnabled()) {
                t.setTagText("");
                t.setEnabled(false);
                count++;
            }
        }
        saveAllTags();
        for (Player p : Bukkit.getOnlinePlayers()) {
            applyTagAppearance(p);
        }
        return count;
    }

    public int disableAllTags() {
        int count = 0;
        for (Map.Entry<UUID, PlayerTag> e : playerTags.entrySet()) {
            PlayerTag t = e.getValue();
            if (t.isEnabled()) {
                t.setEnabled(false);
                count++;
            }
        }
        saveAllTags();
        for (Player p : Bukkit.getOnlinePlayers()) {
            applyTagAppearance(p);
        }
        return count;
    }

    public void removeScoreboardTeamEntry(Player player) {
        try {
            Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
            String raw = player.getUniqueId().toString().replace("-", "");
            String teamName = ("ct_" + raw).substring(0, Math.min(16, ("ct_" + raw).length()));
            Team team = sb.getTeam(teamName);
            if (team != null) {
                team.removeEntry(player.getName());
                if (team.getEntries().isEmpty()) {
                    try { team.unregister(); } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}
    }
}