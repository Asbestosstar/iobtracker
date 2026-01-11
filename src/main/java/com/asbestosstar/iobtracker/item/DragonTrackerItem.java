package com.asbestosstar.iobtracker.item;

import com.asbestosstar.iobtracker.DragonListPacket;
import com.asbestosstar.iobtracker.NetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(new TranslatableComponent("item.iobtrack.tracker.usage").withStyle(ChatFormatting.YELLOW));
		int cd = stack.getOrCreateTag().getInt("Cooldown");
		if (cd > 0) {
			tooltip.add(new TranslatableComponent("item.iobtrack.tracker.cooldown.tooltip", cd / 20)
					.withStyle(ChatFormatting.RED));
		}
	}
}