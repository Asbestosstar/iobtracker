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
		int buttonWidth = 120;
		int buttonHeight = 20;
		int spacingX = 10;
		int spacingY = 25;
		int cols = 3;
		int rows = 2;
		int totalSlots = cols * rows; // max 6

		// Center the grid horizontally
		int totalWidth = (buttonWidth + spacingX) * cols - spacingX;
		int startX = (this.width - totalWidth) / 2;
		int startY = 40; // top margin

		int added = 0;
		for (int i = 0; i < dragonNames.size() && added < totalSlots; i++) {
			final String name = dragonNames.get(i);
			final UUID uuid = dragonUUIDs.get(i);

			int col = added % cols;
			int row = added / cols;

			int x = startX + col * (buttonWidth + spacingX);
			int y = startY + row * (buttonHeight + spacingY);

			Button button = new Button(x, y, buttonWidth, buttonHeight, new TextComponent(name), btn -> {
				System.out.println("[IOBTRACKER] BUTTON PRESSED! Selecting: " + name + " (UUID: " + uuid + ")");
				onSelect(uuid);
			});

			this.addRenderableWidget(button);
			added++;
		}

		// If no dragons found
		if (dragonNames.isEmpty()) {
			Button closeBtn = new Button(this.width / 2 - 75, 40, 150, 20,
					new TranslatableComponent("gui.iobtrack.no_dragons"), btn -> onClose());
			this.addRenderableWidget(closeBtn);
		}

		// Done button at bottom
		Button doneBtn = new Button(this.width / 2 - 40, this.height - 30, 80, 20,
				new TranslatableComponent("gui.done"), btn -> {
					System.out.println("[IOBTRACKER] Done button pressed");
					onClose();
				});
		this.addRenderableWidget(doneBtn);
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