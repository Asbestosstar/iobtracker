package com.kesselot.iobtracker.item;

import java.util.UUID;

import com.kesselot.iobtracker.DragonListPacket;
import com.kesselot.iobtracker.NetworkHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DragonTrackerItem extends Item {

	public DragonTrackerItem(Properties pProperties) {
		super(pProperties.stacksTo(1));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			NetworkHandler.INSTANCE.sendTo(new DragonListPacket((ServerPlayer) player),
					((ServerPlayer) player).connection.connection,
					net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level,
			java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
		tooltip.add(new TranslatableComponent("item.iobtrack.tracker.usage").withStyle(ChatFormatting.YELLOW));
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (!(entity instanceof Player player)) {
			return;
		}

		CompoundTag tag = stack.getOrCreateTag();

		if (!level.isClientSide()) {
			boolean updated = false;
			if (tag.contains("TrackedDragonUUID")) {
				UUID uuid = NbtUtils.loadUUID(tag.get("TrackedDragonUUID"));
				Entity tracked = ((ServerLevel) level).getEntity(uuid);

				if (tracked != null && tracked.isAlive()) {
					tag.putInt("TrackedX", Mth.floor(tracked.getX()));
					tag.putInt("TrackedY", Mth.floor(tracked.getY()));
					tag.putInt("TrackedZ", Mth.floor(tracked.getZ()));
					updated = true;
				} else {
					clearTracking(tag);
					updated = true;
				}
			}

			if (updated) {
				stack.setTag(tag.copy());
				player.getInventory().setItem(slot, stack);
			}
		}
	}

	private void clearTracking(CompoundTag tag) {
		tag.remove("TrackedDragonUUID");
		tag.remove("TrackedDragonClass");
		tag.remove("TrackedX");
		tag.remove("TrackedY");
		tag.remove("TrackedZ");
		tag.remove("iobtrack_frame");
	}
}