package ftm._0xfmel.itdmtrct.utils.fluids;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackHelper {
    public static CompoundNBT saveAllTanks(CompoundNBT pTag, NonNullList<FluidStack> pList) {
        return saveAllTanks(pTag, pList, true);
    }

    public static CompoundNBT saveAllTanks(CompoundNBT pTag, NonNullList<FluidStack> pList, boolean pSaveEmpty) {
        ListNBT listnbt = new ListNBT();

        for (int i = 0; i < pList.size(); ++i) {
            FluidStack fluidstack = pList.get(i);
            if (!fluidstack.isEmpty()) {
                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.putByte("Tank", (byte) i);
                fluidstack.writeToNBT(compoundnbt);
                listnbt.add(compoundnbt);
            }
        }

        if (!listnbt.isEmpty() || pSaveEmpty) {
            pTag.put("Tanks", listnbt);
        }

        return pTag;
    }

    public static void loadAllTanks(CompoundNBT pTag, NonNullList<FluidStack> pList) {
        ListNBT listnbt = pTag.getList("Tanks", 10);

        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            int j = compoundnbt.getByte("Tank") & 255;
            if (j >= 0 && j < pList.size()) {
                pList.set(j, FluidStack.loadFluidStackFromNBT(compoundnbt));
            }
        }
    }
}
