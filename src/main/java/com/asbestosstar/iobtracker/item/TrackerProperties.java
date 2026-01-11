package com.asbestosstar.iobtracker.item;

import javax.annotation.Nullable;

import com.asbestosstar.iobtracker.ModItems;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class TrackerProperties implements ClampedItemPropertyFunction {

	
	public static void clientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemProperties.register(ModItems.TRACKER.get(), new ResourceLocation("iobtrack", "angle"),
					new TrackerProperties());
		});
	}
	
	
	private final Wobble wobble = new Wobble();
	private final Wobble wobbleRandom = new Wobble();

	@Override
	public float unclampedCall(ItemStack p_174672_, @Nullable ClientLevel p_174673_, @Nullable LivingEntity p_174674_,
			int p_174675_) {
		Entity entity = (Entity) (p_174674_ != null ? p_174674_ : p_174672_.getEntityRepresentation());
		if (entity == null) {
			return 0.0F;
		} else {
			if (p_174673_ == null && entity.level instanceof ClientLevel) {
				p_174673_ = (ClientLevel) entity.level;
			}

			BlockPos blockpos = position(p_174672_, p_174673_, p_174674_, p_174675_);
			long i = p_174673_.getGameTime();
			if (blockpos != null && !(entity.position().distanceToSqr((double) blockpos.getX() + 0.5D,
					entity.position().y(), (double) blockpos.getZ() + 0.5D) < (double) 1.0E-5F)) {
				boolean flag = p_174674_ instanceof Player && ((Player) p_174674_).isLocalPlayer();
				double d1 = 0.0D;
				if (flag) {
					d1 = (double) p_174674_.getYRot();
				} else if (entity instanceof ItemEntity) {
					d1 = (double) (180.0F - ((ItemEntity) entity).getSpin(0.5F) / ((float) Math.PI * 2F) * 360.0F);
				} else if (p_174674_ != null) {
					d1 = (double) p_174674_.yBodyRot;
				}

				d1 = Mth.positiveModulo(d1 / 360.0D, 1.0D);
				double d2 = getAngleTo(Vec3.atCenterOf(blockpos), entity) / (double) ((float) Math.PI * 2F);
				double d3;
				if (flag) {
					if (this.wobble.shouldUpdate(i)) {
						this.wobble.update(i, 0.5D - (d1 - 0.25D));
					}

					d3 = d2 + this.wobble.rotation;
				} else {
					d3 = 0.5D - (d1 - 0.25D - d2);
				}

				return Mth.positiveModulo((float) d3, 1.0F);
			} else {
				if (this.wobbleRandom.shouldUpdate(i)) {
					this.wobbleRandom.update(i, Math.random());
				}

				double d0 = this.wobbleRandom.rotation + (double) ((float) hash(p_174675_) / 2.14748365E9F);
				return Mth.positiveModulo((float) d0, 1.0F);
			}
		}
	}

	public static BlockPos position(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity,
			int seed) {
		if (level == null || entity == null) {
			return null;
		}
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("TrackedDragonClass", Tag.TAG_STRING)) {
			return null;
		}
		int x = tag.getInt("TrackedX");
		int y = tag.getInt("TrackedY");
		int z = tag.getInt("TrackedZ");
		return new BlockPos(x, y, z);
	}

	/**
	 * 
	 * @param p_117919_ the position the compass is pointing to
	 */
	public static double getAngleTo(Vec3 p_117919_, Entity p_117920_) {
		return Math.atan2(p_117919_.z() - p_117920_.getZ(), p_117919_.x() - p_117920_.getX());
	}

	public static int hash(int p_174670_) {
		return p_174670_ * 1327217883;
	}

	@OnlyIn(Dist.CLIENT)
	static class Wobble {
		double rotation;
		private double deltaRotation;
		private long lastUpdateTick;

		boolean shouldUpdate(long pGameTime) {
			return this.lastUpdateTick != pGameTime;
		}

		void update(long pGameTime, double pWobbleAmount) {
			this.lastUpdateTick = pGameTime;
			double d0 = pWobbleAmount - this.rotation;
			d0 = Mth.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
			this.deltaRotation += d0 * 0.03D;
			this.deltaRotation *= 0.4D;
			this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0D);
		}
	}

}
