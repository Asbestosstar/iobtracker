package com.asbestosstar.iobtracker;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("iobtrack")
public class IOBTracker {
	private static final Logger LOGGER = LogUtils.getLogger();

	public IOBTracker() {
		// Register item registry
		ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

		LOGGER.info("IOB Tracker mod initialized (items only).");
	}
}