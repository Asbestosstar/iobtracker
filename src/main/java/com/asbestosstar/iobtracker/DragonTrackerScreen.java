package com.asbestosstar.iobtracker;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class DragonTrackerScreen extends Screen {
	private final List<UUID> dragonUUIDs;
	private final List<String> dragonNames;

	public DragonTrackerScreen(List<UUID> uuids, List<String> names) {
		super(new TranslatableComponent("screen.iobtrack.tracker"));
		this.dragonUUIDs = uuids;
		this.dragonNames = names;
	}

	@Override
	protected void init() {
		int y = 30;
		for (int i = 0; i < dragonNames.size() && y < this.height - 40; i++) {
			final String name = dragonNames.get(i);
			final UUID uuid = dragonUUIDs.get(i);

			Button button = new Button(this.width / 2 - 75, y, 150, 20, new TextComponent(name), new Button.OnPress() {
				@Override
				public void onPress(Button btn) {
					System.out.println("[IOBTRACKER] BUTTON PRESSED! Selecting: " + name + " (UUID: " + uuid + ")");
					onSelect(uuid);
				}
			});

			this.renderables.add(button);
			List list = this.children();
			list.add(button);
			y += 25;
		}

		if (dragonNames.isEmpty()) {
			Button closeBtn = new Button(this.width / 2 - 75, 30, 150, 20,
					new TranslatableComponent("gui.iobtrack.no_dragons"), new Button.OnPress() {
						@Override
						public void onPress(Button btn) {
							onClose();
						}
					});
			this.renderables.add(closeBtn);
			List list = this.children();
			list.add(closeBtn);
		}

		Button doneBtn = new Button(this.width / 2 - 40, this.height - 30, 80, 20,
				new TranslatableComponent("gui.done"), new Button.OnPress() {
					@Override
					public void onPress(Button btn) {
						System.out.println("[IOBTRACKER] Done button pressed");
						onClose();
					}
				});
		this.renderables.add(doneBtn);
		List list = this.children();
		list.add(doneBtn);

	}

	private void onSelect(UUID uuid) {
		NetworkHandler.INSTANCE.sendToServer(new DragonSelectPacket(uuid));
		this.onClose();
	}

	@Override
	public void onClose() {
		super.onClose();
	}
}