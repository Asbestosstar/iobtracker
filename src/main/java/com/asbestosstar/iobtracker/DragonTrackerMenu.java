package com.asbestosstar.iobtracker;

import java.util.ArrayList;
import java.util.List;

import com.GACMD.isleofberk.registery.ModEntities;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class DragonTrackerMenu extends AbstractContainerMenu {
	public final Player player; // may be null on client
	public final List<LivingEntity> nearbyDragons = new ArrayList<>();

	// Called on SERVER when opening GUI
	public DragonTrackerMenu(int pContainerId, Inventory pPlayerInventory, Player player) {
		super(ModMenus.DRAGON_TRACKER_MENU.get(), pContainerId);
		this.player = player;
		if (player != null && !player.level.isClientSide()) {
			scanForDragons();
		}
	}

	// Called on CLIENT when receiving GUI open packet
	public DragonTrackerMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf extraData) {
		this(pContainerId, pPlayerInventory, (Player) null);
	}

	private void scanForDragons() {
		ServerLevel serverLevel = (ServerLevel) player.level;

		for (Entity entity : serverLevel.getAllEntities()) {
			if (entity instanceof LivingEntity living && isIOBDragon(living.getType()) && living.isAlive()) {
				nearbyDragons.add(living);
			}
		}
	}

	private boolean isIOBDragon(EntityType<?> type) {
		return type == ModEntities.NIGHT_FURY.get() || type == ModEntities.DEADLY_NADDER.get()
				|| type == ModEntities.LIGHT_FURY.get() || type == ModEntities.SKRILL.get()
				|| type == ModEntities.GRONCKLE.get() || type == ModEntities.MONSTROUS_NIGHTMARE.get()
				|| type == ModEntities.ZIPPLEBACK.get() || type == ModEntities.TERRIBLE_TERROR.get()
				|| type == ModEntities.SPEED_STINGER.get();
	}

	@Override
	public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return true;
	}
}