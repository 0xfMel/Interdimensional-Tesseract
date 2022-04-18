package ftm._0xfmel.itdmtrct.capabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.xml.bind.ValidationException;

import ftm._0xfmel.itdmtrct.handers.ModPacketHander;
import ftm._0xfmel.itdmtrct.network.UpdateChannelMessage;
import ftm._0xfmel.itdmtrct.utils.ValidationUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
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

    void addChannel(TesseractChannel newChannel) throws ValidationException;

    void removeChannel(int id);

    void clearChannels();

    int getNextId();

    int getChannelsSize();

    void setNextId(int nextId);

    void setIsClientSide(boolean isClientSide);

    public static class TesseractChannel implements INBTSerializable<CompoundNBT> {
        public int id;
        public String name;
        public UUID playerUuid;
        public boolean isPrivate;
        public BlockPos pos;
        public String dimension;

        @OnlyIn(Dist.CLIENT)
        public boolean inRange;

        @OnlyIn(Dist.CLIENT)
        public boolean inValidDimension;

        private TesseractChannel(int id, String name, UUID playerUuid, boolean isPrivate, BlockPos pos,
                String dimension) {
            this.id = id;
            this.name = name;
            this.playerUuid = playerUuid;
            this.isPrivate = isPrivate;
            this.pos = pos;
            this.dimension = dimension;
        }

        @OnlyIn(Dist.CLIENT)
        public TesseractChannel(int id, String name, UUID playerUuid, boolean isPrivate, boolean inRange,
                boolean inValidDimension) {

            this(id, name, playerUuid, isPrivate, null, null);
            this.inRange = inRange;
            this.inValidDimension = inValidDimension;
        }

        @OnlyIn(Dist.CLIENT)
        public TesseractChannel(int id, String name, boolean isPrivate) {
            this(id, name, null, isPrivate, null, null);
        }

        public TesseractChannel(String name, UUID playerUuid, boolean isPrivate, BlockPos pos, String dimension) {
            this(-1, name, playerUuid, isPrivate, pos, dimension);
        }

        protected TesseractChannel(TesseractChannel oldChannel) {
            this(oldChannel.id, oldChannel.name, oldChannel.playerUuid, oldChannel.isPrivate, oldChannel.pos,
                    oldChannel.dimension);
        }

        private TesseractChannel() {
        }

        public static TesseractChannel from(CompoundNBT nbt) {
            TesseractChannel tc = new TesseractChannel();
            tc.deserializeNBT(nbt);
            return tc;
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("id", this.id);
            nbt.putString("name", this.name);
            nbt.putUUID("player", this.playerUuid);
            nbt.putBoolean("private", this.isPrivate);
            nbt.putLong("pos", this.pos.asLong());
            nbt.putString("dim", this.dimension);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            this.id = nbt.getInt("id");
            this.name = nbt.getString("name");
            this.playerUuid = nbt.getUUID("player");
            this.isPrivate = nbt.getBoolean("private");
            this.pos = BlockPos.of(nbt.getLong("pos"));
            this.dimension = nbt.getString("dim");
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

        @Override
        public void modifyChannel(int id, Consumer<TesseractChannel> change) throws ValidationException {
            TesseractChannel newChannel = new TesseractChannel(this.getChannel(id));
            change.accept(newChannel);

            if (this.isClientSide) {
                this.channels.put(id, newChannel);
                ModPacketHander.INSTANCE.sendToServer(new UpdateChannelMessage(newChannel));
            } else {
                ValidationUtil.assertLength(newChannel.name, 20, "Channel name from client");
                this.channels.put(id, newChannel);
            }
        }

        @Override
        public void addChannel(TesseractChannel newChannel) throws ValidationException {
            if (newChannel.id < 0) {
                if (this.isClientSide) {
                    ModPacketHander.INSTANCE.sendToServer(new UpdateChannelMessage(newChannel));
                } else {
                    // from client
                    ValidationUtil.assertLength(newChannel.name, 20, "Channel name from client");
                    newChannel.id = this.nextId++;
                    this.channels.put(newChannel.id, newChannel);
                }
            } else {
                // from save / network message
                ValidationUtil.assertLength(newChannel.name, 20, "Channel name from client");
                this.channels.put(newChannel.id, newChannel);
            }
        }

        @Override
        public void removeChannel(int id) {
            this.channels.remove(id);
        }

        @Override
        public void clearChannels() {
            this.channels.clear();
        }
    }
}
