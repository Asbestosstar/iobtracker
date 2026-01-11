package com.kesselot.iobtracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

public class DragonListPacket {
	private final List<UUID> uuids;
	private final List<String> names;

	public DragonListPacket(ServerPlayer player) {
		this.uuids = new ArrayList<>();
		this.names = new ArrayList<>();

		ServerLevel lvl = (ServerLevel) player.level;
		List<LivingEntity> dragons = new ArrayList<>();

		// Collect ALL loaded IOB dragons in the world
		for (Entity e : lvl.getAllEntities()) {
			if (e instanceof LivingEntity living && isIOBDragon(living) && living.isAlive()) {
				dragons.add(living);
			}
		}

		// If no dragons, nothing to do
		if (dragons.isEmpty()) {
			return;
		}

		// Shuffle using world time as seed for consistency per tick
		Random random = new Random(player.getLevel().getGameTime());
		Collections.shuffle(dragons, random);

		// Take up to 6
		int count = Math.min(dragons.size(), 6);
		for (int i = 0; i < count; i++) {
			LivingEntity e = dragons.get(i);

			String baseName = e.getType().getDescription().getString();
			String displayName = baseName;

			uuids.add(e.getUUID());
			names.add(displayName);
		}
	}

	public DragonListPacket(FriendlyByteBuf buf) {
		int count = buf.readInt();
		this.uuids = new ArrayList<>();
		this.names = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			uuids.add(buf.readUUID());
			names.add(buf.readUtf(32767));
		}
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(uuids.size());
		for (int i = 0; i < uuids.size(); i++) {
			buf.writeUUID(uuids.get(i));
			buf.writeUtf(names.get(i), 32767);
		}
	}

	public boolean handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getDirection().getReceptionSide().isClient()) {
				openScreen();
			}
		});
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	private void openScreen() {
		Minecraft.getInstance().setScreen(new DragonTrackerScreen(uuids, names));
	}

	private static boolean isIOBDragon(Entity entity) {
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