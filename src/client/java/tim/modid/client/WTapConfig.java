package tim.modid.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import tim.modid.WTap_Mod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Simple JSON config stored in the game's config folder (w-tap_mod.json). */
public class WTapConfig {
	public enum TargetFilter {
		ALL("Everything"),
		PLAYERS("Players"),
		HOSTILE("Hostile mobs");

		public final String label;

		TargetFilter(String label) {
			this.label = label;
		}

		public TargetFilter next() {
			TargetFilter[] v = values();
			return v[(ordinal() + 1) % v.length];
		}
	}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("w-tap_mod.json");

	/** Master switch for auto sprint + W-tap (the R key). */
	public boolean enabled = true;
	/** Which targets trigger a W-tap. */
	public TargetFilter targetFilter = TargetFilter.ALL;
	/** Show the on-screen status label. */
	public boolean showHud = true;
	/** Show the action-bar message when toggling. */
	public boolean chatFeedback = true;
	/** Ticks sprint stays off after a hit. */
	public int wTapTicks = 2;

	private static WTapConfig instance;

	public static WTapConfig get() {
		if (instance == null) instance = load();
		return instance;
	}

	private static WTapConfig load() {
		if (Files.exists(PATH)) {
			try {
				WTapConfig cfg = GSON.fromJson(Files.readString(PATH), WTapConfig.class);
				if (cfg != null) {
					if (cfg.targetFilter == null) cfg.targetFilter = TargetFilter.ALL;
					cfg.wTapTicks = Math.max(1, Math.min(10, cfg.wTapTicks));
					return cfg;
				}
			} catch (Exception e) {
				WTap_Mod.LOGGER.warn("Could not read config, using defaults", e);
			}
		}
		WTapConfig cfg = new WTapConfig();
		cfg.save();
		return cfg;
	}

	public void save() {
		try {
			Files.createDirectories(PATH.getParent());
			Files.writeString(PATH, GSON.toJson(this));
		} catch (IOException e) {
			WTap_Mod.LOGGER.warn("Could not save config", e);
		}
	}
}