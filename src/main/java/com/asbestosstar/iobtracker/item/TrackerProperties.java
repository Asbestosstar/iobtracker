package com.asbestosstar.iobtracker.item;

import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TrackerProperties implements ClampedItemPropertyFunction {

	private final java.util.Map<Integer, Float> rotations = new java.util.HashMap<>();
	private final java.util.Map<Integer, Long> lastUpdate = new java.util.HashMap<>();

	@Override
	public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
		if (level == null || entity == null) {
			return 0.0F;
		}

		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("TrackedDragonClass", Tag.TAG_STRING)) {
			return 0.0F;
		}

		int x = tag.getInt("TrackedX");
		int z = tag.getInt("TrackedZ");

		// Direction to dragon
		double dx = x - entity.getX();
		double dz = z - entity.getZ();
		double targetAngle = Math.atan2(dz, dx);

		// Player's facing direction
		double playerYaw = Math.toRadians(-entity.getYRot());

		// Relative angle (dragon relative to player's forward)
		double relativeAngle = targetAngle - playerYaw;
		relativeAngle = relativeAngle % (2.0 * Math.PI);
		if (relativeAngle < 0)
			relativeAngle += 2.0 * Math.PI;

		float rawAngle = (float) (relativeAngle / (2.0 * Math.PI));

		float[] ANGLES = { 0.000000F, 0.015625F, 0.046875F, 0.078125F, 0.109375F, 0.140625F, 0.171875F, 0.203125F,
				0.234375F, 0.265625F, 0.296875F, 0.328125F, 0.359375F, 0.390625F, 0.421875F, 0.453125F, 0.484375F,
				0.515625F, 0.546875F, 0.578125F, 0.609375F, 0.640625F, 0.671875F, 0.703125F, 0.734375F, 0.765625F,
				0.796875F, 0.828125F, 0.859375F, 0.890625F, 0.921875F, 0.953125F, 0.984375F };

		// Find the CLOSEST angle (not just <=)
		float best = ANGLES[0];
		float minDiff = Math.abs(rawAngle - ANGLES[0]);
		for (int i = 1; i < ANGLES.length; i++) {
			float diff = Math.abs(rawAngle - ANGLES[i]);
			// Handle wrap-around at 0/1 boundary
			float wrapDiff = Math.abs((rawAngle + 1.0F) - ANGLES[i]);
			diff = Math.min(diff, wrapDiff);
			if (diff < minDiff) {
				minDiff = diff;
				best = ANGLES[i];
			}
		}

		return best;
	}

}
