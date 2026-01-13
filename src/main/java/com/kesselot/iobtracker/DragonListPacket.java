package com.kesselot.iobtracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
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

		List<LivingEntity> dragons = IOBTracker.getDragonsNearPlayer(player);

		// If no dragons, nothing to do
		if (dragons.isEmpty()) {
			player.sendMessage(new TranslatableComponent("item.iobtrack.tracker.none"), Util.NIL_UUID);
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

}