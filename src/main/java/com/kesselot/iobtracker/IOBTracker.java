package com.kesselot.iobtracker;

import org.slf4j.Logger;

import com.kesselot.iobtracker.item.TrackerProperties;
import com.mojang.logging.LogUtils;

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
		TrackerProperties.clientSetup(event);
	}

	private void setup(final FMLCommonSetupEvent event) {
		NetworkHandler.register();
	}


}