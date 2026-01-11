package com.asbestosstar.iobtracker;

import org.slf4j.Logger;

import com.asbestosstar.iobtracker.item.DragonTrackerItem;
import com.mojang.logging.LogUtils;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("iobtrack")
public class IOBTracker {
	private static final Logger LOGGER = LogUtils.getLogger();

	public IOBTracker() {
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);
		ModItems.ITEMS.register(bus);

		// Register tick handler
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		NetworkHandler.register();
	}

	// for cooldown
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END && !event.player.level.isClientSide()) {
			// Check main hand
			tickItemCooldown(event.player.getMainHandItem());
			// Check offhand
			tickItemCooldown(event.player.getOffhandItem());
		}
	}

	private void tickItemCooldown(net.minecraft.world.item.ItemStack stack) {
		if (stack.getItem() instanceof DragonTrackerItem) {
			int cd = stack.getOrCreateTag().getInt("Cooldown");
			if (cd > 0) {
				stack.getOrCreateTag().putInt("Cooldown", cd - 1);
			}
		}
	}
}