package com.asbestosstar.iobtracker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import com.GACMD.isleofberk.registery.ModEntities;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

		for (Entity e : lvl.getAllEntities()) {
		    if (e instanceof LivingEntity living && isIOBDragon(living.getType()) && living.isAlive()) {
		        dragons.add(living);
		    }
		}
		
		
		// Sort by horizontal distance (XZ only)
		dragons.sort((a, b) -> {
			double distA = Math.sqrt(Math.pow(a.getX() - player.getX(), 2) + Math.pow(a.getZ() - player.getZ(), 2));
			double distB = Math.sqrt(Math.pow(b.getX() - player.getX(), 2) + Math.pow(b.getZ() - player.getZ(), 2));
			return Double.compare(distA, distB);
		});

		// Add to lists in sorted order
		for (LivingEntity e : dragons) {
			double dx = e.getX() - player.getX();
			double dz = e.getZ() - player.getZ();
			int dist = (int) Math.sqrt(dx * dx + dz * dz);

			String baseName = e.getType().getDescription().getString();
			String displayName = baseName + " (" + dist + " blocks)";

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

	private static boolean isIOBDragon(EntityType<?> type) {
		return type == ModEntities.NIGHT_FURY.get() || type == ModEntities.LIGHT_FURY.get()
				|| type == ModEntities.NIGHT_LIGHT.get() || type == ModEntities.TRIPLE_STRYKE.get()
				|| type == ModEntities.SKRILL.get() || type == ModEntities.DEADLY_NADDER.get()
				|| type == ModEntities.GRONCKLE.get() || type == ModEntities.MONSTROUS_NIGHTMARE.get()
				|| type == ModEntities.ZIPPLEBACK.get() || type == ModEntities.TERRIBLE_TERROR.get()
				|| type == ModEntities.SPEED_STINGER.get() || type == ModEntities.SPEED_STINGER_LEADER.get();
	}
}