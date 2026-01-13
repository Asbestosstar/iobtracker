package com.kesselot.iobtracker;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class DragonTrackerMenu extends AbstractContainerMenu {
	public final Player player; // may be null on client
	public List<LivingEntity> nearbyDragons = new ArrayList<>();

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
		nearbyDragons = IOBTracker.getDragonsNearPlayer((ServerPlayer) player);
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