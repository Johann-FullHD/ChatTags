package com.johannfullhd.chattags.models;

import org.bukkit.ChatColor;

/**
 * Represents a player's custom chat tag
 */
public class PlayerTag {
    
    private String tagText;
    private ChatColor tagColor;
    private boolean enabled;
    
    /**
     * Creates a new PlayerTag with default values
     */
    public PlayerTag() {
        this.tagText = "";
        this.tagColor = ChatColor.GRAY;
        this.enabled = false;
    }
    
    /**
     * Creates a new PlayerTag with specified values
     * 
     * @param tagText The tag text
     * @param tagColor The tag color
     * @param enabled Whether the tag is enabled
     */
    public PlayerTag(String tagText, ChatColor tagColor, boolean enabled) {
        this.tagText = tagText;
        this.tagColor = tagColor;
        this.enabled = enabled;
    }
    
    /**
     * Gets the formatted tag for display
     * 
     * @return Formatted tag string
     */
    public String getFormattedTag() {
        if (!enabled || tagText.isEmpty()) {
            return "";
        }
        return tagColor + "[" + tagText + "]" + ChatColor.RESET;
    }
    
    // Getters and Setters
    public String getTagText() {
        return tagText;
    }
    
    public void setTagText(String tagText) {
        this.tagText = tagText;
    }
    
    public ChatColor getTagColor() {
        return tagColor;
    }
    
    public void setTagColor(ChatColor tagColor) {
        this.tagColor = tagColor;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}