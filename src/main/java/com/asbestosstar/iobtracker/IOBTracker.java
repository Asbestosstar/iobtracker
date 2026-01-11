package com.asbestosstar.iobtracker;

import org.slf4j.Logger;

import com.asbestosstar.iobtracker.item.TrackerProperties;
import com.mojang.logging.LogUtils;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("iobtrack")
public class IOBTracker {
	private static final Logger LOGGER = LogUtils.getLogger();

	public IOBTracker() {
		var modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setup);
		modBus.addListener(this::clientSetup);
		ModItems.ITEMS.register(modBus);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemProperties.register(ModItems.TRACKER.get(), new ResourceLocation("iobtrack", "angle"),
					new TrackerProperties());
		});
	}

	private void setup(final FMLCommonSetupEvent event) {
		NetworkHandler.register();
	}

	@net.minecraftforge.eventbus.api.SubscribeEvent
	public void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {

	}

}