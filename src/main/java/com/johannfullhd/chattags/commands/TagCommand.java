package com.johannfullhd.chattags.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.johannfullhd.chattags.ChatTags;
import com.johannfullhd.chattags.managers.TagManager;
import com.johannfullhd.chattags.models.PlayerTag;

/**
 * Handles the /tag command
 */
public class TagCommand implements CommandExecutor, TabCompleter {
    
    private final ChatTags plugin;
    private final TagManager tagManager;
    
    public TagCommand(ChatTags plugin, TagManager tagManager) {
        this.plugin = plugin;
        this.tagManager = tagManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check base permission
        if (!player.hasPermission("chattags.use")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "set":
                // Admin: /tag set <player> <text>
                if (args.length >= 3 && player.hasPermission("chattags.admin")) {
                    handleAdminSet(player, args);
                } else {
                    handleSetCommand(player, args);
                }
                break;
            case "color":
            case "colour":
                handleColorCommand(player, args);
                break;
            case "toggle":
                handleToggleCommand(player);
                break;
            case "preview":
                handlePreviewCommand(player);
                break;
            case "clear":
                // Admin: /tag clear <player>
                if (args.length >= 2 && player.hasPermission("chattags.admin")) {
                    handleAdminClear(player, args);
                } else {
                    handleClearCommand(player);
                }
                break;
            case "list":
                handleListCommand(player);
                break;
            case "clear-all":
                if (player.hasPermission("chattags.admin")) handleClearAll(player);
                else player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                break;
            case "disable-all":
                if (player.hasPermission("chattags.admin")) handleDisableAll(player);
                else player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                break;
            case "help":
                sendHelpMessage(player);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown subcommand! Use /tag help for a list of commands.");
                break;
        }
        
        return true;
    }
    
    /**
     * Handles the set subcommand
     */
    private void handleSetCommand(Player player, String[] args) {
        if (!player.hasPermission("chattags.set")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to set your tag!");
            return;
        }
        if (!tagManager.canChangeTag(player)) {
            player.sendMessage(ChatColor.RED + "You must wait " + tagManager.getRemainingCooldownSeconds(player) + "s before changing your tag again.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /tag set <text>");
            return;
        }
        // Join all arguments after "set" to allow spaces
        String tagText = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        if (tagManager.setTagText(player, tagText)) {
            PlayerTag tag = tagManager.getTag(player);
            tag.setEnabled(true);
            tagManager.saveAllTags();
            player.sendMessage(ChatColor.GREEN + "Tag set to: " + tag.getFormattedTag());
        } else {
            player.sendMessage(ChatColor.RED + "Invalid tag text! Must be " + 
                tagManager.getMinTagLength() + "-" + tagManager.getMaxTagLength() + 
                " characters and contain only letters, numbers, and underscores.");
        }
    }
    
    /**
     * Handles the color subcommand
     */
    private void handleColorCommand(Player player, String[] args) {
        if (!player.hasPermission("chattags.color")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to change your tag color!");
            return;
        }
        if (!tagManager.canChangeTag(player)) {
            player.sendMessage(ChatColor.RED + "You must wait " + tagManager.getRemainingCooldownSeconds(player) + "s before changing your tag again.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /tag color <color>");
            player.sendMessage(ChatColor.YELLOW + "Available colors: " + getAvailableColors());
            return;
        }
        
        ChatColor color = tagManager.parseColor(args[1]);
        
        if (color == null) {
            player.sendMessage(ChatColor.RED + "Invalid color! Available colors: " + getAvailableColors());
            return;
        }
        
        if (tagManager.setTagColor(player, color)) {
            PlayerTag tag = tagManager.getTag(player);
            player.sendMessage(ChatColor.GREEN + "Tag color changed to: " + tag.getFormattedTag());
        } else {
            player.sendMessage(ChatColor.RED + "Failed to set tag color!");
        }
    }
    
    /**
     * Handles the toggle subcommand
     */
    private void handleToggleCommand(Player player) {
        if (!player.hasPermission("chattags.toggle")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to toggle your tag!");
            return;
        }
        
        boolean enabled = tagManager.toggleTag(player);
        
        if (enabled) {
            PlayerTag tag = tagManager.getTag(player);
            player.sendMessage(ChatColor.GREEN + "Tag enabled: " + tag.getFormattedTag());
        } else {
            player.sendMessage(ChatColor.YELLOW + "Tag disabled!");
        }
    }
    
    /**
     * Handles the preview subcommand
     */
    private void handlePreviewCommand(Player player) {
        PlayerTag tag = tagManager.getTag(player);
        
        if (tag.getTagText().isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "You don't have a tag set! Use /tag set <text> to create one.");
            return;
        }
        
