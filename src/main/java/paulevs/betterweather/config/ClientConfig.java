package paulevs.betterweather.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulevs.betterweather.client.rendering.CloudRenderer;

import java.io.File;

@SideOnly(Side.CLIENT)
public class ClientConfig {
	private static final Config CONFIG = new Config(new File("config/better_weather/client.cfg"));
	private static boolean fluffyClouds;
	private static GameSettings options;
	private static boolean betaRainColor;
	private static int cloudViewDistance;
	private static boolean waterHidesClouds;

	public static void init() {
		defineEntries();
		CONFIG.save();
		loadFields();
	}

	/** Define every entry with its hard-coded default. Reused by {@link #resetToDefaults()}. */
	private static void defineEntries() {
		CONFIG.addEntry("fluffyClouds", true,
			"Render clouds fluffy, if false clouds will use block-like rendering",
			"Default value is true"
		);
		CONFIG.addEntry("cloudViewDistanceChunks", 9,
			"How far clouds render, as a radius in 32-block cloud tiles.",
			"Clouds draw without fog, so higher values let you watch distant rain fronts approach,",
			"beyond the terrain render distance.",
			"Minimum 4. There is no soft cap: values above " + CloudRenderer.LAG_WARN_RADIUS
				+ " allocate a lot of cloud geometry and can cause HEAVY LAG (the in-game",
			"slider warns past that point). Hard ceiling is " + CloudRenderer.HARD_MAX_RADIUS
				+ " (~" + (CloudRenderer.HARD_MAX_RADIUS * 32) + " blocks).",
			"Default value is 9"
		);
		CONFIG.addEntry("b173RainColor", true,
			"Rain color style.",
			"true = \"b1.7.3 rain color\": the original Beta 1.7.3 light-blue rain hue (faithful to the source mod)",
			"false = modern, more neutral rain tint",
			"Default value is true"
		);
		CONFIG.addEntry("waterHidesClouds", true,
			"Hide clouds and rain behind nearer water, ice and stained glass.",
			"Turn off if a shader pack draws water strangely with it on.",
			"Default value is true"
		);
	}

	/** Read the active values out of the config into the static fields. */
	private static void loadFields() {
		fluffyClouds = CONFIG.getBool("fluffyClouds");
		betaRainColor = CONFIG.getBool("b173RainColor");
		waterHidesClouds = CONFIG.getBool("waterHidesClouds");
		cloudViewDistance = clampViewDistance(CONFIG.getInt("cloudViewDistanceChunks"));
	}

	/** Restore every client config value to its default and persist. */
	public static void resetToDefaults() {
		CONFIG.clear();
		defineEntries();
		CONFIG.flush();
		loadFields();
	}

	/**
	 * @return {@code true} for the original "b1.7.3 rain color" (Beta 1.7.3 light-blue rain),
	 *         {@code false} for the modern neutral tint.
	 */
	public static boolean betaRainColor() {
		return betaRainColor;
	}

	/** Write translucent-block depth before clouds and rain so nearer water hides them. */
	public static boolean waterHidesClouds() {
		return waterHidesClouds;
	}

	/** Raw config flag (ignores the fancy-graphics gate that {@link #renderFluffy()} applies). */
	public static boolean isFluffyClouds() {
		return fluffyClouds;
	}

	/** Live-set fluffy clouds and persist. Caller should rebuild clouds (geometry changes). */
	public static void setFluffyClouds(boolean value) {
		fluffyClouds = value;
		CONFIG.set("fluffyClouds", value);
		CONFIG.flush();
	}

	/** Live-set the rain colour style and persist. Applied next frame (read per-frame). */
	public static void setBetaRainColor(boolean value) {
		betaRainColor = value;
		CONFIG.set("b173RainColor", value);
		CONFIG.flush();
	}

	/** Cloud render radius in 32-block tiles (4..16). */
	public static int getCloudViewDistance() {
		return cloudViewDistance;
	}

	/** Live-set the cloud render radius and persist. Caller applies it to the renderer. */
	public static void setCloudViewDistance(int chunks) {
		cloudViewDistance = clampViewDistance(chunks);
		CONFIG.set("cloudViewDistanceChunks", cloudViewDistance);
		CONFIG.flush();
	}

	private static int clampViewDistance(int chunks) {
		if (chunks < 4) return 4;
		if (chunks > CloudRenderer.HARD_MAX_RADIUS) return CloudRenderer.HARD_MAX_RADIUS;
		return chunks;
	}

	@SuppressWarnings("deprecation")
	public static boolean renderFluffy() {
		if (options == null) {
			options = Minecraft.getMinecraft().gameSettings;
		}
		return fluffyClouds && options.fancyGraphics;
	}

}
