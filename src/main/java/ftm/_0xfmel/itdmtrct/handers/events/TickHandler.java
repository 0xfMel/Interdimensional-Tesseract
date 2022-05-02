package ftm._0xfmel.itdmtrct.handers.events;

import ftm._0xfmel.itdmtrct.capabilities.TesseractChannelsCapability;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber
public class TickHandler {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != Phase.END)
            return;

        World world = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);

        world.getCapability(TesseractChannelsCapability.TESSERACT_CHANNELS_CAPABILITY)
                .ifPresent(
                        (channels) -> channels.getChannels().forEach((channel) -> channel.tick(world.getGameTime())));
    }
}
