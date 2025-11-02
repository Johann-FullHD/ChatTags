<div align="center">

# 🏷️ ChatTags

### A Modern Chat Tag Customization Plugin for Minecraft

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spigot](https://img.shields.io/badge/Spigot-1.13+-brightgreen.svg)](https://www.spigotmc.org/)
[![Paper](https://img.shields.io/badge/Paper-Supported-00add8.svg)](https://papermc.io/)

**Empower your players to stand out with fully customizable chat tags!**

[Features](#-features) • [Installation](#-installation) • [Commands](#-commands) • [Configuration](#-configuration) • [Building](#-building-from-source)

</div>

---

## 📋 Overview

ChatTags is a comprehensive chat tag customization plugin designed for Spigot and Paper servers. Players can create personalized tags with custom colors that appear in chat messages, the Tab list, and even above their heads using scoreboard teams. With built-in validation, cooldown management, and extensive admin controls, ChatTags offers the perfect balance between player creativity and server management.

## ✨ Features

### 🎨 **Customization**
- **Personalized Tags**: Create unique tags with custom text and colors
- **Color Support**: Full Minecraft color support (1.13+ API)
- **Multiple Display Locations**: Tags appear in chat, Tab list, and as nameplate prefixes

### 🛡️ **Validation & Control**
- **Regex Validation**: Ensure tags meet your server's standards
- **Length Restrictions**: Configurable minimum and maximum tag lengths
- **Cooldown System**: Prevent tag spam with adjustable cooldowns
- **Toggle System**: Players can enable/disable their tags on-the-fly

### 👑 **Admin Tools**
- **Player Management**: Set or clear tags for any player
- **Bulk Operations**: Clear or disable all tags at once
- **Bypass Permissions**: Allow trusted players to skip cooldowns
- **Preview System**: See how tags will look before applying

### ⚡ **Performance**
- **YAML Persistence**: Reliable data storage
- **Smart Caching**: Optimized tag retrieval and management
- **Auto-Save**: Configurable intervals to prevent data loss

## 📦 Installation

1. **Download** the latest release JAR from the [Releases](https://github.com/Johann-FullHD/ChatTags/releases) page
2. **Place** the JAR file into your server's `plugins` folder
3. **Start** or restart your server
4. **Configure** the plugin by editing `plugins/ChatTags/config.yml` (optional)
5. **Enjoy** your new chat tag system!

## 🎮 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/tag set <text>` | Set your personal chat tag | `chattags.set` |
| `/tag color <color>` | Change your tag's color | `chattags.color` |
| `/tag toggle` | Toggle your tag on or off | `chattags.toggle` |
| `/tag preview` | Preview how your tag will look | `chattags.use` |
| `/tag clear` | Remove your current tag | `chattags.clear` |
| `/tag list` | View all online players with tags | `chattags.use` |
| `/tag set <player> <text>` | **[Admin]** Set a tag for another player | `chattags.admin` |
| `/tag clear <player>` | **[Admin]** Clear a player's tag | `chattags.admin` |
| `/tag clear-all` | **[Admin]** Clear all player tags | `chattags.admin` |
| `/tag disable-all` | **[Admin]** Disable all active tags | `chattags.admin` |

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `chattags.*` | Grants all ChatTags permissions | OP |
| `chattags.use` | Allows basic use of `/tag` command | `true` |
| `chattags.set` | Allows setting a personal tag | `true` |
| `chattags.color` | Allows changing tag color | `true` |
| `chattags.toggle` | Allows toggling tag visibility | `true` |
| `chattags.clear` | Allows clearing own tag | `true` |
| `chattags.admin` | Grants access to admin commands | OP |
| `chattags.bypass.cooldown` | Bypasses tag change cooldown | `false` |

## ⚙️ Configuration

Edit `plugins/ChatTags/config.yml` to customize the plugin's behavior:

```yaml
tag-settings:
  min-length: 1                          # Minimum tag length
  max-length: 5                          # Maximum tag length
  allowed-pattern: "[a-zA-Z0-9_\\s]+"    # Regex pattern for allowed characters
  default-color: "GRAY"                   # Default tag color
  change-cooldown-seconds: 30             # Cooldown between tag changes

features:
  allow-color-codes: false                # Allow players to use color codes
  enable-animations: false                # Enable animated tags (future feature)

performance:
  auto-save-interval: 5                   # Auto-save interval in minutes
  cache-tags: true                        # Enable tag caching for performance
```

## 🎨 How Tags Appear

ChatTags displays your custom tags in three key locations:

- **💬 Chat Messages**: `[TAG] <PlayerName> Hello everyone!`
- **📋 Tab List**: `[TAG] PlayerName`
- **👤 Nameplate**: Appears above player's head with color and prefix

## 🔧 Building from Source

### Prerequisites
- ☕ **Java 17** or higher
- 📦 **Maven 3.8+**

### Build Instructions

**Windows / Linux / macOS:**
```bash
mvn clean package
```

**If snapshot cache fails:**
```bash
mvn -U clean package
```

**Output Location:**
```
target/ChatTags-1.1.0.jar
```

## 🌐 Compatibility

- **API Version**: 1.13+ (Scoreboard color features require 1.13 or higher)
- **Tested On**: Spigot/Paper 1.20.4
- **Java Version**: 17+

## 🤝 Contributing

We welcome contributions from the community! Here's how you can help:

1. **🐛 Report Bugs**: Open an issue describing the problem
2. **💡 Suggest Features**: Share your ideas in the issues section
3. **🔧 Submit PRs**: Fork, make changes, and submit a pull request
4. **📖 Improve Docs**: Help us make our documentation better

Please follow the existing code style and conventions when contributing.

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Made with ❤️ for the Minecraft community**

If you find this plugin useful, consider giving it a ⭐!

</div>
