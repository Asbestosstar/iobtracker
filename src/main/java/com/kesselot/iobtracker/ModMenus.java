package com.kesselot.iobtracker;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS,
			"iobtrack");

	public static final RegistryObject<MenuType<DragonTrackerMenu>> DRAGON_TRACKER_MENU = MENUS
			.register("dragon_tracker", () -> IForgeMenuType.create(DragonTrackerMenu::new));
}