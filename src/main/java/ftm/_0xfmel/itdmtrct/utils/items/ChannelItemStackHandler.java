package ftm._0xfmel.itdmtrct.utils.items;

import ftm._0xfmel.itdmtrct.tile.InterdimensionalTesseractTile;
import net.minecraftforge.items.ItemStackHandler;

public class ChannelItemStackHandler extends DisableableItemStackHandler {
    private final InterdimensionalTesseractTile tesseractTile;

    public ChannelItemStackHandler(InterdimensionalTesseractTile tesseractTile) {
        super(tesseractTile.getStacksForInterface(), tesseractTile.getCanTransferSupplier());

        this.tesseractTile = tesseractTile;
    }

    @SuppressWarnings("resource")
    public static ItemStackHandler getSidedHandler(InterdimensionalTesseractTile tesseractTile) {
        if (tesseractTile.getLevel().isClientSide) {
            return new ItemStackHandler(tesseractTile.getStacksForInterface());
        }

        return new ChannelItemStackHandler(tesseractTile);
    }

    @Override
    protected void onContentsChanged(int slot) {
        tesseractTile.onHandlerContentsChanged();
    }
}
