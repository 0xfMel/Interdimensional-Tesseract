package ftm._0xfmel.itdmtrct.gameobjects.block;

import ftm._0xfmel.itdmtrct.tile.TesseractItemInterfaceTile;
import ftm._0xfmel.itdmtrct.utils.items.ItemHandlerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

public class ItemTesseractInterfaceBlock extends AbstractTesseractInterfaceBlock {
    public ItemTesseractInterfaceBlock() {
        super("tesseract_item_interface", TesseractItemInterfaceTile::new);
    }

    @Override
    public int getAnalogOutputSignal(BlockState pBlockState, World pLevel, BlockPos pPos) {
        TileEntity te = pLevel.getBlockEntity(pPos);

        if (te != null) {
            return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .map(ItemHandlerUtil::getRedstoneSignalFromItemHandler).orElse(0);
        }

        return 0;
    }
}
