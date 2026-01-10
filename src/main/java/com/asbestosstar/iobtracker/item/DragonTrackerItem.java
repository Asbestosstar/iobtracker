package com.asbestosstar.iobtracker.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

public class DragonTrackerItem extends Item {
	private final RegistryObject<? extends EntityType<?>> targetDragonType;
	private final String dragonTranslationKey;

	public DragonTrackerItem(RegistryObject<? extends EntityType<?>> dragonType, String dragonTranslationKey,
			Properties properties) {
		super(properties);
		this.targetDragonType = dragonType;
		this.dragonTranslationKey = dragonTranslationKey;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide()) {
			EntityType<?> targetType = targetDragonType.get();
//            double searchRadius = 64.0;
			double searchRadius = 2000.0;

			Vec3 playerPos = player.position();
			List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class,
					player.getBoundingBox().inflate(searchRadius),
					entity -> entity.getType() == targetType && entity.isAlive());

			if (candidates.isEmpty()) {
				Component msg = new TranslatableComponent("item.iobtrack.tracker.not_found",
						new TranslatableComponent(dragonTranslationKey)).withStyle(ChatFormatting.RED);
				player.sendMessage(msg, Util.NIL_UUID);
			} else {
				LivingEntity closest = null;
				double closestDist = Double.MAX_VALUE;
				for (LivingEntity e : candidates) {
					double dist = playerPos.distanceToSqr(e.position());
					if (dist < closestDist) {
						closestDist = dist;
						closest = e;
					}
				}

				if (closest != null) {
					BlockPos pos = closest.blockPosition();
					int distance = (int) Math.sqrt(closestDist);
					Component msg = new TranslatableComponent("item.iobtrack.tracker.found",
							new TranslatableComponent(dragonTranslationKey), distance, pos.getX(), pos.getY(),
							pos.getZ()).withStyle(ChatFormatting.GREEN);
					player.sendMessage(msg, Util.NIL_UUID);

					if (level instanceof ServerLevel serverLevel) {
						serverLevel.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.0,
								pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0.1);
					}
				}
			}
		}

		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		Component usage = new TranslatableComponent("item.iobtrack.tracker.usage",
				new TranslatableComponent(dragonTranslationKey)).withStyle(ChatFormatting.YELLOW);
		tooltip.add(usage);

		Component radius = new TranslatableComponent("item.iobtrack.tracker.radius").withStyle(ChatFormatting.GRAY);
		tooltip.add(radius);
	}
}