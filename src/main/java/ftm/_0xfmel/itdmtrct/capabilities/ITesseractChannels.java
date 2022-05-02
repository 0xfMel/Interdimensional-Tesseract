package ftm._0xfmel.itdmtrct.capabilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.xml.bind.ValidationException;

import ftm._0xfmel.itdmtrct.gameobjects.block.AbstractTesseractInterfaceBlock;
import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.handers.ModPacketHander;
import ftm._0xfmel.itdmtrct.network.UpdateChannelMessage;
import ftm._0xfmel.itdmtrct.tile.InterdimensionalTesseractTile;
import ftm._0xfmel.itdmtrct.utils.TesseractInterfaceCounter;
import ftm._0xfmel.itdmtrct.utils.ValidationUtil;
import ftm._0xfmel.itdmtrct.utils.fluids.FluidStackHelper;
import ftm._0xfmel.itdmtrct.utils.interfaces.IInterfaceCounterListener;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public interface ITesseractChannels {
    Stream<TesseractChannel> getChannels();

    TesseractChannel getChannel(int id);

    void modifyChannel(int id, Consumer<TesseractChannel> change) throws ValidationException;

    void modifyChannelUnchecked(int id, Consumer<TesseractChannel> change);

    void addChannel(TesseractChannel newChannel) throws ValidationException;

    void addChannelUnchecked(TesseractChannel newChannel);

    void removeChannel(int id);

    void clearChannels();

    int getNextId();

    int getChannelsSize();

    void setNextId(int nextId);

    void setIsClientSide(boolean isClientSide);

    public static class TesseractChannel implements INBTSerializable<CompoundNBT>, IInterfaceCounterListener {
        public static final int SLOTS_COUNT = 6;
        public static final int TANKS_COUNT = 8;
        public static final int TANKS_CAPACITY = FluidAttributes.BUCKET_VOLUME;
        public static final int ENERGY_CAPACITY_INTERFACE = 500000;
        public static final int ENERGY_MAX_TRANSFER = 8000;
        public static final int ENERGY_CAPACITY = 200000;
        public static final int ENERGY_PER_TICK = 1500;
        public static final int UNPOWERED_COOLDOWN_TICKS = 15;
        public static final int POWERED_COOLDOWN_TICKS = 5;

        public int id;
        public String name;
        public UUID playerUuid;
        public boolean isPrivate;
        public BlockPos pos;
        public String dimension;
        public boolean isSelected;
        public boolean unpowered = true;

        private NonNullList<ItemStack> itemStacks = null;
        private NonNullList<FluidStack> tanks = null;

        private AtomicInteger energy = new AtomicInteger(0);

        private List<InterdimensionalTesseractTile> listeningTiles = new ArrayList<>();

        private TesseractInterfaceCounter interfaceCounter = new TesseractInterfaceCounter(this);

        private long latestTick;
        private long cooldownEndTick;

        private int tickTileTracker;

        @OnlyIn(Dist.CLIENT)
        public boolean inRange;

        @OnlyIn(Dist.CLIENT)
        public boolean inValidDimension;

        private TesseractChannel(int id, String name, UUID playerUuid, boolean isPrivate, boolean isSelected,
                BlockPos pos,
                String dimension) {
            this.id = id;
            this.name = name;
            this.playerUuid = playerUuid;
            this.isPrivate = isPrivate;
            this.isSelected = isSelected;
            this.pos = pos;
            this.dimension = dimension;
        }

        @OnlyIn(Dist.CLIENT)
        public TesseractChannel(int id, String name, UUID playerUuid, boolean isPrivate, boolean isSelected,
                boolean inRange,
                boolean inValidDimension) {

            this(id, name, playerUuid, isPrivate, isSelected, null, null);
            this.inRange = inRange;
            this.inValidDimension = inValidDimension;
        }

        @OnlyIn(Dist.CLIENT)
        public TesseractChannel(int id, String name, boolean isPrivate, boolean isSelected) {
            this(id, name, null, isPrivate, isSelected, null, null);
        }

        public TesseractChannel(String name, UUID playerUuid, boolean isPrivate, BlockPos pos, String dimension) {
            this(-1, name, playerUuid, isPrivate, false, pos, dimension);
        }

        private TesseractChannel() {
        }

        public static TesseractChannel from(CompoundNBT nbt) {
            TesseractChannel tc = new TesseractChannel();
            tc.deserializeNBT(nbt);
            return tc;
        }

        public TesseractChannel copy() {
            CompoundNBT nbt = this.serializeNBT();
            return TesseractChannel.from(nbt);
        }

        public NonNullList<ItemStack> getStacksForInterface() {
            if (this.itemStacks == null) {
                this.itemStacks = NonNullList.withSize(TesseractChannel.SLOTS_COUNT, ItemStack.EMPTY);
            }
            return this.itemStacks;
        }

        public NonNullList<FluidStack> getTanksForInterface() {
            if (this.tanks == null) {
                this.tanks = NonNullList.withSize(TesseractChannel.TANKS_COUNT, FluidStack.EMPTY);
            }
            return this.tanks;
        }

        public AtomicInteger getEnergyForInterface() {
            return this.energy;
        }

        public void setLatestTick(long tick) {
            this.latestTick = tick;
        }

        public void tick(long tick) {
            if (tick <= this.latestTick) {
                if (this.energy.get() >= TesseractChannel.ENERGY_PER_TICK) {
                    this.energy.addAndGet(-TesseractChannel.ENERGY_PER_TICK);
                    if (this.unpowered && tick >= this.cooldownEndTick) {
                        this.unpowered = false;
                        this.cooldownEndTick = tick + TesseractChannel.POWERED_COOLDOWN_TICKS;
                    }
                } else {
                    if (!this.unpowered && tick >= this.cooldownEndTick) {
                        this.unpowered = true;
                        this.cooldownEndTick = tick + TesseractChannel.UNPOWERED_COOLDOWN_TICKS;
                    }
                }
            }

            if (this.tickTileTracker >= this.listeningTiles.size()) {
                this.tickTileTracker = 0;
            }

            boolean updateTracker = false;
            for (int i = this.tickTileTracker; i < this.listeningTiles.size(); i++) {
                if (this.listeningTiles.get(i).tickInterfaces() && i == 0) {
                    updateTracker = true;
                }
            }

            for (int i = 0; i < this.tickTileTracker; i++) {
                this.listeningTiles.get(i).tickInterfaces();
            }

            if (updateTracker) {
                this.tickTileTracker++;
                this.tickTileTracker %= this.listeningTiles.size();
            }
        }

        public int getInterfaceCount(AbstractTesseractInterfaceBlock interfaceBlock) {
            return this.interfaceCounter.getCount(interfaceBlock);
        }

        public void addInterfaceCount(AbstractTesseractInterfaceBlock interfaceBlock, int num) {
            this.interfaceCounter.addCount(interfaceBlock, num);
        }

        public void addInterfaceCount(AbstractTesseractInterfaceBlock interfaceBlock) {
            this.addInterfaceCount(interfaceBlock, 1);
        }

        public void removeInterfaceCount(AbstractTesseractInterfaceBlock interfaceBlock, int num) {
            this.interfaceCounter.removeCount(interfaceBlock, num);
            this.clampEnergy();
        }

        public void removeInterfaceCount(AbstractTesseractInterfaceBlock interfaceBlock) {
            this.removeInterfaceCount(interfaceBlock, 1);
            this.clampEnergy();
        }

        public void removeInterfaceCount(TesseractInterfaceCounter interfaceCounter) {
            this.interfaceCounter.removeAll(interfaceCounter);
            this.clampEnergy();
        }

        public void addInterfaceCount(TesseractInterfaceCounter interfaceCounter) {
            this.interfaceCounter.addAll(interfaceCounter);
        }

        private void clampEnergy() {
            if (this.interfaceCounter.getCount(ModBlocks.TESSERACT_ENERGY_INTERFACE) <= 0) {
                this.energy.set(Math.min(this.energy.get(), TesseractChannel.ENERGY_CAPACITY));
            }
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("id", this.id);
            nbt.putString("name", this.name);
            nbt.putUUID("player", this.playerUuid);
            nbt.putBoolean("private", this.isPrivate);
            nbt.putBoolean("selected", this.isSelected);
            nbt.putBoolean("unpowered", this.unpowered);

            if (this.pos != null) {
                nbt.putLong("pos", this.pos.asLong());
            }

            if (this.dimension != null) {
                nbt.putString("dim", this.dimension);
            }

            nbt.put("interfaceCounter", this.interfaceCounter.serializeNBT());

            if (this.itemStacks != null) {
                ItemStackHelper.saveAllItems(nbt, this.itemStacks);
            }

            if (this.tanks != null) {
                FluidStackHelper.saveAllTanks(nbt, this.tanks);
            }

            nbt.putInt("energy", this.energy.get());

            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            this.id = nbt.getInt("id");
            this.name = nbt.getString("name");
            this.playerUuid = nbt.getUUID("player");
            this.isPrivate = nbt.getBoolean("private");
            this.isSelected = nbt.getBoolean("selected");
            this.unpowered = nbt.getBoolean("unpowered");

            if (nbt.contains("pos")) {
                this.pos = BlockPos.of(nbt.getLong("pos"));
            }

            if (nbt.contains("dim")) {
                this.dimension = nbt.getString("dim");
            }

            if (nbt.contains("interfaceCounter")) {
                this.interfaceCounter.deserializeNBT(nbt.getCompound("interfaceCounter"));
            }

            if (nbt.contains("Items", 9)) {
                if (this.itemStacks == null) {
                    this.itemStacks = NonNullList.withSize(SLOTS_COUNT, ItemStack.EMPTY);
                }
                ItemStackHelper.loadAllItems(nbt, this.itemStacks);
            }

            if (nbt.contains("Tanks", 9)) {
                if (this.tanks == null) {
                    this.tanks = NonNullList.withSize(TANKS_COUNT, FluidStack.EMPTY);
                }
                FluidStackHelper.loadAllTanks(nbt, this.tanks);
            }

            this.energy.set(nbt.getInt("energy"));
        }

        public void addTileListener(InterdimensionalTesseractTile tile) {
            if (!this.listeningTiles.contains(tile)) {
                this.listeningTiles.add(tile);
            }
        }

        public void removeTileListener(InterdimensionalTesseractTile tile) {
            this.listeningTiles.remove(tile);
        }

        public void deleted() {
            this.iterateListeners(InterdimensionalTesseractTile::handleChannelDeleted);
            this.listeningTiles.clear();
        }

        public void onContentsChanged() {
            this.iterateListeners(InterdimensionalTesseractTile::handleContentsChanged);
        }

        @Override
        public void onInterfaceCounterChange() {
            this.iterateListeners(InterdimensionalTesseractTile::setChangedAndUpdateConnections);
        }

        private void iterateListeners(Consumer<InterdimensionalTesseractTile> action) {
            new ArrayList<>(this.listeningTiles).forEach(action);
        }
    }

    public class Provider extends CapabilityProvider<Provider> implements ICapabilitySerializable<CompoundNBT> {
        private final LazyOptional<ITesseractChannels> tesseractChannelsLazyOptional;

        public Provider(boolean isClientSide) {
            super(Provider.class, true);

            tesseractChannelsLazyOptional = LazyOptional.of(() -> new TesseractChannels(isClientSide));
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (cap == TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY) {
                return this.tesseractChannelsLazyOptional.cast();
            }

            return super.getCapability(cap, side);
        }

        @Override
        protected void invalidateCaps() {
            super.invalidateCaps();
            this.tesseractChannelsLazyOptional.invalidate();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return this.tesseractChannelsLazyOptional
                    .map(instance -> (CompoundNBT) TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY
                            .getStorage()
                            .writeNBT(
                                    TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY,
                                    instance, null))
                    .orElseGet(() -> new CompoundNBT());
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            this.tesseractChannelsLazyOptional.ifPresent(
                    instance -> TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY.getStorage().readNBT(
                            TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY,
                            instance, null, nbt));
        }
    }

    public static class TesseractChannels implements ITesseractChannels {
        private boolean isClientSide;
        private Map<Integer, TesseractChannel> channels = new HashMap<>();
        private int nextId = 0;

        public TesseractChannels() {
        }

        public TesseractChannels(boolean isClientSide) {
            this.isClientSide = isClientSide;
        }

        @Override
        public void setIsClientSide(boolean isClientSide) {
            this.isClientSide = isClientSide;
        }

        @Override
        public int getNextId() {
            return this.nextId;
        }

        @Override
        public void setNextId(int nextId) {
            this.nextId = nextId;
        }

        @Override
        public Stream<TesseractChannel> getChannels() {
            return this.channels.entrySet().stream().map((entry) -> entry.getValue());
        }

        @Override
        public TesseractChannel getChannel(int id) {
            return this.channels.get(id);
        }

        @Override
        public int getChannelsSize() {
            return this.channels.size();
        }

        private void modifyChannel(int id, Consumer<TesseractChannel> change, boolean doCheck)
                throws ValidationException {

            TesseractChannel channel = this.getChannel(id);
            if (channel == null)
                return;
            TesseractChannel newChannel = channel.copy();
            change.accept(newChannel);

            if (this.isClientSide) {
                channel.deserializeNBT(newChannel.serializeNBT());
                ModPacketHander.INSTANCE.sendToServer(new UpdateChannelMessage(newChannel));
            } else {
                if (doCheck) {
                    ValidationUtil.assertLength(newChannel.name, 1, 20, "Channel name from client");
                }
                channel.deserializeNBT(newChannel.serializeNBT());
            }
        }

        @Override
        public void modifyChannel(int id, Consumer<TesseractChannel> change) throws ValidationException {
            this.modifyChannel(id, change, true);
        }

        @Override
        public void modifyChannelUnchecked(int id, Consumer<TesseractChannel> change) {
            try {
                this.modifyChannel(id, change, false);
            } catch (ValidationException e) {
                // impossible
            }
        }

        @Override
        public void addChannel(TesseractChannel newChannel) throws ValidationException {
            ValidationUtil.assertLength(newChannel.name, 1, 20, "Channel name from client");
            this.addChannelUnchecked(newChannel);
        }

        @Override
        public void addChannelUnchecked(TesseractChannel newChannel) {
            if (newChannel.id < 0) {
                if (this.isClientSide) {
                    ModPacketHander.INSTANCE.sendToServer(new UpdateChannelMessage(newChannel));
                } else {
                    // from client
                    newChannel.id = this.nextId++;
                    this.channels.put(newChannel.id, newChannel);
                }
            } else {
                // from save / network message
                this.channels.put(newChannel.id, newChannel);
            }
        }

        @Override
        public void removeChannel(int id) {
            TesseractChannel removedChannel = this.channels.remove(id);
            removedChannel.deleted();
        }

        @Override
        public void clearChannels() {
            this.channels.clear();
        }
    }
}
