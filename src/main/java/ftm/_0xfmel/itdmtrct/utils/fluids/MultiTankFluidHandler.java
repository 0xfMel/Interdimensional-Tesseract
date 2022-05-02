package ftm._0xfmel.itdmtrct.utils.fluids;

import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class MultiTankFluidHandler implements IFluidHandler {
    protected NonNullList<FluidStack> tanks;
    protected int capacity;

    public MultiTankFluidHandler(int size, int capacity) {
        this.tanks = NonNullList.withSize(size, FluidStack.EMPTY);
        this.capacity = capacity;
    }

    public MultiTankFluidHandler(NonNullList<FluidStack> tanks, int capacity) {
        this.tanks = tanks;
        this.capacity = capacity;
    }

    @Override
    public int getTanks() {
        return this.tanks.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return this.tanks.get(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.capacity;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        FluidStack tankStack = this.tanks.get(tank);
        return tankStack.isEmpty() || this.tanks.get(tank).isFluidEqual(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int amount = resource.getAmount();
        int filled = 0;
        for (int i = 0; i < this.tanks.size(); i++) {
            FluidStack tank = this.tanks.get(i);
            if (tank.isEmpty() || tank.isFluidEqual(resource)) {
                FluidStack fluid = resource.copy();
                fluid.setAmount(amount - filled);
                filled += this.fill(i, fluid, action);

                if (filled >= amount)
                    return filled;
            }
        }

        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }

        int amount = resource.getAmount();
        int drained = 0;
        for (FluidStack tank : this.tanks) {
            if (!tank.isEmpty() && tank.isFluidEqual(resource)) {
                int drain = amount - drained;
                FluidStack filledStack = this.drain(tank, drain, action);
                drained += filledStack.getAmount();

                if (drained >= amount)
                    break;
            }
        }

        return drained == 0 ? FluidStack.EMPTY : new FluidStack(resource, drained);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (FluidStack tank : this.tanks) {
            if (!tank.isEmpty()) {
                FluidStack drainFluid = tank.copy();
                drainFluid.setAmount(maxDrain);
                return this.drain(drainFluid, action);
            }
        }

        return FluidStack.EMPTY;
    }

    private FluidStack drain(FluidStack fluid, int maxDrain, FluidAction action) {
        int drained = maxDrain;
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && drained > 0) {
            fluid.shrink(drained);
            this.onContentsChanged();
        }
        return stack;
    }

    private int fill(int tank, FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }
        FluidStack fluid = this.tanks.get(tank);
        if (action.simulate()) {
            if (fluid.isEmpty()) {
                return Math.min(this.capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(this.capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            fluid = new FluidStack(resource, Math.min(this.capacity, resource.getAmount()));
            this.tanks.set(tank, fluid);
            this.onContentsChanged();
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        int filled = this.capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluid.setAmount(this.capacity);
        }
        if (filled > 0)
            this.onContentsChanged();
        return filled;
    }

    protected void onContentsChanged() {

    }
}
