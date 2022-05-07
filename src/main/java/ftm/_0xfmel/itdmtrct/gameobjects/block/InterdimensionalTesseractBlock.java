package ftm._0xfmel.itdmtrct.gameobjects.block;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import ftm._0xfmel.itdmtrct.containers.TesseractContainer;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItemGroups;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItems;
import ftm._0xfmel.itdmtrct.tile.InterdimensionalTesseractTile;
import ftm._0xfmel.itdmtrct.utils.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class InterdimensionalTesseractBlock extends BaseBlock implements IWrenchable {
    public static final BooleanProperty NORTH = SixWayBlock.PROPERTY_BY_DIRECTION.get(Direction.NORTH);
    public static final BooleanProperty EAST = SixWayBlock.PROPERTY_BY_DIRECTION.get(Direction.EAST);
    public static final BooleanProperty SOUTH = SixWayBlock.PROPERTY_BY_DIRECTION.get(Direction.SOUTH);
    public static final BooleanProperty WEST = SixWayBlock.PROPERTY_BY_DIRECTION.get(Direction.WEST);
    public static final BooleanProperty UP = SixWayBlock.PROPERTY_BY_DIRECTION.get(Direction.UP);
    public static final BooleanProperty DOWN = SixWayBlock.PROPERTY_BY_DIRECTION.get(Direction.DOWN);

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(13, 0, 0, 16, 3, 3),
            Block.box(0, 0, 0, 3, 3, 3),
            Block.box(13, 0, 13, 16, 3, 16),
            Block.box(0, 0, 13, 3, 3, 16),
            Block.box(13, 13, 0, 16, 16, 3),
            Block.box(0, 13, 0, 3, 16, 3),
            Block.box(13, 13, 13, 16, 16, 16),
            Block.box(0, 13, 13, 3, 16, 16),
            Block.box(2, 2, 2, 14, 14, 14),
            Block.box(15, 15, 3, 16, 16, 13),
            Block.box(3, 15, 0, 13, 16, 1),
            Block.box(3, 15, 15, 13, 16, 16),
            Block.box(0, 15, 3, 1, 16, 13),
            Block.box(15, 0, 3, 16, 1, 13),
            Block.box(3, 0, 0, 13, 1, 1),
            Block.box(3, 0, 15, 13, 1, 16),
            Block.box(0, 0, 3, 1, 1, 13),
            Block.box(15, 3, 15, 16, 13, 16),
            Block.box(0, 3, 0, 1, 13, 1),
            Block.box(15, 3, 0, 16, 13, 1),
            Block.box(0, 3, 15, 1, 13, 16))
            .reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get().optimize();

    private static final VoxelShape CONNECTION_NORTH = Stream.of(
            Block.box(9, 11, 0, 11, 12, 2),
            Block.box(6, 6, 0, 10, 10, 2),
            Block.box(4, 4, 0, 5, 7, 2),
            Block.box(4, 9, 0, 5, 12, 2),
            Block.box(11, 4, 0, 12, 7, 2),
            Block.box(11, 9, 0, 12, 12, 2),
            Block.box(5, 4, 0, 7, 5, 2),
            Block.box(9, 4, 0, 11, 5, 2),
            Block.box(5, 11, 0, 7, 12, 2))
            .reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get().optimize();

    private static final VoxelShape CONNECTION_EAST = Stream.of(
            Block.box(14, 11, 9, 16, 12, 11),
            Block.box(14, 6, 6, 16, 10, 10),
            Block.box(14, 4, 4, 16, 7, 5),
            Block.box(14, 9, 4, 16, 12, 5),
            Block.box(14, 4, 11, 16, 7, 12),
            Block.box(14, 9, 11, 16, 12, 12),
            Block.box(14, 4, 5, 16, 5, 7),
            Block.box(14, 4, 9, 16, 5, 11),
            Block.box(14, 11, 5, 16, 12, 7))
            .reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get().optimize();

    private static final VoxelShape CONNECTION_SOUTH = Stream.of(
            Block.box(5, 11, 14, 7, 12, 16),
            Block.box(6, 6, 14, 10, 10, 16),
            Block.box(11, 4, 14, 12, 7, 16),
            Block.box(11, 9, 14, 12, 12, 16),
            Block.box(4, 4, 14, 5, 7, 16),
            Block.box(4, 9, 14, 5, 12, 16),
            Block.box(9, 4, 14, 11, 5, 16),
            Block.box(5, 4, 14, 7, 5, 16),
            Block.box(9, 11, 14, 11, 12, 16))
            .reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get().optimize();

    private static final VoxelShape CONNECTION_WEST = Stream.of(
            Block.box(0, 11, 5, 2, 12, 7),
            Block.box(0, 6, 6, 2, 10, 10),
            Block.box(0, 4, 11, 2, 7, 12),
            Block.box(0, 9, 11, 2, 12, 12),
            Block.box(0, 4, 4, 2, 7, 5),
            Block.box(0, 9, 4, 2, 12, 5),
            Block.box(0, 4, 9, 2, 5, 11),
            Block.box(0, 4, 5, 2, 5, 7),
            Block.box(0, 11, 9, 2, 12, 11))
            .reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get().optimize();

    private static final VoxelShape CONNECTION_UP = Stream.of(
            Block.box(9, 14, 11, 11, 16, 12),
            Block.box(6, 14, 6, 10, 16, 10),
            Block.box(4, 14, 4, 5, 16, 7),
            Block.box(4, 14, 9, 5, 16, 12),
            Block.box(11, 14, 4, 12, 16, 7),
            Block.box(11, 14, 9, 12, 16, 12),
            Block.box(5, 14, 4, 7, 16, 5),
            Block.box(9, 14, 4, 11, 16, 5),
            Block.box(5, 14, 11, 7, 16, 12))
            .reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get().optimize();

    private static final VoxelShape CONNECTION_DOWN = Stream.of(
            Block.box(9, 0, 4, 11, 2, 5),
            Block.box(6, 0, 6, 10, 2, 10),
            Block.box(4, 0, 9, 5, 2, 12),
            Block.box(4, 0, 4, 5, 2, 7),
            Block.box(11, 0, 9, 12, 2, 12),
            Block.box(11, 0, 4, 12, 2, 7),
            Block.box(5, 0, 11, 7, 2, 12),
            Block.box(9, 0, 11, 11, 2, 12),
            Block.box(5, 0, 4, 7, 2, 5))
            .reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get().optimize();

    protected final VoxelShape[] shapeByIndex;

    public InterdimensionalTesseractBlock() {
        super("interdimensional_tesseract", AbstractBlock.Properties.of(Material.HEAVY_METAL)
                .strength(8f, 9f)
                .sound(SoundType.METAL)
                .lightLevel((blockState) -> 8)
                .harvestTool(ToolType.PICKAXE)
                .requiresCorrectToolForDrops().harvestLevel(0),
                ModItemGroups.TAB);

        BlockState defaultState = this.stateDefinition.any();
        for (BooleanProperty dirProperty : SixWayBlock.PROPERTY_BY_DIRECTION.values()) {
            defaultState = defaultState.setValue(dirProperty, false);
        }

        this.registerDefaultState(defaultState);

        this.shapeByIndex = this.makeShapes();
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(SixWayBlock.PROPERTY_BY_DIRECTION.values().toArray(new BooleanProperty[0]));
    }

    private VoxelShape[] makeShapes() {
        VoxelShape voxelshape = SHAPE;

        VoxelShape[] avoxelshape = {
                CONNECTION_DOWN,
                CONNECTION_UP,
                CONNECTION_NORTH,
                CONNECTION_SOUTH,
                CONNECTION_WEST,
                CONNECTION_EAST
        };

        VoxelShape[] avoxelshape1 = new VoxelShape[64];

        for (int k = 0; k < 64; ++k) {
            VoxelShape voxelshape1 = voxelshape;

            for (int j = 0; j < Direction.values().length; ++j) {
                if ((k & 1 << j) != 0) {
                    voxelshape1 = VoxelShapes.or(voxelshape1, avoxelshape[j]);
                }
            }

            avoxelshape1[k] = voxelshape1.optimize();
        }

        return avoxelshape1;
    }

    @Override
    public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
        VoxelShape baseShape = this.shapeByIndex[this.getAABBIndex(pState)];

        TileEntity te = pLevel.getBlockEntity(pPos);
        if (te instanceof InterdimensionalTesseractTile) {
            return ((InterdimensionalTesseractTile) te).getDynamicShape(baseShape);
        }

        return baseShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, IBlockReader pLevel, BlockPos pPos,
            ISelectionContext pContext) {

        return SHAPE;
    }

    protected int getAABBIndex(BlockState pState) {
        int i = 0;

        for (int j = 0; j < Direction.values().length; ++j) {
            if (pState.getValue(SixWayBlock.PROPERTY_BY_DIRECTION.get(Direction.values()[j]))) {
                i |= 1 << j;
            }
        }

        return i;
    }

    @Override
    public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand,
            BlockRayTraceResult pHit) {

        if (pPlayer.getItemInHand(pHand).getItem() == ModItems.KEEP_LOADED_UPGRADE) {
            TileEntity te = pLevel.getBlockEntity(pPos);
            if (te instanceof InterdimensionalTesseractTile) {
                InterdimensionalTesseractTile tile = (InterdimensionalTesseractTile) te;
                if (!tile.getHasKeepLoadedUpgrade()) {
                    pLevel.playSound(pPlayer, pPos, SoundEvents.ANVIL_USE, SoundCategory.BLOCKS,
                            1, pLevel.random.nextFloat() * 0.1F + 0.9F);

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

                Map<AbstractTesseractInterfaceBlock, Integer> interfaceBlocks = new HashMap<>();
                for (Direction dir : Direction.values()) {
                    Block block = pLevel.getBlockState(pPos.relative(dir)).getBlock();
                    if (block instanceof AbstractTesseractInterfaceBlock) {
                        AbstractTesseractInterfaceBlock interfaceBlock = (AbstractTesseractInterfaceBlock) block;
                        interfaceBlocks.put(interfaceBlock, interfaceBlocks.getOrDefault(interfaceBlock, 0) + 1);
                    }
                }

                tile.onLoadPlace();
                interfaceBlocks.entrySet().forEach((entry) -> tile.setInterfaceCount(entry.getKey(), entry.getValue()));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, World pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.isClientSide && !pState.is(pNewState.getBlock())) {
            TileEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof InterdimensionalTesseractTile) {
                InterdimensionalTesseractTile tile = (InterdimensionalTesseractTile) tileentity;
                if (tile.getChannelId() >= 0 && tile.getOwnChannel()) {
                    InventoryHelper.dropContents(pLevel, pPos, tile.getStacksForInterface());
                }

                if (tile.getHasKeepLoadedUpgrade()) {
                    ItemEntity itementity = new ItemEntity(pLevel, pPos.getX() + 0.5d, pPos.getY() + 0.5d,
                            pPos.getZ() + 0.5d, new ItemStack(ModItems.KEEP_LOADED_UPGRADE));

                    pLevel.addFreshEntity(itementity);
                }
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
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

            if (pState.getValue(SixWayBlock.PROPERTY_BY_DIRECTION.get(facing))) {
                if (!fromState.is(pBlock) && pBlock instanceof AbstractTesseractInterfaceBlock) {
                    tile.removeInterfaceCount((AbstractTesseractInterfaceBlock) pBlock);
                }
            } else if (fromState.getBlock() instanceof AbstractTesseractInterfaceBlock
                    && fromState.getValue(AbstractTesseractInterfaceBlock.DIRECTION) == facing.getOpposite()) {

                tile.addInterfaceCount((AbstractTesseractInterfaceBlock) fromState.getBlock());
            }
        }

        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, IWorld pLevel,
            BlockPos pCurrentPos, BlockPos pFacingPos) {

        return pState.setValue(SixWayBlock.PROPERTY_BY_DIRECTION.get(pFacing),
                pFacingState.getBlock() instanceof AbstractTesseractInterfaceBlock
                        && pFacingState.getValue(AbstractTesseractInterfaceBlock.CONNECTED)
                        && pFacingState.getValue(AbstractTesseractInterfaceBlock.DIRECTION) == pFacing.getOpposite());
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        BlockState state = this.defaultBlockState();
        BlockPos pos = pContext.getClickedPos();
        World world = pContext.getLevel();

        for (Direction dir : Direction.values()) {
            BlockPos dirPos = pos.relative(dir);
            BlockState dirState = world.getBlockState(dirPos);
            if (dirState.getBlock() instanceof AbstractTesseractInterfaceBlock) {
                Direction dirOpposite = dir.getOpposite();
                BlockState dirStatePending = ((AbstractTesseractInterfaceBlock) world.getBlockState(dirPos).getBlock())
                        .getPendingUpdateState(dirState, dirOpposite,
                                ModBlocks.INTERDIMENSIONAL_TESSERACT, world, dirPos);
                state = state.setValue(SixWayBlock.PROPERTY_BY_DIRECTION.get(dir), dirStatePending
                        .getValue(AbstractTesseractInterfaceBlock.DIRECTION) == dirOpposite);
            }
        }

        return state;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        if (world instanceof ClientWorld) {
            VoxelShapes.block()
                    .forAllBoxes((p_228348_3_, p_228348_5_, p_228348_7_, p_228348_9_,
                            p_228348_11_, p_228348_13_) -> {
                        double d1 = Math.min(1.0D, p_228348_9_ - p_228348_3_);
                        double d2 = Math.min(1.0D, p_228348_11_ - p_228348_5_);
                        double d3 = Math.min(1.0D, p_228348_13_ - p_228348_7_);
                        int i = Math.max(2, MathHelper.ceil(d1 / 0.25D));
                        int j = Math.max(2, MathHelper.ceil(d2 / 0.25D));
                        int k = Math.max(2, MathHelper.ceil(d3 / 0.25D));

                        for (int l = 0; l < i; ++l) {
                            for (int i1 = 0; i1 < j; ++i1) {
                                for (int j1 = 0; j1 < k; ++j1) {
                                    double d4 = ((double) l + 0.5D) / (double) i;
                                    double d5 = ((double) i1 + 0.5D) / (double) j;
                                    double d6 = ((double) j1 + 0.5D) / (double) k;
                                    double d7 = d4 * d1 + p_228348_3_;
                                    double d8 = d5 * d2 + p_228348_5_;
                                    double d9 = d6 * d3 + p_228348_7_;
                                    manager.add((new DiggingParticle((ClientWorld) world, (double) pos.getX() +
                                            d7,
                                            (double) pos.getY() + d8,
                                            (double) pos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, state))
                                            .init(pos));
                                }
                            }
                        }

                    });

            return true;
        }

        return super.addDestroyEffects(state, world, pos, manager);
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
