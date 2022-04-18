package ftm._0xfmel.itdmtrct;

import ftm._0xfmel.itdmtrct.capabilities.ModCapabilities;
import ftm._0xfmel.itdmtrct.client.screen.ModScreens;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import ftm._0xfmel.itdmtrct.handers.ModPacketHander;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModGlobals.MOD_ID)
public class ItdmTrct {
    public ItdmTrct() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ModPacketHander.registerNetworkPackets();
        ModCapabilities.registerCapabilities();
    }

    private void setupClient(final FMLClientSetupEvent event) {
        ModScreens.registerScreens();
    }
}
