package ftm._0xfmel.itdmtrct.utils;

import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class ConcurrencyUtil {
    public static void runNextTick(Runnable runnable, boolean isClientSide) {
        ThreadTaskExecutor<Runnable> executor = LogicalSidedProvider.WORKQUEUE
                .get(isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER);
        executor.tell(
                new TickDelayedTask(0, runnable));
    }
}
