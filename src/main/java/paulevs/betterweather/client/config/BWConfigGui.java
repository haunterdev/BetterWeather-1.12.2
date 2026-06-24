package paulevs.betterweather.client.config;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulevs.betterweather.client.rendering.BetterWeatherRenderer;
import paulevs.betterweather.client.rendering.CloudRenderer;
import paulevs.betterweather.config.ClientConfig;
import paulevs.betterweather.config.CommonConfig;

/**
 * In-game BetterWeather config screen (Mods list -> Config). Every control applies LIVE:
 * cloud height, rain colour and cloud speed are read per-frame/per-call by the renderers and the
 * weather sim, so changes show up the next frame; fluffy-clouds toggles geometry, so it forces a
 * cloud rebuild. Every change is also persisted to the .cfg immediately.
 */
@SideOnly(Side.CLIENT)
public class BWConfigGui extends GuiScreen {

	private static final int ID_PRESET = 1;
	private static final int ID_FLUFFY = 2;
	private static final int ID_RAIN = 3;
	private static final int ID_VANILLA_RAIN = 4;
	private static final int ID_FREQUENT_RAIN = 5;
	private static final int ID_HEIGHT = 10;
	private static final int ID_SPEED = 11;
	private static final int ID_VIEWDIST = 12;
	private static final int ID_DONE = 200;
	private static final int ID_RESET = 201;

	private final GuiScreen parent;

	// Y of the heavy-lag warning line, drawn when the view-distance value is high.
	private int warnY;

	private GuiButton presetButton;
	private GuiButton fluffyButton;
	private GuiButton rainButton;
	private GuiButton vanillaRainButton;
	private GuiButton frequentRainButton;
	private GuiSlider heightSlider;
	private GuiSlider speedSlider;
	private GuiSlider viewDistSlider;

	public BWConfigGui(GuiScreen parent) {
		this.parent = parent;
	}

	@Override
	public boolean doesGuiPauseGame() {
		// Keep the world rendering/ticking behind the screen so cloud-height and other changes
		// are visible live as you adjust the controls.
		return false;
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		int x = this.width / 2 - 100;
		int y = 40;

		presetButton = new GuiButton(ID_PRESET, x, y, 200, 20, presetLabel());
		this.buttonList.add(presetButton);
		y += 24;

		heightSlider = new GuiSlider(ID_HEIGHT, x, y, 200, 20, "Custom Height: ", "",
			0, 255, CommonConfig.getCloudHeightCustom(), false, true, this::onSlider);
		this.buttonList.add(heightSlider);
		y += 24;

		fluffyButton = new GuiButton(ID_FLUFFY, x, y, 200, 20, fluffyLabel());
		this.buttonList.add(fluffyButton);
		y += 24;

		rainButton = new GuiButton(ID_RAIN, x, y, 200, 20, rainLabel());
		this.buttonList.add(rainButton);
		y += 24;

		vanillaRainButton = new GuiButton(ID_VANILLA_RAIN, x, y, 200, 20, vanillaRainLabel());
		this.buttonList.add(vanillaRainButton);
		y += 24;

		frequentRainButton = new GuiButton(ID_FREQUENT_RAIN, x, y, 200, 20, frequentRainLabel());
		this.buttonList.add(frequentRainButton);
		y += 24;

		speedSlider = new GuiSlider(ID_SPEED, x, y, 200, 20, "Clouds Speed: ", "",
			0.0, 0.02, CommonConfig.getCloudsSpeed(), true, true, this::onSlider);
		speedSlider.precision = 3;
		speedSlider.updateSlider();
		this.buttonList.add(speedSlider);
		y += 24;

		viewDistSlider = new GuiSlider(ID_VIEWDIST, x, y, 200, 20, "Cloud View Distance: ", " tiles",
			4, CloudRenderer.HARD_MAX_RADIUS, ClientConfig.getCloudViewDistance(), false, true, this::onSlider);
		this.buttonList.add(viewDistSlider);
		y += 24;

		// Reserve a line for the heavy-lag warning (drawn in drawScreen when the value is high).
		warnY = y;
		y += 14;

		this.buttonList.add(new GuiButton(ID_RESET, x, y, 98, 20, "Reset to Default"));
		this.buttonList.add(new GuiButton(ID_DONE, x + 102, y, 98, 20, "Done"));
	}

