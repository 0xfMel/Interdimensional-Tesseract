package ftm._0xfmel.itdmtrct.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class StateUtil {
    public static final Direction[] UPDATE_SHAPE_ORDER = new Direction[] { Direction.WEST, Direction.EAST,
            Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP };

    public static void updateNeighbourShapesExceptFromFacing(BlockState state, IWorld pLevel, BlockPos pPos, int pFlag,
            Direction pSkipSide) {

        StateUtil.updateNeighbourShapesExceptFromFacing(state, pLevel, pPos, pFlag, pSkipSide, 512);
    }

    public static void updateNeighbourShapesExceptFromFacing(BlockState state, IWorld pLevel, BlockPos pPos, int pFlag,
            Direction pSkipSide, int pRecursionLeft) {

        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (Direction direction : StateUtil.UPDATE_SHAPE_ORDER) {
            if (direction != pSkipSide) {
                blockpos$mutable.setWithOffset(pPos, direction);
                BlockState blockstate = pLevel.getBlockState(blockpos$mutable);
                BlockState blockstate1 = blockstate.updateShape(direction.getOpposite(), state, pLevel,
                        blockpos$mutable, pPos);
                Block.updateOrDestroy(blockstate, blockstate1, pLevel, blockpos$mutable, pFlag, pRecursionLeft);
            }
        }
    }
}
