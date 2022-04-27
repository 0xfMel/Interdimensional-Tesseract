package ftm._0xfmel.itdmtrct.gameobjects.block;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ftm._0xfmel.itdmtrct.containers.TesseractContainer;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItemGroups;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItems;
import ftm._0xfmel.itdmtrct.tile.InterdimensionalTesseractTile;
import ftm._0xfmel.itdmtrct.utils.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class InterdimensionalTesseractBlock extends BaseBlock {
    public static final Map<Direction, BooleanProperty> DIRECTION_PROPERTY_MAP = Stream.of(Direction.values())
            .collect(Collectors.toMap((d) -> d, (d) -> BooleanProperty.create(d.getName())));

    public static final BooleanProperty NORTH = DIRECTION_PROPERTY_MAP.get(Direction.NORTH);
    public static final BooleanProperty EAST = DIRECTION_PROPERTY_MAP.get(Direction.EAST);
    public static final BooleanProperty SOUTH = DIRECTION_PROPERTY_MAP.get(Direction.SOUTH);
    public static final BooleanProperty WEST = DIRECTION_PROPERTY_MAP.get(Direction.WEST);
    public static final BooleanProperty UP = DIRECTION_PROPERTY_MAP.get(Direction.UP);
    public static final BooleanProperty DOWN = DIRECTION_PROPERTY_MAP.get(Direction.DOWN);

    public InterdimensionalTesseractBlock() {
        super("interdimensional_tesseract", AbstractBlock.Properties.of(Material.HEAVY_METAL)
                .strength(8f, 9f)
                .sound(SoundType.METAL)
                .lightLevel((blockState) -> 8)
                .noOcclusion(),
                ModItemGroups.TAB);

        BlockState defaultState = this.stateDefinition.any();
        for (BooleanProperty dirProperty : DIRECTION_PROPERTY_MAP.values()) {
            defaultState = defaultState.setValue(dirProperty, false);
        }

        this.registerDefaultState(defaultState);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DIRECTION_PROPERTY_MAP.values().toArray(new BooleanProperty[0]));
    }

    @Override
    public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand,
            BlockRayTraceResult pHit) {

        if (pPlayer.getItemInHand(pHand).getItem() == ModItems.KEEP_LOADED_UPGRADE) {
            TileEntity te = pLevel.getBlockEntity(pPos);
            if (te instanceof InterdimensionalTesseractTile) {
                InterdimensionalTesseractTile tile = (InterdimensionalTesseractTile) te;
                if (!tile.getHasKeepLoadedUpgrade()) {
                    if (pLevel.isClientSide) {
                        return ActionResultType.SUCCESS;
                    } else {
                        tile.setHasKeepLoadedUpgrade(true);
                        if (!pPlayer.isCreative()) {
                            pPlayer.getItemInHand(pHand).shrink(1);
                        }
                        return ActionResultType.CONSUME;
                    }
                }
            }
        }

        if (!pLevel.isClientSide) {
            TileEntity te = pLevel.getBlockEntity(pPos);

            if (te instanceof InterdimensionalTesseractTile && pPlayer instanceof ServerPlayerEntity) {
                TesseractContainer.openFor((ServerPlayerEntity) pPlayer, (InterdimensionalTesseractTile) te);
            }
        }

        return ActionResultType.sidedSuccess(pLevel.isClientSide);
    }

    @Override
    public void onPlace(BlockState pState, World pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pState.is(pOldState.getBlock())) {
            TileEntity te = pLevel.getBlockEntity(pPos);

            if (te instanceof InterdimensionalTesseractTile) {
                InterdimensionalTesseractTile tile = (InterdimensionalTesseractTile) te;

                int itemInterfaces = Stream.of(Direction.values()).reduce(0, (count, dir) -> count
                        + (pLevel.getBlockState(pPos.relative(dir)).is(ModBlocks.TESSERACT_ITEM_INTERFACE) ? 1 : 0),
                        (a, b) -> a + b);

                tile.onLoadPlace();
                tile.setItemInterfaceCount(itemInterfaces);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState pState, World pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos,
            boolean pIsMoving) {

        TileEntity te = pLevel.getBlockEntity(pPos);

        if (te instanceof InterdimensionalTesseractTile) {
            InterdimensionalTesseractTile tile = (InterdimensionalTesseractTile) te;

            BlockState fromState = pLevel.getBlockState(pFromPos);
            Direction facing = Utils.relativeTo(pFromPos, pPos);

            if (pState.getValue(DIRECTION_PROPERTY_MAP.get(facing))) {
                if (pBlock.is(ModBlocks.TESSERACT_ITEM_INTERFACE)
                        && !fromState.is(ModBlocks.TESSERACT_ITEM_INTERFACE)) {

                    tile.removeItemInterfaceCount();
                }
            } else if (fromState.is(ModBlocks.TESSERACT_ITEM_INTERFACE)
                    && fromState.getValue(TesseractInterfaceBlock.DIRECTION) == facing.getOpposite()) {

                tile.addItemInterfaceCount();
            }
        }

        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, IWorld pLevel,
            BlockPos pCurrentPos, BlockPos pFacingPos) {

        return pState.setValue(DIRECTION_PROPERTY_MAP.get(pFacing),
                pFacingState.getBlock() instanceof TesseractInterfaceBlock
                        && pFacingState.getValue(TesseractInterfaceBlock.CONNECTED)
                        && pFacingState.getValue(TesseractInterfaceBlock.DIRECTION) == pFacing.getOpposite());
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        BlockState state = this.defaultBlockState();
        BlockPos pos = pContext.getClickedPos();
        World world = pContext.getLevel();

        for (Direction dir : Direction.values()) {
            BlockPos dirPos = pos.relative(dir);
            BlockState dirState = world.getBlockState(dirPos);
            if (dirState.getBlock() instanceof TesseractInterfaceBlock) {
                Direction dirOpposite = dir.getOpposite();
                BlockState dirStatePending = ((TesseractInterfaceBlock) world.getBlockState(dirPos).getBlock())
                        .getPendingUpdateState(dirState, dirOpposite,
                                ModBlocks.INTERDIMENSIONAL_TESSERACT, world, dirPos);
                state = state.setValue(DIRECTION_PROPERTY_MAP.get(dir), dirStatePending
                        .getValue(TesseractInterfaceBlock.DIRECTION) == dirOpposite);
            }
        }

        return state;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new InterdimensionalTesseractTile();
    }
}
