<div align="center">
  <img src="src/main/resources/assets/better_weather/icon.png" width="120" alt="Better Weather icon"/>

  <h1>Better Weather (1.12.2 Forge)</h1>

  <p>Volumetric fluffy clouds and local, moving weather for Minecraft 1.12.2.</p>
</div>

A Minecraft Forge 1.12.2 port of [paulevs' Better Weather](https://github.com/paulevsGitch/BetterWeather),
originally written for Minecraft Beta 1.7.3. All of the original look and behavior is preserved, with
the rendering and simulation rebuilt on the 1.12.2 Forge rendering pipeline.

## Features

- **Volumetric clouds.** Clouds are 32 to 64 blocks tall, fluffy, and softly shaded instead of the flat vanilla sheet.
- **Local weather.** Rain and snow are tied to cloud fronts. Weather moves across the world with the clouds rather than toggling globally.
- **Better rain and snow rendering.** Precipitation uses level of detail and renders over larger distances.
- **Better weather sounds.** Improved rain ambience.
- **Clouds beyond render distance.** Clouds can extend past the terrain view distance without clipping at the far plane.
- **In game config.** Cloud height presets (Beta 108 / vanilla 128 / custom), fluffy toggle, rain color, vanilla rain control, frequent rain, cloud speed, and cloud view distance are editable live under Mods, Better Weather, Config.

## Commands

Because BetterWeather rain is **localized** (rain travels in fronts instead of falling
everywhere at once), `/findrain` helps you locate active weather.

| Command | Description |
|---------|-------------|
| `/findrain [radius] [step]` | Locate the nearest localized rain front. With no arguments it auto-expands the search until rain is found, then prints the coordinates, distance, front strength, and a ready-to-use `/tp`. Optional `radius` (16+) forces a fixed scan range; `step` (1-64) sets the sample spacing (larger = faster but coarser). |
| `/bwtoggle <clouds\|rain\|scroll>` | Toggle a render subsystem on/off (debugging). |
| `/bwdebug` | Print live fog/cloud sample values at your position (debugging). |

These are **client-side** commands: they are handled locally by the client, so they work in
singleplayer and on any server, and do not conflict with server-side command libraries
(such as Brigadier-based mods like brigo).

> Tip: if `vanillaRainControl` is enabled in the config, rain is global and follows the vanilla
> `/weather` command, so `/findrain` will report rain everywhere while it is raining.

## Requirements

- Minecraft 1.12.2
- Minecraft Forge 14.23.5.2860 or newer
- [MixinBooter](https://www.curseforge.com/minecraft/mc-mods/mixinbooter) (the mod loads its mixins through MixinBooter)

## Installation

1. Install Minecraft Forge for 1.12.2.
2. Drop MixinBooter and `better_weather-1.1.jar` into your `mods` folder.
3. Launch the game.

## License

[MIT](LICENSE). The original mod is MIT licensed by paulevs; this port keeps the same license.
