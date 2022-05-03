package ftm._0xfmel.itdmtrct.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.Maps;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels.TesseractChannel;
import ftm._0xfmel.itdmtrct.capabilities.TesseractChannelsCapability;
import ftm._0xfmel.itdmtrct.client.model.TesseractBakedModel;
import ftm._0xfmel.itdmtrct.containers.TesseractContainer;
import ftm._0xfmel.itdmtrct.gameobjects.block.AbstractTesseractInterfaceBlock;
import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItems;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import ftm._0xfmel.itdmtrct.utils.TesseractInterfaceCounter;
import ftm._0xfmel.itdmtrct.utils.energy.AtomicEnergyStorage;
import ftm._0xfmel.itdmtrct.utils.interfaces.ITickableTesseractInterfaceTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;

public class InterdimensionalTesseractTile extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
    public static final int DATA_SLOTS_COUNT = 2;

    private static final VoxelShape POWER_SHAPE_NORTH = Block.box(3, 3, 0, 13, 13, 2);
    private static final VoxelShape POWER_SHAPE_EAST = Block.box(14, 3, 3, 16, 13, 13);
    private static final VoxelShape POWER_SHAPE_SOUTH = Block.box(3, 3, 14, 13, 13, 16);
    private static final VoxelShape POWER_SHAPE_WEST = Block.box(0, 3, 3, 2, 13, 13);
    private static final VoxelShape POWER_SHAPE_UP = Block.box(3, 14, 3, 13, 16, 13);
    private static final VoxelShape POWER_SHAPE_DOWN = Block.box(3, 0, 3, 13, 2, 13);

    private static final Map<Direction, VoxelShape> POWER_SHAPE_BY_DIRECTION = Util
            .make(Maps.newEnumMap(Direction.class), (p_203421_0_) -> {
                p_203421_0_.put(Direction.NORTH, POWER_SHAPE_NORTH);
                p_203421_0_.put(Direction.EAST, POWER_SHAPE_EAST);
                p_203421_0_.put(Direction.SOUTH, POWER_SHAPE_SOUTH);
                p_203421_0_.put(Direction.WEST, POWER_SHAPE_WEST);
                p_203421_0_.put(Direction.UP, POWER_SHAPE_UP);
                p_203421_0_.put(Direction.DOWN, POWER_SHAPE_DOWN);
            });

    private LazyOptional<IEnergyStorage> energyStorageLazyOptional = null;

    private int channelId = -1;
    private boolean ownChannel;

    private boolean hasKeepLoadedUpgrade;

    private TesseractInterfaceCounter interfaceCounter = new TesseractInterfaceCounter();

    protected int tickInterfaceTracker;

    private List<Direction> poweredDirections = new ArrayList<>();
    private VoxelShape shapeCache = null;
    private boolean sendPoweredDirections = false;

    @OnlyIn(Dist.CLIENT)
    private boolean ownEnergy;

    protected final IIntArray dataAccess = new IIntArray() {
        public int get(int pIndex) {
            switch (pIndex) {
                case 0:
                    return InterdimensionalTesseractTile.this.ownChannel ? 1 : 0;
                case 1:
                    return InterdimensionalTesseractTile.this.getChannel()
                            .map((channel) -> channel.getEnergyForInterface().get()).orElse(0);
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
    }

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
        pCompound.putInt("channelId", this.channelId);
        pCompound.putBoolean("ownChannel", this.ownChannel);
        pCompound.putBoolean("keepLoaded", this.hasKeepLoadedUpgrade);

        pCompound.put("interfaceCounter", this.interfaceCounter.serializeNBT());

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

        if (nbt.contains("interfaceCounter")) {
            this.interfaceCounter.deserializeNBT(nbt.getCompound("interfaceCounter"));
        }
    }

    // chunk load
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        nbt.putInt("channelId", this.channelId);
        nbt.putBoolean("keepLoaded", this.hasKeepLoadedUpgrade);
        nbt.putBoolean("ownEnergy", this.useOwnEnergy());

        nbt.putIntArray("poweredDirections",
                this.poweredDirections.stream().mapToInt(Direction::get3DDataValue).toArray());

        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.ownEnergy = tag.getBoolean("ownEnergy");

        this.poweredDirections = IntStream.of(tag.getIntArray("poweredDirections"))
                .mapToObj(Direction::from3DDataValue).collect(Collectors.toList());

        this.requestModelDataUpdate();
        this.level.sendBlockUpdated(this.worldPosition, state, state, 0);

        super.handleUpdateTag(state, tag);
    }

    // update
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("channelId", this.channelId);
        nbt.putBoolean("keepLoaded", this.hasKeepLoadedUpgrade);
        nbt.putBoolean("ownEnergy", this.useOwnEnergy());

        if (this.sendPoweredDirections) {
            nbt.putIntArray("poweredDirections",
                    this.poweredDirections.stream().mapToInt(Direction::get3DDataValue).toArray());
            this.sendPoweredDirections = false;
        }

        return new SUpdateTileEntityPacket(this.worldPosition, -1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getTag();
        this.channelId = tag.getInt("channelId");
        this.hasKeepLoadedUpgrade = tag.getBoolean("keepLoaded");
        this.ownEnergy = tag.getBoolean("ownEnergy");

        boolean flag = false;
        if (tag.contains("poweredDirections")) {
            this.poweredDirections = IntStream.of(tag.getIntArray("poweredDirections"))
                    .mapToObj(Direction::from3DDataValue).collect(Collectors.toList());
            flag = true;
        }

        this.clearCache();
        this.setChanged();

        if (flag) {
            this.requestModelDataUpdate();
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 0);
        }
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            this.getChannel().ifPresent((channel) -> channel.setLatestTick(this.level.getGameTime()));
            this.updatePoweredDirections();
        }
    }

    public boolean tickInterfaces() {
        if (this.interfaceCounter.getTotal() <= 0)
            return true;

        ITickableTesseractInterfaceTile[] interfaceTiles = new ITickableTesseractInterfaceTile[this.interfaceCounter
                .getTotal()];

        int j = 0;
        for (int i = 0; i < 6; i++) {
            ITickableTesseractInterfaceTile interfaceTile = this.getTickableInterface(Direction.from3DDataValue(i));
            if (interfaceTile != null) {
                interfaceTiles[j++] = interfaceTile;
            }
        }

        if (this.tickInterfaceTracker >= interfaceTiles.length) {
            this.tickInterfaceTracker = 0;
        }

        boolean updateTracker = false;
        for (int i = this.tickInterfaceTracker; i < interfaceTiles.length; i++) {
            ITickableTesseractInterfaceTile interfaceTile = interfaceTiles[i];
            if (interfaceTile != null) {
                if (interfaceTiles[i].tesseractTick() && i == this.tickInterfaceTracker) {
                    updateTracker = true;
                }
            }
        }

        for (int i = 0; i < this.tickInterfaceTracker; i++) {
            ITickableTesseractInterfaceTile interfaceTile = interfaceTiles[i];
            if (interfaceTile != null) {
                interfaceTiles[i].tesseractTick();
            }
        }

        if (updateTracker) {
            this.tickInterfaceTracker++;
            this.tickInterfaceTracker %= interfaceTiles.length;
            return this.tickInterfaceTracker == 0;
        }

        return false;
    }

    private ITickableTesseractInterfaceTile getTickableInterface(Direction side) {
        if (this.getBlockState().getValue(SixWayBlock.PROPERTY_BY_DIRECTION.get(side))) {
            TileEntity te = this.level.getBlockEntity(this.worldPosition.relative(side));
            if (te instanceof ITickableTesseractInterfaceTile) {
                return (ITickableTesseractInterfaceTile) te;
            }
        }

        return null;
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
                            channel.addTileListener(this);
                        }
                    });
        }

        if (this.hasKeepLoadedUpgrade) {
            this.forceChunk(true);
        }

        this.handleContentsChanged();
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
                                channel.removeInterfaceCount(this.interfaceCounter);
                                channel.removeTileListener(this);
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
            this.runOnChannel((channel) -> channel.removeTileListener(this));
        }

        super.onChunkUnloaded();
    }

    public void updatePoweredDirections() {
        List<Direction> newPoweredDirections = new ArrayList<>();
        if (this.useOwnEnergy()) {
            BlockPos.Mutable mutablePos = new BlockPos.Mutable();
            for (Direction dir : Direction.values()) {
                mutablePos.setWithOffset(this.worldPosition, dir);
                TileEntity te = this.level.getBlockEntity(mutablePos);

                if (te != null
                        && !(te instanceof TesseractEnergyInterfaceTile)
                        && !(te instanceof InterdimensionalTesseractTile)
                        && te.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()).isPresent()) {

                    newPoweredDirections.add(dir);
                }
            }
        }

        if (this.poweredDirections.size() != newPoweredDirections.size()
                || !newPoweredDirections.containsAll(this.poweredDirections)) {

            this.shapeCache = null;
            this.poweredDirections = newPoweredDirections;
            this.sendPoweredDirections = true;
            this.sendChangesToClient();
        }
    }

    @Override
    public IModelData getModelData() {
        ModelDataMap.Builder dataBuilder = new ModelDataMap.Builder();
        dataBuilder.withInitial(TesseractBakedModel.POWERED_DIRECTIONS_PROPERTY, this.poweredDirections);
        return dataBuilder.build();
    }

    public VoxelShape getDynamicShape(VoxelShape baseShape) {
        if (this.shapeCache == null) {
            this.shapeCache = VoxelShapes.or(baseShape, this.poweredDirections.stream()
                    .map(InterdimensionalTesseractTile.POWER_SHAPE_BY_DIRECTION::get).toArray(VoxelShape[]::new));
        }

        return this.shapeCache;
    }

    public boolean useOwnEnergy() {
        if (this.level.isClientSide)
            return this.ownEnergy;

        return this.getChannel().map((channel) -> channel.getInterfaceCount(ModBlocks.TESSERACT_ENERGY_INTERFACE) <= 0)
                .orElse(false);
    }

    public boolean isUnpowered() {
        return this.getChannel().map((channel) -> channel.unpowered).orElse(true);
    }

    public Supplier<Boolean> getCanTransferSupplier() {
        return () -> !this.isUnpowered();
    }

    private LazyOptional<IEnergyStorage> getEnergyStorageLazyOptional() {
        if (this.useOwnEnergy()) {
            return LazyOptional.of(() -> new AtomicEnergyStorage(
                    TesseractChannel.ENERGY_CAPACITY,
                    Integer.MAX_VALUE, 0,
                    this.getEnergyForInterface()));
        }

        return LazyOptional.empty();
    }

    @Override
    public void clearCache() {
        super.clearCache();

        this.shapeCache = null;

        if (this.energyStorageLazyOptional != null) {
            LazyOptional<IEnergyStorage> oldCap = this.energyStorageLazyOptional;
            this.energyStorageLazyOptional = null;
            oldCap.invalidate();
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == CapabilityEnergy.ENERGY) {
            if (this.energyStorageLazyOptional == null) {
                this.energyStorageLazyOptional = this.getEnergyStorageLazyOptional();
            }
            return this.energyStorageLazyOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();

        if (this.energyStorageLazyOptional != null) {
            this.energyStorageLazyOptional.invalidate();
        }
    }

    public void setChangedAndUpdateConnections() {
        this.clearCache();
        this.setChanged();
        this.getBlockState().updateNeighbourShapes(this.level, this.worldPosition, 0);
        this.sendChangesToClient();
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
                channel.removeInterfaceCount(this.interfaceCounter);
                channel.removeTileListener(this);
            });

            this.channelId = id;

            this.runOnChannel((channel) -> {
                channel.addInterfaceCount(this.interfaceCounter);
                channel.addTileListener(this);
            });

            this.setChangedAndUpdateConnections();
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
        return this.level.isClientSide
                ? NonNullList.create()
                : this.getChannel().map(TesseractChannel::getStacksForInterface)
                        .orElseGet(NonNullList::create);
    }

    public NonNullList<FluidStack> getTanksForInterface() {
        return this.level.isClientSide
                ? NonNullList.create()
                : this.getChannel().map(TesseractChannel::getTanksForInterface)
                        .orElseGet(NonNullList::create);
    }

    public AtomicInteger getEnergyForInterface() {
        return this.level.isClientSide
                ? new AtomicInteger()
                : this.getChannel().map(TesseractChannel::getEnergyForInterface)
                        .orElseGet(AtomicInteger::new);
    }

    private Optional<TesseractChannel> getChannel() {
        if (this.channelId < 0)
            return Optional.empty();

        return this.level.getServer().getLevel(World.OVERWORLD)
                .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                .map((channels) -> Optional.ofNullable(channels.getChannel(this.channelId)))
                .orElseGet(() -> Optional.empty());
    }

    private void runOnChannel(Consumer<TesseractChannel> runnable) {
        if (this.channelId < 0)
            return;

        this.getChannel().ifPresent(runnable);
    }

    public void addInterfaceCount(AbstractTesseractInterfaceBlock interfaceBlock, int num) {
        this.interfaceCounter.addCount(interfaceBlock, num);
        this.setChanged();

        this.runOnChannel((channel) -> channel.addInterfaceCount(interfaceBlock, num));
    }

    public void setInterfaceCount(AbstractTesseractInterfaceBlock interfaceBlock, int num) {
        this.interfaceCounter.setCount(interfaceBlock, num);
        this.setChanged();

        this.runOnChannel((channel) -> channel.addInterfaceCount(interfaceBlock, num));
    }

    public void addInterfaceCount(AbstractTesseractInterfaceBlock interfaceBlock) {
        this.addInterfaceCount(interfaceBlock, 1);
    }

    public void removeInterfaceCount(AbstractTesseractInterfaceBlock interfaceBlock, int num) {
        this.interfaceCounter.removeCount(interfaceBlock, num);
        this.setChanged();

        this.runOnChannel((channel) -> channel.removeInterfaceCount(interfaceBlock, num));
    }

    public void removeInterfaceCount(AbstractTesseractInterfaceBlock interfaceBlock) {
        this.removeInterfaceCount(interfaceBlock, 1);
    }

    public int getInterfaceCount(AbstractTesseractInterfaceBlock interfaceBlock) {
        return this.interfaceCounter.getCount(interfaceBlock);
    }

    public void onHandlerContentsChanged() {
        this.runOnChannel((channel) -> channel.onContentsChanged());
    }

    public void handleChannelDeleted() {
        if (this.level.hasChunk(this.worldPosition.getX() / 16, this.worldPosition.getZ() / 16)) {
            this.setChannelId(-1);
        }
    }

    public void handleContentsChanged() {
        if (this.level.hasChunk(this.worldPosition.getX() / 16, this.worldPosition.getZ() / 16)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (Direction dir : Direction.values()) {
                mutable.setWithOffset(this.worldPosition, dir);
                this.level.updateNeighbourForOutputSignal(mutable, this.level.getBlockState(mutable).getBlock());
            }
        }
    }
}
