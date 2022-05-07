package ftm._0xfmel.itdmtrct.containers;

import java.util.ArrayList;
import java.util.List;

import ftm._0xfmel.itdmtrct.client.screen.TesseractScreen;
import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.handers.ModPacketHander;
import ftm._0xfmel.itdmtrct.network.ChannelListMessage;
import ftm._0xfmel.itdmtrct.tile.InterdimensionalTesseractTile;
import ftm._0xfmel.itdmtrct.utils.ConcurrencyUtil;
import ftm._0xfmel.itdmtrct.utils.FactoryMap;
import ftm._0xfmel.itdmtrct.utils.Logging;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkHooks;

public class TesseractContainer extends Container {
    public static TesseractContainer factory(int windowId, PlayerInventory inv, PacketBuffer data) {
        ChannelListMessage.decode(data).handleWork();
        TesseractScreen.prevSelectedChannel = data.readVarInt();
        TileEntity te = inv.player.level.getBlockEntity(data.readBlockPos());

        return new TesseractContainer(windowId, new IntArray(InterdimensionalTesseractTile.DATA_SLOTS_COUNT), te,
                inv.player);
    }

    public static void openFor(ServerPlayerEntity serverPlayer, InterdimensionalTesseractTile te) {
        NetworkHooks.openGui(serverPlayer, (INamedContainerProvider) te,
                (buf) -> {
                    ChannelListMessage.write(serverPlayer, te, buf);
                    buf.writeVarInt(te.getChannelId());
                    buf.writeBlockPos(te.getBlockPos());
                });
    }

    private static FactoryMap<InterdimensionalTesseractTile, List<ServerPlayerEntity>> TILE_PLAYERS_OPEN = new FactoryMap<>(
            () -> new ArrayList<>());

    private InterdimensionalTesseractTile te;
    private IIntArray data;

    @SuppressWarnings("resource")
    public TesseractContainer(int pContainerId, IIntArray data, TileEntity te, PlayerEntity player) {
        super(ModContainerTypes.TESSERACT, pContainerId);

        checkContainerDataCount(data, InterdimensionalTesseractTile.DATA_SLOTS_COUNT);
        this.data = data;
        this.addDataSlots(data);

        if (te instanceof InterdimensionalTesseractTile) {
            this.te = (InterdimensionalTesseractTile) te;
        } else {
            Logging.LOGGER.warn("Provided tile entity is not an instance of InterdimensionalTesseractTile");
            ConcurrencyUtil.runNextTick(() -> player.closeContainer(), player.level.isClientSide);
            return;
        }

        if (!te.getLevel().isClientSide) {
            if (player instanceof ServerPlayerEntity) {
                List<ServerPlayerEntity> tilePlayersOpen = TesseractContainer.TILE_PLAYERS_OPEN.factoryGet(this.te);
                if (!tilePlayersOpen.contains(player)) {
                    tilePlayersOpen.add((ServerPlayerEntity) player);
                }
            }

            ModPacketHander.EVENT.registerObject(this);
        }
    }

    @Override
    public void removed(PlayerEntity pPlayer) {
        super.removed(pPlayer);

        if (!pPlayer.level.isClientSide) {
            if (pPlayer instanceof ServerPlayerEntity) {
                TesseractContainer.TILE_PLAYERS_OPEN.factoryGet(this.te).remove(pPlayer);
            }

            ModPacketHander.EVENT.unregisterObject(this);
        }
    }

    @SubscribeEvent
    public void handleNetworkEvent(NetworkEvent e) {
        PacketBuffer payload = e.getPayload();
        if (payload != null) {
            int id = payload.readByte();
            if (id == ModPacketHander.REMOVE_UPGRADE_EVENT_ID) {
                Context ctx = e.getSource().get();
                ctx.enqueueWork(() -> {
                    PlayerEntity player = ctx.getSender();
                    if (player.containerMenu == this && this.te.getHasKeepLoadedUpgrade()) {
                        this.te.removeKeepLoadedUpgrade(player);
                    }
                });

                ctx.setPacketHandled(true);
            }
        }
    }

    @Override
    public boolean stillValid(PlayerEntity pPlayer) {
        return Container.stillValid(
                IWorldPosCallable.create(this.te.getLevel(), this.te.getBlockPos()),
                pPlayer,
                ModBlocks.INTERDIMENSIONAL_TESSERACT);
    }

    public void sendChannelListToListeners() {
        for (ServerPlayerEntity player : TesseractContainer.TILE_PLAYERS_OPEN.factoryGet(this.te)) {
            ChannelListMessage.send(player, this.te);
        }
    }

    public InterdimensionalTesseractTile getTileEntity() {
        return this.te;
    }

    @OnlyIn(Dist.CLIENT)
    public int getChannelId() {
        return this.te.getChannelId();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getOwnChannel() {
        return this.data.get(0) != 0;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getHasKeepLoadedUpgrade() {
        return this.te.getHasKeepLoadedUpgrade();
    }

    @OnlyIn(Dist.CLIENT)
    public int getEnergy() {
        return this.data.get(1);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getUseOwnEnergy() {
        return this.te.useOwnEnergy();
    }
}
