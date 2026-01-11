package com.asbestosstar.iobtracker;

import com.GACMD.isleofberk.registery.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

public class DragonListPacket {
	private final List<UUID> uuids;
	private final List<String> names;

	public DragonListPacket(net.minecraft.server.level.ServerPlayer player) {
		this.uuids = new ArrayList<>();
		this.names = new ArrayList<>();

		// Track which types we've already added
		Set<EntityType<?>> seenTypes = new HashSet<>();

		player.level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(2000)).stream()
				.filter(e -> isIOBDragon(e.getType()) && e.isAlive()).forEach(e -> {
					EntityType<?> type = e.getType();
					if (!seenTypes.contains(type)) {
						seenTypes.add(type);
						uuids.add(e.getUUID());
						names.add(e.getType().getDescription().getString());
					}
				});
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

	private static boolean isIOBDragon(EntityType<?> type) {
		return type == ModEntities.NIGHT_FURY.get() || type == ModEntities.DEADLY_NADDER.get()
				|| type == ModEntities.LIGHT_FURY.get() || type == ModEntities.SKRILL.get()
				|| type == ModEntities.GRONCKLE.get() || type == ModEntities.MONSTROUS_NIGHTMARE.get()
				|| type == ModEntities.ZIPPLEBACK.get() || type == ModEntities.TERRIBLE_TERROR.get()
				|| type == ModEntities.SPEED_STINGER.get();
	}
}