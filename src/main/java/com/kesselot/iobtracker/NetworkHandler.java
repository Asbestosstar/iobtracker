// src/main/java/com/asbestosstar/iobtracker/NetworkHandler.java
package com.kesselot.iobtracker;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation("iobtrack", "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);

	public static void register() {
		int id = 0;
		INSTANCE.messageBuilder(DragonListPacket.class, id++).encoder(DragonListPacket::toBytes)
				.decoder(DragonListPacket::new).consumer(DragonListPacket::handle).add();
		INSTANCE.messageBuilder(DragonSelectPacket.class, id++).encoder(DragonSelectPacket::toBytes)
				.decoder(DragonSelectPacket::new).consumer(DragonSelectPacket::handle).add();
	}
}