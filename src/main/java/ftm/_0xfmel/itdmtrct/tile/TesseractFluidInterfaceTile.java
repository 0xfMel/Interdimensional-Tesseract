package ftm._0xfmel.itdmtrct.tile;

import ftm._0xfmel.itdmtrct.utils.fluids.ChannelMultiTankFluidHandler;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TesseractFluidInterfaceTile extends AbstractTesseractInterfaceTile<IFluidHandler> {
    public TesseractFluidInterfaceTile() {
        super(ModTileEntityTypes.TESSERACT_FLUID_INTERFACE, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
    }

    public TesseractFluidInterfaceTile(Direction direction) {
        super(ModTileEntityTypes.TESSERACT_FLUID_INTERFACE, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction);
    }

    @Override
    protected IFluidHandler getCapabilityImplementation(InterdimensionalTesseractTile tesseractTile) {
        return ChannelMultiTankFluidHandler.getSidedHandler(tesseractTile);
    }
}
