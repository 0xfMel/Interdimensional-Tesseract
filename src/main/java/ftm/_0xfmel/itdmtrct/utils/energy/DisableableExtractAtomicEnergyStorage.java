package ftm._0xfmel.itdmtrct.utils.energy;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class DisableableExtractAtomicEnergyStorage extends AtomicEnergyStorage {
    protected Supplier<Boolean> canExtract;

    public DisableableExtractAtomicEnergyStorage(int capacity, int maxReceive, int maxExtract, AtomicInteger energy,
            Supplier<Boolean> canExtract) {

        super(capacity, maxReceive, maxExtract, energy);
        this.canExtract = canExtract;
    }

    @Override
    public boolean canExtract() {
        return super.canExtract() && this.canExtract.get();
    }
}
