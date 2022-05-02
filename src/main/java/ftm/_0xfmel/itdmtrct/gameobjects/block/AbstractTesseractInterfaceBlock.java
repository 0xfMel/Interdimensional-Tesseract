package ftm._0xfmel.itdmtrct.gameobjects.block;

import java.util.function.Function;

import ftm._0xfmel.itdmtrct.gameobjects.item.ItemGroupCategory;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItemGroups;
import ftm._0xfmel.itdmtrct.tile.AbstractTesseractInterfaceTile;
import ftm._0xfmel.itdmtrct.utils.StateUtil;
import ftm._0xfmel.itdmtrct.utils.Utils;
import ftm._0xfmel.itdmtrct.utils.interfaces.ITesseractInterfaceTile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class AbstractTesseractInterfaceBlock extends BaseBlock implements IWrenchable {
    public static final EnumProperty<Direction> DIRECTION = EnumProperty.create("direction", Direction.class);
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    private final Function<Direction, TileEntity> tileFactory;

    public AbstractTesseractInterfaceBlock(String name, Function<Direction, TileEntity> tileFactory) {
        super(name, AbstractBlock.Properties.of(Material.HEAVY_METAL)
                .strength(8f, 9f)
                .sound(SoundType.METAL), ModItemGroups.TAB, ItemGroupCategory.INTERFACE);

        this.tileFactory = tileFactory;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DIRECTION, Direction.NORTH)
                .setValue(CONNECTED, false));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DIRECTION, CONNECTED);
    }

    @Override
    public void onPlace(BlockState pState, World pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (pOldState.is(pState.getBlock())) {
            TileEntity te = pLevel.getBlockEntity(pPos);
            if (te instanceof ITesseractInterfaceTile) {
                ((ITesseractInterfaceTile) te).setDirection(pState.getValue(DIRECTION));
            }

            // update any surrounding tesseracts to detect connecting to different tesseract
            pLevel.updateNeighborsAt(pPos, pState.getBlock());
        }
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, IWorld pLevel,
            BlockPos pCurrentPos, BlockPos pFacingPos) {

        return this.getPendingUpdateState(pState, pFacing, pFacingState.getBlock(), pLevel, pCurrentPos);
    }

    public BlockState getPendingUpdateState(BlockState state, Direction facing, Block facingBlock, IWorld world,
            BlockPos pos) {
        if (state.getValue(CONNECTED)) {
            if (world.getBlockState(pos.relative(state.getValue(DIRECTION)))
                    .is(ModBlocks.INTERDIMENSIONAL_TESSERACT)) {
                return state;
            }

            return this.findBlockState(world, pos, facing, facingBlock);
        }

        if (facingBlock.is(ModBlocks.INTERDIMENSIONAL_TESSERACT)) {
            return this.defaultBlockState().setValue(CONNECTED, true).setValue(DIRECTION, facing);
        }

        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        return this.findBlockState(pContext.getLevel(), pContext.getClickedPos(), null, null);
    }

    private BlockState findBlockState(IWorld world, BlockPos pos, Direction facing, Block facingBlock) {
        for (Direction dir : Direction.values()) {
            if ((dir == facing ? facingBlock : world.getBlockState(pos.relative(dir)).getBlock())
                    .is(ModBlocks.INTERDIMENSIONAL_TESSERACT)) {
                return this.defaultBlockState().setValue(CONNECTED, true).setValue(DIRECTION, dir);
            }
        }
        return this.defaultBlockState().setValue(CONNECTED, false);
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (state.getValue(CONNECTED)) {
            Direction dirVal = state.getValue(DIRECTION);
            if (dirVal == Utils.relativeTo(neighbor, pos)
                    && world.getBlockState(neighbor).is(ModBlocks.INTERDIMENSIONAL_TESSERACT)) {

                TileEntity te = world.getBlockEntity(pos);
                if (te instanceof AbstractTesseractInterfaceTile) {
                    te.clearCache();

                    if (!world.isClientSide() && world instanceof World) {
                        World theWorld = (World) world;
                        theWorld.updateNeighborsAtExceptFromFacing(pos, state.getBlock(), dirVal);
                        StateUtil.updateNeighbourShapesExceptFromFacing(state, theWorld, pos, 0, dirVal);
                    }
                }
            }
        }

        super.onNeighborChange(state, world, pos, neighbor);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public abstract int getAnalogOutputSignal(BlockState pBlockState, World pLevel, BlockPos pPos);

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(CONNECTED);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return this.tileFactory.apply(state.getValue(DIRECTION));
    }
}
