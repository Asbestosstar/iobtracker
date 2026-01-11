package com.kesselot.iobtracker;

import java.util.UUID;

import com.kesselot.iobtracker.item.DragonTrackerItem;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DragonSelectPacket {
	private final UUID dragonUUID;

	public DragonSelectPacket(UUID uuid) {
		this.dragonUUID = uuid;
	}

	public DragonSelectPacket(FriendlyByteBuf buf) {
		this.dragonUUID = buf.readUUID();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeUUID(dragonUUID);
	}

	public boolean handle(java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (player == null) {
				return;
			}

			LivingEntity target = null;
			ServerLevel serverLevel = (ServerLevel) player.level;

			for (Entity e : serverLevel.getAllEntities()) {
				if (e.getUUID().equals(dragonUUID) && e instanceof LivingEntity) {
					target = (LivingEntity) e;
					break;
				}
			}

			if (target != null) {
				// Consume gronckle_iron
				boolean consumed = false;
				Item gronckleIron = com.GACMD.isleofberk.registery.ModItems.GRONCKLE_IRON
						.get();
				Inventory inv = player.getInventory();

				for (int i = 0; i < inv.items.size(); i++) {
					ItemStack stack = inv.items.get(i);
					if (!stack.isEmpty() && stack.getItem() == gronckleIron) {
						if (stack.getCount() > 1) {
							stack.shrink(1);
						} else {
							inv.setItem(i, ItemStack.EMPTY);
						}
						consumed = true;
						break;
					}
				}

				if (!consumed) {
					ItemStack offhand = inv.offhand.get(0);
					if (!offhand.isEmpty() && offhand.getItem() == gronckleIron) {
						if (offhand.getCount() > 1) {
							offhand.shrink(1);
						} else {
							inv.offhand.set(0, ItemStack.EMPTY);
						}
						consumed = true;
					}
				}

				if (consumed) {
					ItemStack tracker = player.getMainHandItem();
					if (tracker.getItem() instanceof DragonTrackerItem) {
						CompoundTag tag = tracker.getOrCreateTag();

						tag.put("TrackedDragonUUID", NbtUtils.createUUID(target.getUUID()));

						tag.putString("TrackedDragonClass", target.getClass().getName());

						tag.putInt("Cooldown", 1200);

						// Sync to client
						player.containerMenu.slots.get(36 + player.getInventory().selected).set(tracker);
					}

					serverLevel.sendParticles(ParticleTypes.ASH, target.getX(),
							target.getY() + 1, target.getZ(), 20, 0.5, 0.5, 0.5, 0.1);

					double dx = target.getX() - player.getX();
					double dz = target.getZ() - player.getZ();
					int distance = (int) Math.sqrt(dx * dx + dz * dz);

					player.sendMessage(new TranslatableComponent(
							"item.iobtrack.tracker.found", target.getType().getDescription(), distance,
							target.blockPosition().getX(), target.blockPosition().getY(), target.blockPosition().getZ())
							.withStyle(ChatFormatting.GREEN), net.minecraft.Util.NIL_UUID);
				} else {
					player.sendMessage(new TranslatableComponent(
							"item.iobtrack.tracker.no_gronckle_iron").withStyle(ChatFormatting.RED),
							net.minecraft.Util.NIL_UUID);
				}
			}
		});
		return true;
	}

}