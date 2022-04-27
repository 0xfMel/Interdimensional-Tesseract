package ftm._0xfmel.itdmtrct.capabilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.xml.bind.ValidationException;

import ftm._0xfmel.itdmtrct.handers.ModPacketHander;
import ftm._0xfmel.itdmtrct.network.UpdateChannelMessage;
import ftm._0xfmel.itdmtrct.utils.ValidationUtil;
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

    public static class TesseractChannel implements INBTSerializable<CompoundNBT> {
        public static final int SLOTS_COUNT = 6;

        public int id;
        public String name;
        public UUID playerUuid;
        public boolean isPrivate;
        public BlockPos pos;
        public String dimension;
        public boolean isSelected;

        private NonNullList<ItemStack> itemStacks = null;

        private List<Runnable> deleteListeners = new ArrayList<>();

        public int itemInterfaceCount = 0;

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

        public void addItemInterfaceCount(int num) {
            this.itemInterfaceCount += num;
        }

        public void addItemInterfaceCount() {
            this.addItemInterfaceCount(1);
        }

        public void removeItemInterfaceCount(int num) {
            this.itemInterfaceCount -= num;
        }

        public void removeItemInterfaceCount() {
            this.removeItemInterfaceCount(1);
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("id", this.id);
            nbt.putString("name", this.name);
            nbt.putUUID("player", this.playerUuid);
            nbt.putBoolean("private", this.isPrivate);
            nbt.putBoolean("selected", this.isSelected);
            nbt.putLong("pos", this.pos.asLong());
            nbt.putString("dim", this.dimension);
            nbt.putInt("itemInterfaceCount", this.itemInterfaceCount);

            if (this.itemStacks != null) {
                ItemStackHelper.saveAllItems(nbt, this.itemStacks);
            }

            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            this.id = nbt.getInt("id");
            this.name = nbt.getString("name");
            this.playerUuid = nbt.getUUID("player");
            this.isPrivate = nbt.getBoolean("private");
            this.isSelected = nbt.getBoolean("selected");
            this.pos = BlockPos.of(nbt.getLong("pos"));
            this.dimension = nbt.getString("dim");
            this.itemInterfaceCount = nbt.getInt("itemInterfaceCount");

            if (nbt.contains("Items", 9)) {
                if (this.itemStacks == null) {
                    this.itemStacks = NonNullList.withSize(SLOTS_COUNT, ItemStack.EMPTY);
                }
                ItemStackHelper.loadAllItems(nbt, this.itemStacks);
            }
        }

        public void addDeleteListener(Runnable listener) {
            if (!this.deleteListeners.contains(listener)) {
                this.deleteListeners.add(listener);
            }
        }

        public void removeDeleteListener(Runnable listener) {
            this.deleteListeners.remove(listener);
        }

        public void deleted() {
            this.deleteListeners.forEach(Runnable::run);
            this.deleteListeners.clear();
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
                // this.channels.put(id, newChannel);
                channel.deserializeNBT(newChannel.serializeNBT());
                ModPacketHander.INSTANCE.sendToServer(new UpdateChannelMessage(newChannel));
            } else {
                if (doCheck) {
                    ValidationUtil.assertLength(newChannel.name, 20, "Channel name from client");
                }
                // this.channels.put(id, newChannel);
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
            ValidationUtil.assertLength(newChannel.name, 20, "Channel name from client");
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
