package com.johannfullhd.chattags.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.johannfullhd.chattags.managers.TagManager;
import com.johannfullhd.chattags.models.PlayerTag;

public class ChatListener implements org.bukkit.event.Listener {
    private final TagManager tagManager;
    
    public ChatListener(TagManager tagManager) {
        this.tagManager = tagManager;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        PlayerTag tag = tagManager.getTag(event.getPlayer());
        String tagPrefix = tag.getFormattedTag();
        String format = (tagPrefix.isEmpty() ? "" : tagPrefix) + "<%1$s> %2$s";
        event.setFormat(format);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        tagManager.applyTagAppearance(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        tagManager.removeScoreboardTeamEntry(event.getPlayer());
    }
}