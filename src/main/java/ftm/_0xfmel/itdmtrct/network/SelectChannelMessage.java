package ftm._0xfmel.itdmtrct.network;

import java.util.function.Supplier;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels.TesseractChannel;
import ftm._0xfmel.itdmtrct.capabilities.TesseractChannelsCapability;
import ftm._0xfmel.itdmtrct.containers.TesseractContainer;
import ftm._0xfmel.itdmtrct.tile.InterdimensionalTesseractTile;
import ftm._0xfmel.itdmtrct.utils.ChannelUtil;
import ftm._0xfmel.itdmtrct.utils.Logging;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SelectChannelMessage {
    private final int id;

    public SelectChannelMessage(int id) {
        this.id = id;
    }

    public static SelectChannelMessage decode(PacketBuffer buf) {
        return new SelectChannelMessage(buf.readVarInt());
    }

    public void encode(PacketBuffer buf) {
        buf.writeVarInt(this.id);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();

            if (player.containerMenu instanceof TesseractContainer && player.containerMenu.stillValid(player)) {
                InterdimensionalTesseractTile te = ((TesseractContainer) player.containerMenu).getTileEntity();
                player.getServer().getLevel(World.OVERWORLD)
                        .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                        .ifPresent((tesseractChannels) -> {
                            int teChannelId = te.getChannelId();
                            if (teChannelId == this.id)
                                return;

                            TesseractChannel channel = tesseractChannels.getChannel(this.id);
                            if (channel == null)
                                return;

                            if (!ChannelUtil.isValid(channel, te, player)) {
                                Logging.LOGGER.warn("User " + player.getName() + " (" + player.getUUID()
                                        + ") tried selecting an invalid channel");
                                return;
                            }

                            if (te.getOwnChannel()) {
                                tesseractChannels.removeChannel(teChannelId);
                            } else if (teChannelId >= 0) {
                                tesseractChannels.modifyChannelUnchecked(teChannelId, (changeChannel) -> {
                                    changeChannel.isSelected = false;
                                });
                            }
                            te.setChannelId(this.id);
                            te.setOwnChannel(false);

                            tesseractChannels.modifyChannelUnchecked(this.id, (changeChannel) -> {
                                changeChannel.isSelected = true;
                            });
                        });
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
