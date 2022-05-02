package ftm._0xfmel.itdmtrct.tile;

import ftm._0xfmel.itdmtrct.utils.energy.AtomicEnergyStorage;
import ftm._0xfmel.itdmtrct.utils.energy.ChannelEnergyStorage;
import ftm._0xfmel.itdmtrct.utils.interfaces.ITickableTesseractInterfaceTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TesseractEnergyInterfaceTile
        extends AbstractTesseractInterfaceTile<IEnergyStorage> implements ITickableTesseractInterfaceTile {

    protected int outputTracker;

    public TesseractEnergyInterfaceTile() {
        super(ModTileEntityTypes.TESSERACT_ENERGY_INTERFACE, CapabilityEnergy.ENERGY);
    }

    public TesseractEnergyInterfaceTile(Direction direction) {
        super(ModTileEntityTypes.TESSERACT_ENERGY_INTERFACE, CapabilityEnergy.ENERGY, direction);
    }

    @Override
    protected IEnergyStorage getCapabilityImplementation(InterdimensionalTesseractTile tesseractTile) {
        return ChannelEnergyStorage.getSidedHandler(tesseractTile);
    }

    @Override
    public boolean tesseractTick() {
        if (this.getTesseractTile().map((tile) -> !tile.isUnpowered()).orElse(false)) {
            this.getCapability().ifPresent((cap) -> {
                if (cap instanceof AtomicEnergyStorage) {
                    for (int i = this.outputTracker; i < 6 && cap.getEnergyStored() > 0; i++) {
                        this.tryTransferOut((AtomicEnergyStorage) cap, Direction.from3DDataValue(i));
                    }

                    for (int i = 0; i < this.outputTracker && cap.getEnergyStored() > 0; i++) {
                        this.tryTransferOut((AtomicEnergyStorage) cap, Direction.from3DDataValue(i));
                    }
                }
            });
        }

        this.outputTracker++;
        this.outputTracker %= 6;
        return this.outputTracker == 0;
    }

    private void tryTransferOut(AtomicEnergyStorage cap, Direction side) {
        TileEntity te = this.level.getBlockEntity(this.worldPosition.relative(side));
        if (te != null) {
            te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite())
                    .ifPresent((e) -> {
                        if (cap.canExtract() && e.canReceive()) {
                            int maxTransfer = Math.min(cap.getEnergyStored(), cap.getMaxExtract());
                            cap.extractEnergy(e.receiveEnergy(maxTransfer, false), false);
                        }
                    });
        }
    }
}
