package ftm._0xfmel.itdmtrct.network;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels;
import ftm._0xfmel.itdmtrct.capabilities.TesseractChannelsCapability;
import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels.TesseractChannel;
import ftm._0xfmel.itdmtrct.containers.TesseractContainer;
import ftm._0xfmel.itdmtrct.tile.InterdimensionalTesseractTile;
import ftm._0xfmel.itdmtrct.utils.Logging;
import ftm._0xfmel.itdmtrct.utils.exception.ValidationException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateChannelMessage {
    private final int id;
    private final String name;
    private final boolean isPrivate;

    public UpdateChannelMessage(TesseractChannel channel) {
        this(channel.id, channel.name, channel.isPrivate);
    }

    private UpdateChannelMessage(int id, String name, boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
    }

    public static UpdateChannelMessage decode(PacketBuffer buf) {
        return new UpdateChannelMessage(buf.readVarInt(), buf.readUtf(), buf.readBoolean());
    }

    public void encode(PacketBuffer buf) {
        buf.writeVarInt(this.id);
        buf.writeUtf(this.name);
        buf.writeBoolean(this.isPrivate);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();

            boolean flag = false;
            InterdimensionalTesseractTile te = null;
            TesseractContainer container = null;
            int teChannelId = -1;

            if (player.containerMenu instanceof TesseractContainer && player.containerMenu.stillValid(player)) {
                container = (TesseractContainer) player.containerMenu;
                te = container.getTileEntity();
                teChannelId = te.getChannelId();
                boolean ownChannel = te.getOwnChannel();
                flag = (teChannelId == this.id && ownChannel) || (!ownChannel && this.id < 0);
            }

            if (!flag)
                return;

            Optional<ITesseractChannels> tesseractChannels = player.getServer().getLevel(World.OVERWORLD)
                    .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY).resolve();

            if (tesseractChannels.isPresent()) {
                UUID playerUuid = player.getUUID();

                try {
                    if (this.id < 0) {
                        if (teChannelId >= 0) {
                            tesseractChannels.get().modifyChannel(teChannelId, (changeChannel) -> {
                                changeChannel.isSelected = false;
                            });
                        }

                        TesseractChannel newChannel = new TesseractChannel(this.name, playerUuid, this.isPrivate,
                                te.getBlockPos(), te.getLevel().dimension().location().toString());
                        tesseractChannels.get().addChannel(newChannel);
                        te.setChannelId(newChannel.id);
                        te.setOwnChannel(true);
                    } else {
                        if (tesseractChannels.get().getChannel(this.id).playerUuid.equals(playerUuid)) {
                            tesseractChannels.get().modifyChannel(this.id, changeChannel -> {
                                changeChannel.name = this.name;
                                changeChannel.isPrivate = this.isPrivate;
                            });
                        } else {
                            Logging.LOGGER.warn("User " + player.getName() + " (" + playerUuid
                                    + ") tried modifying someone else's channel");
                        }
                    }
                } catch (ValidationException e) {
                    Logging.LOGGER.warn("User " + player.getName() + " (" + playerUuid
                            + ") tried sending invalid data: " + e.getMessage());
                }

                container.sendChannelListToListeners();
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