	private void onSlider(GuiSlider slider) {
		if (slider == heightSlider) {
			CommonConfig.setCloudHeightCustom(slider.getValueInt());
			// Dragging the height implies you want the custom value to take effect.
			if (!"custom".equals(CommonConfig.getCloudHeightPreset())) {
				CommonConfig.setCloudHeightPreset("custom");
				if (presetButton != null) presetButton.displayString = presetLabel();
			}
		}
		else if (slider == speedSlider) {
			CommonConfig.setCloudsSpeed(slider.getValue());
		}
		// viewDistSlider is intentionally NOT applied here: a high value regrows the cloud-chunk
		// VBO pool, which is far too heavy to do every frame while dragging. It is applied once on
		// release (see mouseReleased).
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		if (viewDistSlider != null) {
			int n = viewDistSlider.getValueInt();
			if (n != ClientConfig.getCloudViewDistance()) {
				ClientConfig.setCloudViewDistance(n);
				BetterWeatherRenderer.setCloudViewDistance(n);
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
			case ID_PRESET:
				CommonConfig.setCloudHeightPreset(nextPreset(CommonConfig.getCloudHeightPreset()));
				button.displayString = presetLabel();
				break;
			case ID_FLUFFY:
				ClientConfig.setFluffyClouds(!ClientConfig.isFluffyClouds());
				BetterWeatherRenderer.updateClouds();
				button.displayString = fluffyLabel();
				break;
			case ID_RAIN:
				ClientConfig.setBetaRainColor(!ClientConfig.betaRainColor());
				button.displayString = rainLabel();
				break;
			case ID_VANILLA_RAIN:
				CommonConfig.setVanillaRainControl(!CommonConfig.isVanillaRainControl());
				// Rain source changed -> cloud volume must be recomputed.
				BetterWeatherRenderer.updateClouds();
				button.displayString = vanillaRainLabel();
				break;
			case ID_FREQUENT_RAIN:
				CommonConfig.setFrequentRain(!CommonConfig.isFrequentRain());
				// Front strength changed -> cloud volume must be recomputed.
				BetterWeatherRenderer.updateClouds();
				button.displayString = frequentRainLabel();
				break;
			case ID_RESET:
				CommonConfig.resetToDefaults();
				ClientConfig.resetToDefaults();
				// Re-apply the defaults that the renderer caches, then rebuild the controls so
				// every label/slider reflects the restored values.
				BetterWeatherRenderer.setCloudViewDistance(ClientConfig.getCloudViewDistance());
				BetterWeatherRenderer.updateClouds();
				this.initGui();
				break;
			case ID_DONE:
				this.mc.displayGuiScreen(parent);
				break;
			default:
				break;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, "Better Weather", this.width / 2, 12, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer,
			"Active cloud Y: " + CommonConfig.getCloudHeight(), this.width / 2, 26, 0xA0A0A0);
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (viewDistSlider != null && viewDistSlider.getValueInt() > CloudRenderer.LAG_WARN_RADIUS) {
			this.drawCenteredString(this.fontRenderer,
				"High view distance - expect heavy lag", this.width / 2, warnY, 0xFFFF5555);
		}
	}

	private static String nextPreset(String current) {
		if ("b173".equals(current)) return "vanilla";
		if ("vanilla".equals(current)) return "custom";
		return "b173";
	}

	private static String presetLabel() {
		String preset = CommonConfig.getCloudHeightPreset();
		String pretty;
		switch (preset) {
			case "b173":
				pretty = "Beta 1.7.3 (" + CommonConfig.CLOUD_HEIGHT_B173 + ")";
				break;
			case "custom":
				pretty = "Custom (" + CommonConfig.getCloudHeightCustom() + ")";
				break;
			default:
				pretty = "Vanilla (" + CommonConfig.CLOUD_HEIGHT_VANILLA + ")";
				break;
		}
		return "Cloud Height: " + pretty;
	}

	private static String fluffyLabel() {
		return "Fluffy Clouds: " + (ClientConfig.isFluffyClouds() ? "ON" : "OFF");
	}

	private static String rainLabel() {
		return "Rain Colour: " + (ClientConfig.betaRainColor() ? "Beta 1.7.3" : "Modern");
	}

	private static String vanillaRainLabel() {
		return "Vanilla Rain Control: " + (CommonConfig.isVanillaRainControl() ? "ON" : "OFF");
	}

	private static String frequentRainLabel() {
		return "Frequent Rain: " + (CommonConfig.isFrequentRain() ? "ON" : "OFF");
	}
}
