package ftm._0xfmel.itdmtrct.tile;

import java.util.Optional;
import java.util.function.Consumer;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels.TesseractChannel;
import ftm._0xfmel.itdmtrct.capabilities.TesseractChannelsCapability;
import ftm._0xfmel.itdmtrct.containers.TesseractContainer;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItems;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;

public class InterdimensionalTesseractTile extends TileEntity implements INamedContainerProvider {
    public static final int DATA_SLOTS_COUNT = 1;

    private int channelId = -1;
    private boolean ownChannel;

    private boolean hasKeepLoadedUpgrade;

    private int itemInterfaceCount = 0;

    private final Runnable channelDeleteHandler;

    protected final IIntArray dataAccess = new IIntArray() {
        public int get(int pIndex) {
            switch (pIndex) {
                case 0:
                    return InterdimensionalTesseractTile.this.ownChannel ? 1 : 0;
                default:
                    return 0;
            }
        }

        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0:
                    InterdimensionalTesseractTile.this.ownChannel = pValue != 0;
                    break;
            }
        }

        public int getCount() {
            return InterdimensionalTesseractTile.DATA_SLOTS_COUNT;
        }
    };

    public InterdimensionalTesseractTile() {
        super(ModTileEntityTypes.INTERDIMENSIONAL_TESSERACT);
        this.channelDeleteHandler = this::handleChannelDeleted;
    }

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
        pCompound.putInt("channelId", this.channelId);
        pCompound.putBoolean("ownChannel", this.ownChannel);
        pCompound.putBoolean("keepLoaded", this.hasKeepLoadedUpgrade);
        pCompound.putInt("itemInterfaceCount", this.itemInterfaceCount);

        return super.save(pCompound);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);

        this.channelId = nbt.getInt("channelId");

        if (nbt.contains("ownChannel")) {
            this.ownChannel = nbt.getBoolean("ownChannel");
        }

        this.hasKeepLoadedUpgrade = nbt.getBoolean("keepLoaded");

        if (nbt.contains("itemInterfaceCount")) {
            this.itemInterfaceCount = nbt.getInt("itemInterfaceCount");
        }
    }

    // chunk load
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        nbt.putInt("channelId", this.channelId);
        nbt.putBoolean("keepLoaded", this.hasKeepLoadedUpgrade);
        return nbt;
    }

    // update
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("channelId", this.channelId);
        nbt.putBoolean("keepLoaded", this.hasKeepLoadedUpgrade);
        return new SUpdateTileEntityPacket(this.worldPosition, -1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getTag();
        this.channelId = tag.getInt("channelId");
        this.hasKeepLoadedUpgrade = tag.getBoolean("keepLoaded");
        this.setChanged();
    }

    // used as called after load during chunk load
    // not used for placement logic as its called before load if NBT on item
    // onPlace used instead
    @Override
    public void onLoad() {
        if (!this.level.isClientSide) {
            this.onLoadPlace();
        }

        super.onLoad();
    }

    public void onLoadPlace() {
        if (this.channelId >= 0) {
            this.level.getServer().getLevel(World.OVERWORLD)
                    .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                    .ifPresent((tesseractChannels) -> {
                        TesseractChannel channel = tesseractChannels.getChannel(this.channelId);
                        if (channel == null
                                || (this.ownChannel && (!channel.pos.equals(this.worldPosition)
                                        || !channel.dimension.equals(this.level.dimension().location().toString())))) {
                            this.channelId = -1;
                            this.ownChannel = false;
                            this.setChanged();
                        }

                        if (channel != null) {
                            channel.addDeleteListener(this.channelDeleteHandler);
                        }
                    });
        }

        if (this.hasKeepLoadedUpgrade) {
            this.forceChunk(true);
        }
    }

    private void forceChunk(boolean add) {
        if (this.level instanceof ServerWorld) {
            ForgeChunkManager.forceChunk((ServerWorld) this.level, ModGlobals.MOD_ID, this.worldPosition,
                    this.worldPosition.getX() / 16, this.worldPosition.getZ() / 16, add, true);
        }
    }

    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return new TesseractContainer(p_createMenu_1_, this.dataAccess, this, p_createMenu_3_);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.tesseract.title");
    }

    @Override
    public void setRemoved() {
        if (!this.level.isClientSide) {
            if (this.hasKeepLoadedUpgrade) {
                this.forceChunk(false);
            }

            if (this.channelId >= 0) {
                this.level.getServer().getLevel(World.OVERWORLD)
                        .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                        .ifPresent((channels) -> {
                            TesseractChannel channel = channels.getChannel(this.channelId);

                            if (channel != null) {
                                channel.removeItemInterfaceCount(this.itemInterfaceCount);
                                channel.removeDeleteListener(this.channelDeleteHandler);
                            }

                            if (this.ownChannel) {
                                channels.removeChannel(this.channelId);
                            } else {
                                channels.modifyChannelUnchecked(this.channelId, (channel1) -> {
                                    channel1.isSelected = false;
                                });
                            }
                        });
            }
        }

        super.setRemoved();
    }

    @Override
    public void onChunkUnloaded() {
        if (!this.level.isClientSide && this.channelId >= 0) {
            this.runOnChannel((channel) -> channel.removeDeleteListener(this.channelDeleteHandler));
        }

        super.onChunkUnloaded();
    }

    public int getChannelId() {
        return this.channelId;
    }

    public boolean getOwnChannel() {
        return this.ownChannel;
    }

    public void setChannelId(int id) {
        if (this.channelId != id) {

            this.runOnChannel((channel) -> {
                channel.removeItemInterfaceCount(this.itemInterfaceCount);
                channel.removeDeleteListener(this.channelDeleteHandler);
            });

            this.channelId = id;

            this.runOnChannel((channel) -> {
                channel.addItemInterfaceCount(this.itemInterfaceCount);
                channel.addDeleteListener(this.channelDeleteHandler);
            });

            this.setChanged();
            this.sendChangesToClient();
        }
    }

    public void setOwnChannel(boolean ownChannel) {
        if (ownChannel != this.ownChannel) {
            this.ownChannel = ownChannel;
            this.setChanged();
        }
    }

    public boolean getHasKeepLoadedUpgrade() {
        return this.hasKeepLoadedUpgrade;
    }

    public void setHasKeepLoadedUpgrade(boolean add) {
        if (this.hasKeepLoadedUpgrade != add) {
            this.hasKeepLoadedUpgrade = add;
            this.setChanged();
            this.forceChunk(add);
            this.sendChangesToClient();
        }
    }

    private void sendChangesToClient() {
        BlockState state = this.level.getBlockState(this.worldPosition);
        this.level.sendBlockUpdated(this.worldPosition, state, state, 2);
    }

    public void removeKeepLoadedUpgrade(PlayerEntity player) {
        if (this.hasKeepLoadedUpgrade) {
            this.setHasKeepLoadedUpgrade(false);
            ItemStack stack = new ItemStack(ModItems.KEEP_LOADED_UPGRADE);
            if (!player.inventory.add(stack) && !player.isCreative()) {
                player.drop(stack, false);
            }
        }
    }

    public NonNullList<ItemStack> getStacksForInterface() {
        return (this.level.isClientSide ? this.level : this.level.getServer().getLevel(World.OVERWORLD))
                .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                .map((channels) -> Optional.ofNullable(channels.getChannel(this.channelId)))
                .flatMap((channelOptional) -> channelOptional.map((channel) -> channel.getStacksForInterface()))
                .orElseGet(() -> NonNullList.create());
    }

    private void runOnChannel(Consumer<TesseractChannel> runnable) {
        if (this.channelId < 0)
            return;

        this.level.getServer().getLevel(World.OVERWORLD)
                .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                .map((channels) -> Optional.ofNullable(channels.getChannel(this.channelId)))
                .ifPresent((channelOptional) -> channelOptional.ifPresent(runnable));
    }

    public void addItemInterfaceCount(int num) {
        this.itemInterfaceCount += num;
        this.setChanged();

        // todo - handle adding TE "itemInterfaceCount" to channel when channel selected
        // handle incrementing "itemInterfaceCount" of the new TE when placing tesseract
        // next to interface
        // handle placing with nbt for above

        this.runOnChannel((channel) -> channel.addItemInterfaceCount(num));
    }

    public void setItemInterfaceCount(int num) {
        this.itemInterfaceCount = num;
        this.setChanged();

        this.runOnChannel((channel) -> channel.addItemInterfaceCount(num));
    }

    public void addItemInterfaceCount() {
        this.addItemInterfaceCount(1);
    }

    public void removeItemInterfaceCount(int num) {
        this.itemInterfaceCount -= num;
        this.setChanged();

        this.runOnChannel((channel) -> channel.removeItemInterfaceCount(num));
    }

    public void removeItemInterfaceCount() {
        this.removeItemInterfaceCount(1);
    }

    private void handleChannelDeleted() {
        if (this.level.hasChunk(this.worldPosition.getX() / 16, this.worldPosition.getZ() / 16)) {
            this.setChannelId(-1);
        }
    }

    public int getItemInterfaceCount() {
        return this.itemInterfaceCount;
    }
}
