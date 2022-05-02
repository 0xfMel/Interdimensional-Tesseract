package ftm._0xfmel.itdmtrct.tile;

import ftm._0xfmel.itdmtrct.utils.items.ChannelItemStackHandler;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TesseractItemInterfaceTile extends AbstractTesseractInterfaceTile<IItemHandler> {
    public TesseractItemInterfaceTile() {
        super(ModTileEntityTypes.TESSERACT_ITEM_INTERFACE, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
    }

    public TesseractItemInterfaceTile(Direction direction) {
        super(ModTileEntityTypes.TESSERACT_ITEM_INTERFACE, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
    }

    @Override
    protected IItemHandler getCapabilityImplementation(InterdimensionalTesseractTile tesseractTile) {
        return ChannelItemStackHandler.getSidedHandler(tesseractTile);
    }
}
