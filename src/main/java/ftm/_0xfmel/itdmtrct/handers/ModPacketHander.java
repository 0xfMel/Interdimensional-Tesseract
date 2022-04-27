package ftm._0xfmel.itdmtrct.handers;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import ftm._0xfmel.itdmtrct.network.ChannelListMessage;
import ftm._0xfmel.itdmtrct.network.SelectChannelMessage;
import ftm._0xfmel.itdmtrct.network.UpdateChannelMessage;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModPacketHander {
	private static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(ModGlobals.MOD_ID, "main"),
			() -> ModPacketHander.PROTOCOL_VERSION,
			ModPacketHander.PROTOCOL_VERSION::equals,
			ModPacketHander.PROTOCOL_VERSION::equals);

	public static final ResourceLocation EVENT_RL = new ResourceLocation(ModGlobals.MOD_ID, "event");
	public static final EventNetworkChannel EVENT = NetworkRegistry.newEventChannel(
			ModPacketHander.EVENT_RL,
			() -> ModPacketHander.PROTOCOL_VERSION,
			ModPacketHander.PROTOCOL_VERSION::equals,
			ModPacketHander.PROTOCOL_VERSION::equals);

	private static int eventDiscrim = 0;
	public static final int REMOVE_UPGRADE_EVENT_ID = ModPacketHander.eventDiscrim++;

	public static void registerNetworkPackets() {
		int discrim = 0;

		// To server
		ModPacketHander.INSTANCE.registerMessage(
				discrim++,
				UpdateChannelMessage.class,
				UpdateChannelMessage::encode,
				UpdateChannelMessage::decode,
				UpdateChannelMessage::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
		ModPacketHander.INSTANCE.registerMessage(
				discrim++,
				SelectChannelMessage.class,
				SelectChannelMessage::encode,
				SelectChannelMessage::decode,
				SelectChannelMessage::handle,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));

		// To client
		ModPacketHander.INSTANCE.registerMessage(
				discrim++,
				ChannelListMessage.class,
				ChannelListMessage::encode,
				ChannelListMessage::decode,
				ChannelListMessage::handle,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	public static void sendEventToServer(int id, Optional<PacketBuffer> extra) {
		int capacity = extra.map((extraBuf) -> extraBuf.capacity()).orElse(0);
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer(1 + capacity));
		buf.writeByte(id & 0xff);
		extra.ifPresent(buf::writeBytes);

		Minecraft.getInstance().getConnection().getConnection()
				.send(NetworkDirection.PLAY_TO_SERVER.buildPacket(Pair.of(buf, Integer.MIN_VALUE),
						ModPacketHander.EVENT_RL).getThis());
	}

	public static void sendEventToServer(int id) {
		ModPacketHander.sendEventToServer(id, Optional.empty());
	}
}
