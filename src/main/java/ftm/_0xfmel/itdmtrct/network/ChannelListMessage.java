package ftm._0xfmel.itdmtrct.network;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels.TesseractChannel;
import ftm._0xfmel.itdmtrct.capabilities.TesseractChannelsCapability;
import ftm._0xfmel.itdmtrct.handers.ModPacketHander;
import ftm._0xfmel.itdmtrct.utils.ChannelUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class ChannelListMessage {
    private static final int IS_OWN_FLAG;
    private static final int IS_PRIVATE_FLAG;
    private static final int IS_SELECTED_FLAG;
    private static final int IN_RANGE_FLAG;
    private static final int IN_VALID_DIMENSION_FLAG;

    static {
        int flagIndex = 0;
        IS_OWN_FLAG = 1 << flagIndex++;
        IS_PRIVATE_FLAG = 1 << flagIndex++;
        IS_SELECTED_FLAG = 1 << flagIndex++;
        IN_RANGE_FLAG = 1 << flagIndex++;
        IN_VALID_DIMENSION_FLAG = 1 << flagIndex++;
    }

    private final Stream<TesseractChannel> channels;
    private final UUID playerFor;

    private final TileEntity te;

    public static void write(ServerPlayerEntity player, TileEntity te, PacketBuffer buf) {
        player.getServer().getLevel(World.OVERWORLD)
                .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                .ifPresent((tesseractChannels) -> {
                    new ChannelListMessage(
                            tesseractChannels.getChannels(),
                            player.getUUID(),
                            te).encode(buf);
                });
    }

    public static void send(ServerPlayerEntity player, TileEntity te) {
        player.getServer().getLevel(World.OVERWORLD)
                .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                .ifPresent((tesseractChannels) -> {
                    ModPacketHander.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            new ChannelListMessage(
                                    tesseractChannels.getChannels(),
                                    player.getUUID(),
                                    te));
                });
    }

    private ChannelListMessage(Stream<TesseractChannel> channels, UUID playerFor, TileEntity te) {
        this.channels = channels;
        this.playerFor = playerFor;
        this.te = te;
    }

    @OnlyIn(Dist.CLIENT)
    private ChannelListMessage(Stream<TesseractChannel> channels, UUID playerFor) {
        this(channels, playerFor, null);
    }

    @SuppressWarnings("resource")
    public static ChannelListMessage decode(PacketBuffer buf) {
        int len = buf.readVarInt();
        TesseractChannel[] channels = new TesseractChannel[len];
        UUID me = Minecraft.getInstance().player.getUUID();
        for (int i = 0; i < len; i++) {
            int id = buf.readVarInt();
            String name = buf.readUtf();
            int flag = buf.readByte() & 0xff;
            UUID playerUuid = null;
            boolean isPrivate = false;
            if ((flag & IS_OWN_FLAG) == IS_OWN_FLAG) {
                playerUuid = me;
                isPrivate = (flag & IS_PRIVATE_FLAG) == IS_PRIVATE_FLAG;
            }
            boolean isSelected = (flag & IS_SELECTED_FLAG) == IS_SELECTED_FLAG;
            boolean inRange = (flag & IN_RANGE_FLAG) == IN_RANGE_FLAG;
            boolean inValidDimension = (flag & IN_VALID_DIMENSION_FLAG) == IN_VALID_DIMENSION_FLAG;
            channels[i] = new TesseractChannel(id, name, playerUuid, isPrivate, isSelected, inRange, inValidDimension);
        }
        return new ChannelListMessage(Stream.of(channels), me);
    }

    public void encode(PacketBuffer buf) {
        TesseractChannel[] filteredChannels = channels
                .filter((channel) -> !channel.isPrivate || channel.playerUuid.equals(this.playerFor))
                .toArray(TesseractChannel[]::new);
        buf.writeVarInt(filteredChannels.length);
        Stream.of(filteredChannels).forEachOrdered((channel) -> {
            buf.writeVarInt(channel.id);
            buf.writeUtf(channel.name);
            buf.writeByte(
                    (channel.playerUuid.equals(this.playerFor) ? IS_OWN_FLAG : 0)
                            | (channel.isPrivate ? IS_PRIVATE_FLAG : 0)
                            | (channel.isSelected ? IS_SELECTED_FLAG : 0)
                            | (ChannelUtil.isChannelDistanceValid(channel, this.te) ? IN_RANGE_FLAG : 0)
                            | (ChannelUtil.isChannelDimensionValid(channel, this.te) ? IN_VALID_DIMENSION_FLAG : 0));
        });
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(this::handleWork);
        ctx.get().setPacketHandled(true);
    }

    @SuppressWarnings("resource")
    public void handleWork() {
        Minecraft.getInstance().level
                .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                .ifPresent((tesseractChannels) -> {

                    tesseractChannels.clearChannels();

                    this.channels.forEachOrdered(channel -> tesseractChannels.addChannelUnchecked(channel));
                });
    }
}
