package ftm._0xfmel.itdmtrct.utils.fluids;

import java.util.function.Supplier;

import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public class DisableableMultiTankFluidHandler extends MultiTankFluidHandler {
    protected NonNullList<FluidStack> tanks;
    protected int capacity;
    protected Supplier<Boolean> canTransfer;

    public DisableableMultiTankFluidHandler(int size, int capacity, Supplier<Boolean> canTransfer) {
        super(size, capacity);

        this.canTransfer = canTransfer;
    }

    public DisableableMultiTankFluidHandler(NonNullList<FluidStack> tanks, int capacity,
            Supplier<Boolean> canTransfer) {

        super(tanks, capacity);

        this.canTransfer = canTransfer;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return this.canTransfer.get() && super.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (!this.canTransfer.get())
            return 0;

        return super.fill(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (!this.canTransfer.get())
            return FluidStack.EMPTY;

        return super.drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (!this.canTransfer.get())
            return FluidStack.EMPTY;

        return super.drain(maxDrain, action);
    }
}
