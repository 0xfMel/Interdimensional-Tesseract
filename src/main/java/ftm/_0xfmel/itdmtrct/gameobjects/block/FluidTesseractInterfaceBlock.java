package ftm._0xfmel.itdmtrct.gameobjects.block;

import ftm._0xfmel.itdmtrct.tile.TesseractFluidInterfaceTile;
import ftm._0xfmel.itdmtrct.utils.fluids.FluidHandlerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class FluidTesseractInterfaceBlock extends AbstractTesseractInterfaceBlock {
    public FluidTesseractInterfaceBlock() {
        super("tesseract_fluid_interface", TesseractFluidInterfaceTile::new);
    }

    @Override
    public int getAnalogOutputSignal(BlockState pBlockState, World pLevel, BlockPos pPos) {
        TileEntity te = pLevel.getBlockEntity(pPos);

        if (te != null) {
            return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                    .map(FluidHandlerUtil::getRedstoneSignalFromFluidHandler).orElse(0);
        }

        return 0;
    }

    @Override
    public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand,
            BlockRayTraceResult pHit) {

        ItemStack handStack = pPlayer.getItemInHand(pHand);

        if (pLevel.isClientSide) {
            return handStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve().isPresent()
                    ? ActionResultType.SUCCESS
                    : ActionResultType.PASS;
        }

        TileEntity te = pLevel.getBlockEntity(pPos);

        if (te != null) {
            if (te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map((handler) -> {
                FluidActionResult result = FluidUtil
                        .tryEmptyContainerAndStow(handStack, handler,
                                new InvWrapper(pPlayer.inventory),
                                Integer.MAX_VALUE, pPlayer,
                                true);

                if (result.isSuccess()) {
                    pPlayer.setItemInHand(pHand, result.getResult());
                    return true;
                }

                FluidActionResult result1 = FluidUtil
                        .tryFillContainerAndStow(handStack, handler,
                                new InvWrapper(pPlayer.inventory),
                                Integer.MAX_VALUE, pPlayer,
                                true);

                if (result1.isSuccess()) {
                    pPlayer.setItemInHand(pHand, result.getResult());
                    return true;
                }

                return false;
            }).orElse(false)) {
                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.PASS;
    }
}
