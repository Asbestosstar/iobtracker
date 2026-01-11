package com.asbestosstar.iobtracker.item;

import java.util.UUID;

import com.asbestosstar.iobtracker.DragonListPacket;
import com.asbestosstar.iobtracker.NetworkHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
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
			int cooldown = stack.getOrCreateTag().getInt("Cooldown");
			if (cooldown > 0) {
				player.sendMessage(new TranslatableComponent("item.iobtrack.tracker.cooldown", cooldown / 20)
						.withStyle(ChatFormatting.RED), Util.NIL_UUID);
				return InteractionResultHolder.fail(stack);
			}

			NetworkHandler.INSTANCE.sendTo(new DragonListPacket((ServerPlayer) player),
					((ServerPlayer) player).connection.connection,
					net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
		}
		return InteractionResultHolder.success(stack);
	}

	public static void applyCooldown(ItemStack tracker) {
		tracker.getOrCreateTag().putInt("Cooldown", 1200);
	}

	@Override
	public void appendHoverText(net.minecraft.world.item.ItemStack stack,
			@javax.annotation.Nullable net.minecraft.world.level.Level level,
			java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
		tooltip.add(new net.minecraft.network.chat.TranslatableComponent("item.iobtrack.tracker.usage")
				.withStyle(net.minecraft.ChatFormatting.YELLOW));

		int cd = stack.getOrCreateTag().getInt("Cooldown");
		if (cd > 0) {
			tooltip.add(new net.minecraft.network.chat.TranslatableComponent("item.iobtrack.tracker.cooldown.tooltip",
					cd / 20).withStyle(net.minecraft.ChatFormatting.RED));
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (!(entity instanceof Player player))
			return;

		CompoundTag tag = stack.getOrCreateTag();

		if (!level.isClientSide()) {
			int cd = tag.getInt("Cooldown");
			if (cd > 0) {
				tag.putInt("Cooldown", cd - 1);
			}

			boolean updated = false;
			if (tag.contains("TrackedDragonUUID")) {
				UUID uuid = net.minecraft.nbt.NbtUtils.loadUUID(tag.get("TrackedDragonUUID"));
				Entity tracked = ((net.minecraft.server.level.ServerLevel) level).getEntity(uuid);

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

			if (updated || tag.getInt("Cooldown") != cd) {
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