# AutoSprint Mod

**Effortless sprinting and smarter W-tapping for Minecraft (Fabric).**

AutoSprint is a lightweight, client-side Fabric mod that keeps you sprinting automatically and adds configurable W-tapping after attacks. It's built for PvP players and for anyone who wants smoother movement without holding down a sprint key.

> **Server rules:** Automated sprinting and W-tapping are not allowed on every server. Use this mod only in singleplayer or on servers where it is permitted, and check the server's rules first. You are responsible for how you use it.

## Features

- **Auto Sprint:** hold W and you sprint, with no double-tapping. Sprint switches off when vanilla doesn't allow it: while sneaking, swimming, riding, flying, using an item, blinded or low on hunger.
- **Smart W-Tapping:** after you attack, sprint is briefly dropped and then restored. This resets sprint knockback for stronger follow-up hits and combos.
- **Target Filter:** choose whether W-tapping triggers on everything, only on players, or only on hostile mobs.
- **On-Screen Indicator:** a small HUD label shows "Sprint: ON/OFF" and your current target filter.
- **Saved Settings:** your on/off state and filter persist between sessions.
- **Optional Chat Feedback:** toggle messages appear on the action bar and can be turned off.
- **Lightweight:** runs only on your client and does a few simple checks per tick.

## Controls

| Action | Default Key |
|---|---|
| Toggle AutoSprint / W-Tap | `R` |
| Cycle W-Tap target filter | Unbound (set it in Options > Controls) |

All keys can be changed in Options > Controls > Key Binds, under **Auto Sprint Mod**.

## Configuration

Settings are saved in `config/w-tap_mod.json`, which is created on first launch.

| Option | Values | Description |
|---|---|---|
| `enabled` | `true` / `false` | Master switch for auto sprint and W-tap |
| `targetFilter` | `ALL`, `PLAYERS`, `HOSTILE` | Which targets trigger a W-tap |
| `showHud` | `true` / `false` | Show the on-screen status label |
| `chatFeedback` | `true` / `false` | Show the action-bar message when toggling |
| `wTapTicks` | `1`–`10` | Ticks sprint stays off after a hit (default `2`) |

## Requirements

- Minecraft **1.21.11**
- [Fabric Loader](https://fabricmc.net/use/) **0.20.1** or newer
- [Fabric API](https://modrinth.com/mod/fabric-api)
- Java **21** or newer
- Client-side only; it isn't needed on the server

## Installation

1. Install Fabric Loader for Minecraft 1.21.11.
2. Download the latest `w-tap_mod-<version>.jar` from the **Releases** page. Or download from https://modrinth.com/mod/w-tap_mod
3. Put the jar and Fabric API in your `mods` folder (`%appdata%\.minecraft\mods` on Windows).
4. Launch the game.

## Building from source

```
git clone <your-repo-url>
cd <repo-folder>
gradlew build        # Windows (Command Prompt)
./gradlew build      # macOS, Linux, PowerShell
```

The mod jar appears in `build/libs/`. Use the one **without** `-sources` in its name. To test in a development client, run `gradlew runClient`.

## How it works

- Each client tick, the mod checks whether you can sprint. If so, it turns sprint on. If not, it turns sprint off.
- When you attack a living entity that matches the target filter, sprint is dropped for `wTapTicks` ticks and then restored.
- The mod runs only on your client and sends nothing to the server other than vanilla's normal sprint state.

## License

Released under the [CC0 1.0 Universal](LICENSE) license.
