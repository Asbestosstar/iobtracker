package com.asbestosstar.iobtracker;

import com.asbestosstar.iobtracker.item.DragonTrackerItem;
import com.GACMD.isleofberk.registery.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "iobtrack");

	// Use translation keys for localisation
	public static final RegistryObject<Item> NIGHT_FURY_TRACKER = ITEMS.register("night_fury_tracker",
			() -> new DragonTrackerItem(ModEntities.NIGHT_FURY, "dragon.night_fury",
					new Item.Properties().stacksTo(1)));

	public static final RegistryObject<Item> DEADLY_NADDER_TRACKER = ITEMS.register("deadly_nadder_tracker",
			() -> new DragonTrackerItem(ModEntities.DEADLY_NADDER, "dragon.deadly_nadder",
					new Item.Properties().stacksTo(1)));

}