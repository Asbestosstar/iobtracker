package com.kesselot.iobtracker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.kesselot.iobtracker.item.TrackerProperties;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

	public static List<LivingEntity> getDragonsNearPlayer(ServerPlayer player) {
		return getDragonsNearPosition(player.getLevel(), player.blockPosition());
	}

	public static List<LivingEntity> getDragonsNearPosition(ServerLevel lvl, BlockPos pos) {
		List<LivingEntity> ret = new ArrayList<>();
		double maxDistanceSq = 160.0 * 160.0; // Compare squared distance to avoid sqrt

		for (Entity e : lvl.getAllEntities()) {
			if (e instanceof LivingEntity living && isIOBDragon(living) && living.isAlive()) {
				// Check horizontal distance only (X and Z), ignoring Y
				double dx = living.getX() - pos.getX();
				double dz = living.getZ() - pos.getZ();
				if (dx * dx + dz * dz <= maxDistanceSq) {
					ret.add(living);
				}
			}
		}
		return ret;
	}

	public static boolean isIOBDragon(Entity entity) {
		return entity instanceof com.GACMD.isleofberk.entity.dragons.nightfury.NightFury
				|| entity instanceof com.GACMD.isleofberk.entity.dragons.lightfury.LightFury
				|| entity instanceof com.GACMD.isleofberk.entity.dragons.nightlight.NightLight
				|| entity instanceof com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStryke
				|| entity instanceof com.GACMD.isleofberk.entity.dragons.skrill.Skrill
				|| entity instanceof com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadder
				|| entity instanceof com.GACMD.isleofberk.entity.dragons.gronckle.Gronckle
				|| entity instanceof com.GACMD.isleofberk.entity.dragons.montrous_nightmare.MonstrousNightmare
				|| entity instanceof com.GACMD.isleofberk.entity.dragons.zippleback.ZippleBack
				|| entity instanceof com.GACMD.isleofberk.entity.dragons.terrible_terror.TerribleTerror
				|| entity instanceof com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStinger
				|| entity instanceof com.GACMD.isleofberk.entity.dragons.speedstingerleader.SpeedStingerLeader;
	}

}