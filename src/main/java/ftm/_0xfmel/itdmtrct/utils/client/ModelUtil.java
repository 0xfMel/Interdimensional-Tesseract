package ftm._0xfmel.itdmtrct.utils.client;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelUtil {
    public static final Map<Direction, ModelRotation> ROTATION_BY_DIRECTION = Util
            .make(Maps.newEnumMap(Direction.class), (p_203421_0_) -> {
                p_203421_0_.put(Direction.NORTH, ModelRotation.X0_Y0);
                p_203421_0_.put(Direction.EAST, ModelRotation.X0_Y90);
                p_203421_0_.put(Direction.SOUTH, ModelRotation.X0_Y180);
                p_203421_0_.put(Direction.WEST, ModelRotation.X0_Y270);
                p_203421_0_.put(Direction.UP, ModelRotation.X270_Y0);
                p_203421_0_.put(Direction.DOWN, ModelRotation.X90_Y0);
            });
}
