package com.asbestosstar.iobtracker;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
			net.minecraft.server.level.ServerPlayer player = ctx.get().getSender();
			if (player == null)
				return;

			net.minecraft.world.entity.LivingEntity target = null;
			for (net.minecraft.world.entity.Entity e : player.level.getEntitiesOfClass(
					net.minecraft.world.entity.Entity.class, player.getBoundingBox().inflate(2000),
					entity -> entity.getUUID().equals(dragonUUID)
							&& entity instanceof net.minecraft.world.entity.LivingEntity)) {
				target = (net.minecraft.world.entity.LivingEntity) e;
				break;
			}

			if (target != null) {
				// Consume gronckle_iron
				boolean consumed = false;
				net.minecraft.world.item.Item gronckleIron = com.GACMD.isleofberk.registery.ModItems.GRONCKLE_IRON
						.get();
				net.minecraft.world.entity.player.Inventory inv = player.getInventory();

				for (int i = 0; i < inv.items.size(); i++) {
					net.minecraft.world.item.ItemStack stack = inv.items.get(i);
					if (!stack.isEmpty() && stack.getItem() == gronckleIron) {
						if (stack.getCount() > 1)
							stack.shrink(1);
						else
							inv.setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
						consumed = true;
						break;
					}
				}

				if (!consumed) {
					net.minecraft.world.item.ItemStack offhand = inv.offhand.get(0);
					if (!offhand.isEmpty() && offhand.getItem() == gronckleIron) {
						if (offhand.getCount() > 1)
							offhand.shrink(1);
						else
							inv.offhand.set(0, net.minecraft.world.item.ItemStack.EMPTY);
						consumed = true;
					}
				}

				if (consumed) {
					ItemStack tracker = player.getMainHandItem();
					if (tracker.getItem() instanceof com.asbestosstar.iobtracker.item.DragonTrackerItem) {
						CompoundTag tag = tracker.getOrCreateTag();

						tag.put("TrackedDragonUUID", net.minecraft.nbt.NbtUtils.createUUID(target.getUUID()));

						tag.putString("TrackedDragonClass", target.getClass().getName());

						tag.putInt("Cooldown", 1200);

						// Sync to client
						player.containerMenu.slots.get(36 + player.getInventory().selected).set(tracker);
					}

					if (player.level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
						serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.ASH, target.getX(),
								target.getY() + 1, target.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
					}

					double dx = target.getX() - player.getX();
					double dz = target.getZ() - player.getZ();
					int distance = (int) Math.sqrt(dx * dx + dz * dz);

					player.sendMessage(new net.minecraft.network.chat.TranslatableComponent(
							"item.iobtrack.tracker.found", target.getType().getDescription(), distance,
							target.blockPosition().getX(), target.blockPosition().getY(), target.blockPosition().getZ())
							.withStyle(net.minecraft.ChatFormatting.GREEN), net.minecraft.Util.NIL_UUID);
				} else {
					player.sendMessage(new net.minecraft.network.chat.TranslatableComponent(
							"item.iobtrack.tracker.no_gronckle_iron").withStyle(net.minecraft.ChatFormatting.RED),
							net.minecraft.Util.NIL_UUID);
				}
			}
		});
		return true;
	}

}