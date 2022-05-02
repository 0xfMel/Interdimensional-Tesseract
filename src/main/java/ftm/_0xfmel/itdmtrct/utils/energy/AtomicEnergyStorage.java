package ftm._0xfmel.itdmtrct.utils.energy;

import java.util.concurrent.atomic.AtomicInteger;

import net.minecraftforge.energy.IEnergyStorage;

public class AtomicEnergyStorage implements IEnergyStorage {
    protected AtomicInteger energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public AtomicEnergyStorage(int capacity, int maxReceive, int maxExtract, AtomicInteger energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = energy;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        int energyReceived = Math.min(capacity - energy.get(), Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energy.addAndGet(energyReceived);
            this.onEnergyChanged();
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        int energyExtracted = Math.min(energy.get(), Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            energy.updateAndGet((i) -> i - energyExtracted);
            this.onEnergyChanged();
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return energy.get();
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    public int getMaxReceive() {
        return this.maxReceive;
    }

    public int getMaxExtract() {
        return this.maxExtract;
    }

    protected void onEnergyChanged() {
    }
}
