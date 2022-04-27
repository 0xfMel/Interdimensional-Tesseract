package ftm._0xfmel.itdmtrct.capabilities;

import java.security.InvalidParameterException;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels.TesseractChannel;
import ftm._0xfmel.itdmtrct.utils.CollectorsUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class TesseractChannelsCapability {
    @CapabilityInject(ITesseractChannels.class)
    public static final Capability<ITesseractChannels> TESSERACT_CHANNELS_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(ITesseractChannels.class, new Capability.IStorage<ITesseractChannels>() {
            @Override
            public INBT writeNBT(Capability<ITesseractChannels> capability, ITesseractChannels instance,
                    Direction side) {

                ListNBT list = instance.getChannels().map(channel -> channel.serializeNBT())
                        .collect(CollectorsUtil.toNBTList());
                CompoundNBT nbt = new CompoundNBT();
                nbt.put("channels", list);
                nbt.putInt("nextId", instance.getNextId());
                return nbt;
            }

            @Override
            public void readNBT(Capability<ITesseractChannels> capability, ITesseractChannels instance, Direction side,
                    INBT nbt) {

                if (nbt instanceof CompoundNBT) {
                    CompoundNBT compound = (CompoundNBT) nbt;

                    if (!compound.isEmpty()) {
                        ListNBT channels = (ListNBT) compound.get("channels");

                        channels.forEach(channelNbt -> instance
                                .addChannelUnchecked(TesseractChannel.from((CompoundNBT) channelNbt)));

                        instance.setNextId(compound.getInt("nextId"));
                    }
                } else {
                    throw new InvalidParameterException("Wrong NBT type passed to TesseractChannels.Storage.readNBT");
                }
            }
        }, ITesseractChannels.TesseractChannels::new);
    }
}
