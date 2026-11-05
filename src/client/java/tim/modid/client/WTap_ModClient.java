package tim.modid.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import java.util.Objects;
import tim.modid.WTap_Mod;

public class WTap_ModClient implements ClientModInitializer {
	private static final int MIN_FOOD_TO_SPRINT = 6; // vanilla needs food > 6
	private static final int COLOR_ON = 0xFF55FF55;
	private static final int COLOR_OFF = 0xFFFF5555;
	private static final int COLOR_INFO = 0xFFFFFFFF;

	private final WTapConfig config = WTapConfig.get();
	private KeyMapping toggleKey;
	private KeyMapping filterKey;
	private int wTapTicksLeft = 0;

	@Override
	public void onInitializeClient() {
		KeyMapping.Category category = KeyMapping.Category.register(Objects.requireNonNull(WTap_Mod.id("main")));
		toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.w-tap_mod.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, category));
		// Unbound by default; set it in Options > Controls.
		filterKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.w-tap_mod.cycle_filter", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, category));

		AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
			if (level.isClientSide() && config.enabled && matchesFilter(entity)) {
				wTapTicksLeft = config.wTapTicks;
			}
			return InteractionResult.PASS;
		});

		ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
		HudElementRegistry.addLast(Objects.requireNonNull(WTap_Mod.id("status")), this::renderHud);
	}

	private boolean matchesFilter(Entity entity) {
		if (!(entity instanceof LivingEntity living) || !living.isAlive()) return false;
		return switch (config.targetFilter) {
			case ALL -> true;
			case PLAYERS -> entity instanceof Player;
			case HOSTILE -> entity instanceof Enemy;
		};
	}

	private void onEndTick(Minecraft mc) {
		LocalPlayer player = mc.player;
		if (player == null) {
			wTapTicksLeft = 0;
			return;
		}

		while (toggleKey.consumeClick()) {
			config.enabled = !config.enabled;
			wTapTicksLeft = 0;
			config.save();
			message(player, "Auto Sprint & W-Tap " + (config.enabled ? "enabled" : "disabled"));
		}
		while (filterKey.consumeClick()) {
			config.targetFilter = config.targetFilter.next();
			config.save();
			message(player, "W-Tap targets: " + config.targetFilter.label);
		}

		if (!config.enabled) return;

		if (!canSprint(mc, player)) {
			wTapTicksLeft = 0;
			if (player.isSprinting()) player.setSprinting(false);
			return;
		}

		if (wTapTicksLeft > 0) {
			wTapTicksLeft--;
			player.setSprinting(false);
		} else if (!player.isSprinting()) {
			player.setSprinting(true);
		}
	}

	private void message(LocalPlayer player, String text) {
		if (config.chatFeedback) {
			player.displayClientMessage(Component.literal(Objects.requireNonNull(text)), true);
		}
	}

	private void renderHud(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();
		if (!config.showHud || mc.player == null || mc.options.hideGui) return;

		String state = config.enabled ? "Sprint: ON" : "Sprint: OFF";
		graphics.drawString(mc.font, state, 4, 4, config.enabled ? COLOR_ON : COLOR_OFF, true);
		if (config.enabled) {
			graphics.drawString(mc.font, "W-Tap: " + config.targetFilter.label,
					4, 4 + mc.font.lineHeight + 2, COLOR_INFO, true);
		}
	}

	private static boolean canSprint(Minecraft mc, LocalPlayer player) {
		boolean hasFood = player.getFoodData().getFoodLevel() > MIN_FOOD_TO_SPRINT
				|| player.getAbilities().mayfly;
		return mc.options.keyUp.isDown()
				&& hasFood
				&& !player.isShiftKeyDown()
				&& !player.isUsingItem()
				&& !player.isInWater()
				&& !player.isPassenger()
				&& !player.getAbilities().flying
				&& !player.hasEffect(MobEffects.BLINDNESS);
	}
}