package ftm._0xfmel.itdmtrct.containers;

import java.util.List;

import com.google.common.collect.Lists;

import ftm._0xfmel.itdmtrct.client.screen.TesseractScreen;
import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.network.ChannelListMessage;
import ftm._0xfmel.itdmtrct.tile.InterdimenstionalTesseractTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class TesseractContainer extends Container {
    public static TesseractContainer factory(int windowId, PlayerInventory inv, PacketBuffer data) {
        ChannelListMessage.decode(data).handleWork();
        TesseractScreen.prevSelectedChannel = data.readVarInt();
        return new TesseractContainer(windowId, new IntArray(InterdimenstionalTesseractTile.DATA_SLOTS_COUNT));
    }

    public static void openFor(ServerPlayerEntity serverPlayer, InterdimenstionalTesseractTile te) {
        NetworkHooks.openGui(serverPlayer, (INamedContainerProvider) te,
                (buf) -> {
                    ChannelListMessage.write(serverPlayer, te.getBlockPos(), buf);
                    buf.writeVarInt(te.getChannelId());
                });
    }

    private InterdimenstionalTesseractTile te;
    private IIntArray data;

    private final List<IContainerListener> containerListeners = Lists.newArrayList();

    private TesseractContainer(int pContainerId, IIntArray data) {
        super(ModContainerTypes.TESSERACT, pContainerId);

        checkContainerDataCount(data, InterdimenstionalTesseractTile.DATA_SLOTS_COUNT);
        this.data = data;
        this.addDataSlots(data);
    }

    public TesseractContainer(int pContainerId, InterdimenstionalTesseractTile te, IIntArray data) {
        this(pContainerId, data);
        this.te = te;
    }

    @Override
    public boolean stillValid(PlayerEntity pPlayer) {
        return Container.stillValid(
                IWorldPosCallable.create(this.te.getLevel(), this.te.getBlockPos()),
                pPlayer,
                ModBlocks.INTERDIMENSIONAL_TESSERACT);
    }

    @Override
    public void addSlotListener(IContainerListener pListener) {
        super.addSlotListener(pListener);

        this.containerListeners.add(pListener);
    }

    @Override
    public void removeSlotListener(IContainerListener pListener) {
        super.removeSlotListener(pListener);

        this.containerListeners.remove(pListener);
    }

    public void sendChannelListToListeners() {
        for (IContainerListener listener : this.containerListeners) {
            if (listener instanceof ServerPlayerEntity) {
                ChannelListMessage.send((ServerPlayerEntity) listener, this.te.getBlockPos());
            }
        }
    }

    public InterdimenstionalTesseractTile getTileEntity() {
        return this.te;
    }

    @OnlyIn(Dist.CLIENT)
    public int getChannelId() {
        return this.data.get(0);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getOwnChannel() {
        return this.data.get(1) != 0;
    }
}
