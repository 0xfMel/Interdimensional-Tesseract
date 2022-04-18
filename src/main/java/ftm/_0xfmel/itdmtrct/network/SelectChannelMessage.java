package ftm._0xfmel.itdmtrct.network;

import java.util.Optional;
import java.util.function.Supplier;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels;
import ftm._0xfmel.itdmtrct.capabilities.TesseractChannelsCapability;
import ftm._0xfmel.itdmtrct.containers.TesseractContainer;
import ftm._0xfmel.itdmtrct.tile.InterdimenstionalTesseractTile;
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
                InterdimenstionalTesseractTile te = ((TesseractContainer) player.containerMenu).getTileEntity();
                Optional<ITesseractChannels> tesseractChannels = player.getServer().getLevel(World.OVERWORLD)
                        .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY).resolve();
                if (tesseractChannels.isPresent() && tesseractChannels.get().getChannel(this.id) != null) {
                    // TODO validity check
                    if (te.getChannelId() != this.id) {
                        if (te.getOwnChannel()) {
                            tesseractChannels.get().removeChannel(te.getChannelId());
                        }
                        te.setChannelId(this.id);
                        te.setOwnChannel(false);
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
