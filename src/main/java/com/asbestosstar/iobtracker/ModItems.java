package com.asbestosstar.iobtracker;

import com.asbestosstar.iobtracker.item.DragonTrackerItem;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "iobtrack");

	public static final RegistryObject<Item> TRACKER = ITEMS.register("tracker",
			() -> new DragonTrackerItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));

}