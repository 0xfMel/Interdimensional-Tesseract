package ftm._0xfmel.itdmtrct.utils;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class Utils {
    public static <T, U> void doNothing(T a, U b) {
    }

    public static Direction relativeTo(BlockPos posOf, BlockPos relTo) {
        BlockPos normal = posOf.subtract(relTo);
        return Direction.fromNormal(normal.getX(), normal.getY(), normal.getZ());
    }
}
