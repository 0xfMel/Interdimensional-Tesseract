package ftm._0xfmel.itdmtrct.utils.fluids;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidHandlerUtil {
    public static int getRedstoneSignalFromFluidHandler(IFluidHandler fluidHandler) {
        int i = 0;
        float f = 0.0F;

        for (int j = 0; j < fluidHandler.getTanks(); ++j) {
            FluidStack fluidstack = fluidHandler.getFluidInTank(j);
            if (!fluidstack.isEmpty()) {
                f += (float) fluidstack.getAmount()
                        / (float) fluidHandler.getTankCapacity(j);
                ++i;
            }
        }

        f = f / (float) fluidHandler.getTanks();
        return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
    }
}
