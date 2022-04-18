package ftm._0xfmel.itdmtrct.handers;

import java.util.Optional;

import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import ftm._0xfmel.itdmtrct.network.ChannelListMessage;
import ftm._0xfmel.itdmtrct.network.SelectChannelMessage;
import ftm._0xfmel.itdmtrct.network.UpdateChannelMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModPacketHander {
        private static final String PROTOCOL_VERSION = "1";
        public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
                        new ResourceLocation(ModGlobals.MOD_ID, "main"), () -> PROTOCOL_VERSION,
                        PROTOCOL_VERSION::equals,
                        PROTOCOL_VERSION::equals);

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
}
