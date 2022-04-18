package ftm._0xfmel.itdmtrct.gameobjects.block;

import ftm._0xfmel.itdmtrct.containers.TesseractContainer;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItemGroups;
import ftm._0xfmel.itdmtrct.tile.InterdimenstionalTesseractTile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class InterdimensionalTesseractBlock extends BaseBlock {
    public InterdimensionalTesseractBlock() {
        super("interdimensional_tesseract", AbstractBlock.Properties.of(Material.HEAVY_METAL)
                .strength(6f, 8f)
                .sound(SoundType.METAL)
                .lightLevel((blockState) -> 8)
                .noOcclusion(),
                ModItemGroups.TAB);
    }

    @Override
    public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand,
            BlockRayTraceResult pHit) {

        if (!pLevel.isClientSide) {
            TileEntity te = pLevel.getBlockEntity(pPos);

            if (te instanceof InterdimenstionalTesseractTile) {
                if (pPlayer instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) pPlayer;
                    TesseractContainer.openFor(serverPlayer, (InterdimenstionalTesseractTile) te);
                }
            }
        }

        return ActionResultType.sidedSuccess(pLevel.isClientSide);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new InterdimenstionalTesseractTile();
    }
}