        player.sendMessage(ChatColor.GOLD + "Tag Preview: " + tag.getFormattedTag() + ChatColor.WHITE + player.getName() + ": Hello world!");
    }
    
    /**
     * Handles the clear subcommand
     */
    private void handleClearCommand(Player player) {
        if (!player.hasPermission("chattags.clear")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to clear your tag!");
            return;
        }
        if (!tagManager.canChangeTag(player)) {
            player.sendMessage(ChatColor.RED + "You must wait " + tagManager.getRemainingCooldownSeconds(player) + "s before changing your tag again.");
            return;
        }
        tagManager.clearTag(player);
        player.sendMessage(ChatColor.GREEN + "Your tag has been cleared!");
    }
    
    /**
     * Handles the list subcommand
     */
    private void handleListCommand(Player player) {
        List<String> lines = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerTag t = tagManager.getTag(p);
            if (!t.getTagText().isEmpty()) {
                lines.add("- " + p.getName() + ": " + t.getFormattedTag() + (t.isEnabled() ? ChatColor.GREEN + "(enabled)" : ChatColor.YELLOW + "(disabled)"));
            }
        }
        if (lines.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No players with tags online.");
        } else {
            player.sendMessage(ChatColor.GOLD + "Players with tags (online):");
            lines.forEach(player::sendMessage);
        }
    }
    
    /**
     * Handles the clear-all subcommand
     */
    private void handleClearAll(Player player) {
        int changed = tagManager.clearAllTags();
        player.sendMessage(ChatColor.GREEN + "Cleared tags for " + changed + " players.");
    }
    
    /**
     * Handles the disable-all subcommand
     */
    private void handleDisableAll(Player player) {
        int changed = tagManager.disableAllTags();
        player.sendMessage(ChatColor.YELLOW + "Disabled tags for " + changed + " players.");
    }
    
    /**
     * Sends the help message
     */
    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GOLD + "========== ChatTags Help ==========");
        player.sendMessage(ChatColor.YELLOW + "/tag set <text>" + ChatColor.WHITE + " - Set your chat tag");
        player.sendMessage(ChatColor.YELLOW + "/tag color <color>" + ChatColor.WHITE + " - Change your tag color");
        player.sendMessage(ChatColor.YELLOW + "/tag toggle" + ChatColor.WHITE + " - Toggle your tag on/off");
        player.sendMessage(ChatColor.YELLOW + "/tag preview" + ChatColor.WHITE + " - Preview your tag");
        player.sendMessage(ChatColor.YELLOW + "/tag clear" + ChatColor.WHITE + " - Clear your tag");
        player.sendMessage(ChatColor.YELLOW + "/tag list" + ChatColor.WHITE + " - List players with tags");
        if (player.hasPermission("chattags.admin")) {
            player.sendMessage(ChatColor.DARK_AQUA + "-- Admin --");
            player.sendMessage(ChatColor.YELLOW + "/tag set <player> <text>" + ChatColor.WHITE + " - Set tag for a player");
            player.sendMessage(ChatColor.YELLOW + "/tag clear <player>" + ChatColor.WHITE + " - Clear a player's tag");
            player.sendMessage(ChatColor.YELLOW + "/tag clear-all" + ChatColor.WHITE + " - Clear tags for all players");
            player.sendMessage(ChatColor.YELLOW + "/tag disable-all" + ChatColor.WHITE + " - Disable tags for all players");
        }
        player.sendMessage(ChatColor.GOLD + "===================================");
    }
    
    /**
     * Gets a formatted string of available colors
     */
    private String getAvailableColors() {
        List<String> colors = Arrays.stream(ChatColor.values())
            .filter(ChatColor::isColor)
            .map(c -> c + c.name().toLowerCase())
            .collect(Collectors.toList());
        
        return String.join(ChatColor.WHITE + ", ", colors);
    }
    
    /**
     * Handles the admin set subcommand
     */
    private void handleAdminSet(Player sender, String[] args) {
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
            return;
        }
        String tagText = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        if (tagManager.setTagText(target, tagText)) {
            PlayerTag tag = tagManager.getTag(target);
            tag.setEnabled(true);
            tagManager.applyTagAppearance(target);
            tagManager.saveAllTags();
            sender.sendMessage(ChatColor.GREEN + "Set tag for " + target.getName() + ": " + tag.getFormattedTag());
            target.sendMessage(ChatColor.YELLOW + "Your tag was set by an admin: " + tag.getFormattedTag());
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid tag text! Must be " +
                tagManager.getMinTagLength() + "-" + tagManager.getMaxTagLength() +
                " characters and contain only allowed characters.");
        }
    }

    /**
     * Handles the admin clear subcommand
     */
    private void handleAdminClear(Player sender, String[] args) {
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
            return;
        }
        tagManager.clearTag(target);
        sender.sendMessage(ChatColor.GREEN + "Cleared tag for " + target.getName());
        target.sendMessage(ChatColor.YELLOW + "Your tag was cleared by an admin.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        boolean isAdmin = sender.hasPermission("chattags.admin");

        if (args.length == 1) {
            completions.addAll(Arrays.asList("set", "color", "toggle", "preview", "clear", "list", "clear-all", "disable-all", "help"));
            return completions.stream()
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if ((sub.equals("color") || sub.equals("colour"))) {
                return Arrays.stream(ChatColor.values())
                    .filter(ChatColor::isColor)
                    .map(c -> c.name().toLowerCase())
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
            if (isAdmin && (sub.equals("set") || sub.equals("clear"))) {
                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
        }

        return completions;
    }
}