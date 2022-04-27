package ftm._0xfmel.itdmtrct.gameobjects.block;

import java.util.Optional;
import java.util.function.Function;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels;
import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels.TesseractChannel;
import ftm._0xfmel.itdmtrct.capabilities.TesseractChannelsCapability;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItemGroups;
import ftm._0xfmel.itdmtrct.tile.TesseractItemInterfaceTile;
import ftm._0xfmel.itdmtrct.utils.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.InventoryHelper;
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

public class TesseractInterfaceBlock extends BaseBlock {
    public static final EnumProperty<Direction> DIRECTION = EnumProperty.create("direction", Direction.class);
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    private final Function<Direction, TileEntity> tileFactory;

    public TesseractInterfaceBlock(String name, Function<Direction, TileEntity> tileFactory) {
        super(name, AbstractBlock.Properties.of(Material.HEAVY_METAL)
                .strength(8f, 9f)
                .sound(SoundType.METAL), ModItemGroups.TAB);

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
            if (te instanceof TesseractItemInterfaceTile) {
                ((TesseractItemInterfaceTile) te).setDirection(pState.getValue(DIRECTION));
            }

            // update any surrounding tesseracts to detect connecting to different tesseract
            pLevel.updateNeighborsAt(pPos, pState.getBlock());
        }
    }

    // todo - redstone comparator output

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, World pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.isClientSide && !pState.is(pNewState.getBlock())) {
            TileEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof TesseractItemInterfaceTile) {
                ((TesseractItemInterfaceTile) tileentity).getTesseractTile().ifPresent((tesseractTile) -> {
                    if (tesseractTile.getItemInterfaceCount() <= 1) {
                        boolean drop = tesseractTile.getChannelId() < 0;

                        if (!drop) {
                            Optional<ITesseractChannels> channels = pLevel.getServer().getLevel(World.OVERWORLD)
                                    .getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY).resolve();

                            if (channels.isPresent()) {
                                TesseractChannel channel = channels.get().getChannel(tesseractTile.getChannelId());
                                if (channel == null || channel.itemInterfaceCount <= 1) {
                                    drop = true;
                                }
                            } else {
                                drop = true;
                            }
                        }

                        if (drop) {
                            InventoryHelper.dropContents(pLevel, pPos, tesseractTile.getStacksForInterface());
                        }
                    }
                });
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
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
                if (te instanceof TesseractItemInterfaceTile) {
                    te.clearCache();

                    if (!world.isClientSide() && world instanceof IWorld) {

                        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

                        for (Direction direction : AbstractBlock.UPDATE_SHAPE_ORDER) {
                            if (direction == dirVal)
                                continue;
                            blockpos$mutable.setWithOffset(pos, direction);
                            BlockState blockstate = world.getBlockState(blockpos$mutable);
                            BlockState blockstate1 = blockstate.updateShape(direction.getOpposite(), state,
                                    (IWorld) world,
                                    blockpos$mutable, pos);
                            Block.updateOrDestroy(blockstate, blockstate1, (IWorld) world, blockpos$mutable, 2,
                                    512);
                        }
                    }
                }
            }
        }

        super.onNeighborChange(state, world, pos, neighbor);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(CONNECTED);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return this.tileFactory.apply(state.getValue(DIRECTION));
    }
}
