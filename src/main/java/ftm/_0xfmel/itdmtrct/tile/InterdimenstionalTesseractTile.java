package ftm._0xfmel.itdmtrct.tile;

import javax.xml.bind.ValidationException;

import ftm._0xfmel.itdmtrct.capabilities.TesseractChannelsCapability;
import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels.TesseractChannel;
import ftm._0xfmel.itdmtrct.containers.TesseractContainer;
import ftm._0xfmel.itdmtrct.utils.Logging;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class InterdimenstionalTesseractTile extends TileEntity implements INamedContainerProvider {
    public static final int DATA_SLOTS_COUNT = 2;

    private int channelId = -1;
    private boolean ownChannel;

    protected final IIntArray dataAccess = new IIntArray() {
        public int get(int pIndex) {
            switch (pIndex) {
                case 0:
                    return InterdimenstionalTesseractTile.this.channelId;
                case 1:
                    return InterdimenstionalTesseractTile.this.ownChannel ? 1 : 0;
                default:
                    return 0;
            }
        }

        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0:
                    InterdimenstionalTesseractTile.this.channelId = pValue;
                    break;
                case 1:
                    InterdimenstionalTesseractTile.this.ownChannel = pValue != 0;
                    break;
            }
        }

        public int getCount() {
            return InterdimenstionalTesseractTile.DATA_SLOTS_COUNT;
        }
    };

    public InterdimenstionalTesseractTile() {
        super(ModTileEntityTypes.INTERDIMENSIONAL_TESSERACT);
    }

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
        pCompound.putInt("channelId", this.channelId);
        pCompound.putBoolean("ownChannel", this.ownChannel);

        return super.save(pCompound);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);

        this.channelId = nbt.getInt("channelId");
        this.ownChannel = nbt.getBoolean("ownChannel");

        if (this.level != null && this.channelId >= 0) {
            this.level.getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                    .ifPresent((tesseractChannels) -> {
                        TesseractChannel channel = tesseractChannels.getChannel(this.channelId);
                        if (channel == null || !channel.pos.equals(this.getBlockPos())
                                || !channel.dimension.equals(this.level.dimension().toString())) {
                            this.channelId = -1;
                            this.ownChannel = false;
                            this.setChanged();
                        }
                    });
        }
    }

    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return new TesseractContainer(p_createMenu_1_, this, this.dataAccess);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.tesseract.title");
    }

    @Override
    public void setRemoved() {
        if (!this.level.isClientSide && this.ownChannel) {
            this.level.getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                    .ifPresent(channels -> channels.removeChannel(this.channelId));
        }

        super.setRemoved();
    }

    public int getChannelId() {
        return this.channelId;
    }

    public boolean getOwnChannel() {
        return this.ownChannel;
    }

    public void setChannelId(int id) {
        this.channelId = id;
        this.setChanged();
    }

    public void setOwnChannel(boolean ownChannel) {
        this.ownChannel = ownChannel;
        this.setChanged();
    }
}
