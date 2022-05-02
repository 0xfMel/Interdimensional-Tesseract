package ftm._0xfmel.itdmtrct.utils.energy;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels.TesseractChannel;
import ftm._0xfmel.itdmtrct.tile.InterdimensionalTesseractTile;

public class ChannelEnergyStorage extends DisableableExtractAtomicEnergyStorage {
    private final InterdimensionalTesseractTile tesseractTile;

    public ChannelEnergyStorage(InterdimensionalTesseractTile tesseractTile) {
        super(TesseractChannel.ENERGY_CAPACITY_INTERFACE, Integer.MAX_VALUE,
                TesseractChannel.ENERGY_MAX_TRANSFER, tesseractTile.getEnergyForInterface(),
                tesseractTile.getCanTransferSupplier());

        this.tesseractTile = tesseractTile;
    }

    @SuppressWarnings("resource")
    public static AtomicEnergyStorage getSidedHandler(InterdimensionalTesseractTile tesseractTile) {
        if (tesseractTile.getLevel().isClientSide) {
            return new AtomicEnergyStorage(TesseractChannel.ENERGY_CAPACITY_INTERFACE,
                    Integer.MAX_VALUE,
                    TesseractChannel.ENERGY_MAX_TRANSFER, tesseractTile.getEnergyForInterface());
        }

        return new ChannelEnergyStorage(tesseractTile);
    }

    @Override
    protected void onEnergyChanged() {
        this.tesseractTile.onHandlerContentsChanged();
    }
}
