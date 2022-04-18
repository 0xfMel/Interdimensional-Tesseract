package ftm._0xfmel.itdmtrct.handers.events;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AttachCapabilityHander {
    @SubscribeEvent
    public static void onAttachCapabilityWorld(AttachCapabilitiesEvent<World> e) {
        World world = e.getObject();
        if (world.isClientSide || world.dimension() == World.OVERWORLD) {
            e.addCapability(new ResourceLocation(ModGlobals.MOD_ID, "tesseract_channels"),
                    new ITesseractChannels.Provider(world.isClientSide));
        }
    }
}
