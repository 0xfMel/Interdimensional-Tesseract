package ftm._0xfmel.itdmtrct.gameobjects.block;

import ftm._0xfmel.itdmtrct.tile.TesseractEnergyInterfaceTile;
import ftm._0xfmel.itdmtrct.utils.energy.EnergyUtil;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

public class EnergyTesseractInterfaceBlock extends AbstractTesseractInterfaceBlock {
    public EnergyTesseractInterfaceBlock() {
        super("tesseract_energy_interface", TesseractEnergyInterfaceTile::new);
    }

    @Override
    public int getAnalogOutputSignal(BlockState pBlockState, World pLevel, BlockPos pPos) {
        TileEntity te = pLevel.getBlockEntity(pPos);

        if (te != null) {
            return te.getCapability(CapabilityEnergy.ENERGY)
                    .map(EnergyUtil::getRedstoneSignalFromEnergyStorage).orElse(0);
        }

        return 0;
    }
}
