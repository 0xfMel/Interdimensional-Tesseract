package ftm._0xfmel.itdmtrct.utils.energy;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyUtil {
    public static int getRedstoneSignalFromEnergyStorage(IEnergyStorage energy) {
        return MathHelper.floor((float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored() * 14.0F)
                + (energy.getEnergyStored() > 0 ? 1 : 0);
    }
}
