# ChatTags - Minecraft Spigot/Paper Plugin

A comprehensive chat tag customization plugin for Spigot/Paper servers. Players can create tags with colors that appear in chat, the Tab list, and above their name (via scoreboard teams).

## Features
- Custom tags with colors and validation (regex, min/max length)
- Chat format: [TAG]<PLAYER> Message
- Tag shown in Tab list and nameplate (scoreboard team prefix)
- Team color matches tag color (1.13+ API)
- Toggle tags on/off, preview tags
- Admin tools: set/clear other players, bulk clear/disable
- Cooldown for changing/clearing tags (configurable)
- Persistent YAML storage, caching
- Permissions for fine-grained control

## Commands
- /tag set <text> – Set your chat tag
- /tag color <color> – Change your tag color
- /tag toggle – Toggle your tag on/off
- /tag preview – Preview how your tag looks
- /tag clear – Clear your tag
- /tag list – List online players with tags
- /tag set <player> <text> – Admin: set tag for a player
- /tag clear <player> – Admin: clear a player's tag
- /tag clear-all – Admin: clear tags for all players
- /tag disable-all – Admin: disable all tags

## Permissions
- chattags.* – All permissions (OP)
- chattags.use – Use /tag (true)
- chattags.set – Set your tag (true)
- chattags.color – Change tag color (true)
- chattags.toggle – Toggle tag (true)
- chattags.clear – Clear tag (true)
- chattags.admin – Admin functions (OP)
- chattags.bypass.cooldown – Bypass change cooldown (false)

## Configuration (plugins/ChatTags/config.yml)
```yaml
tag-settings:
  min-length: 1
  max-length: 5
  allowed-pattern: "[a-zA-Z0-9_\\s]+"
  default-color: "GRAY"
  change-cooldown-seconds: 30
features:
  allow-color-codes: false
  enable-animations: false
performance:
  auto-save-interval: 5
  cache-tags: true
```

## Installation
1) Download the latest release JAR from GitHub Releases
2) Put it into your server's plugins folder
3) Start or restart the server
4) Edit plugins/ChatTags/config.yml as needed

## Build from Source
- Requirements: Java 17, Maven 3.8+
- Build:
  - Windows: mvn clean package
  - If snapshots cache fails: mvn -U clean package
- Output JAR: target/ChatTags-1.1.0.jar

## Compatibility
- api-version: 1.13 (Scoreboard colors require 1.13+)
- Tested with Spigot/Paper 1.20.4

## How it renders
- Chat: [TAG]<PLAYER> Hello!
- Tab list: [TAG] PLAYER (as player list name)
- Nameplate: set via scoreboard team prefix and color

## Contributing
- Open issues and PRs on GitHub
- Follow code style from the project

## License
MIT License – see LICENSE