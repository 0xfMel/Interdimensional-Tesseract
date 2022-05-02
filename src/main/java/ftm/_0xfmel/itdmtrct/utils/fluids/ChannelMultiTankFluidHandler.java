package ftm._0xfmel.itdmtrct.utils.fluids;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels.TesseractChannel;
import ftm._0xfmel.itdmtrct.tile.InterdimensionalTesseractTile;

public class ChannelMultiTankFluidHandler extends DisableableMultiTankFluidHandler {
    private final InterdimensionalTesseractTile tesseractTile;

    public ChannelMultiTankFluidHandler(InterdimensionalTesseractTile tesseractTile) {
        super(tesseractTile.getTanksForInterface(), TesseractChannel.TANKS_CAPACITY,
                tesseractTile.getCanTransferSupplier());

        this.tesseractTile = tesseractTile;
    }

    @SuppressWarnings("resource")
    public static MultiTankFluidHandler getSidedHandler(InterdimensionalTesseractTile tesseractTile) {
        if (tesseractTile.getLevel().isClientSide) {
            return new MultiTankFluidHandler(tesseractTile.getTanksForInterface(), TesseractChannel.TANKS_CAPACITY);
        }

        return new ChannelMultiTankFluidHandler(tesseractTile);
    }

    @Override
    protected void onContentsChanged() {
        tesseractTile.onHandlerContentsChanged();
    }
}
