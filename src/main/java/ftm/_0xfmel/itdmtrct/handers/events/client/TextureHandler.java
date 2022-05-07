package ftm._0xfmel.itdmtrct.handers.events.client;

import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Bus.MOD, value = { Dist.CLIENT })
public class TextureHandler {
    private static final ResourceLocation POWER_TEXTURE_LOCATION = new ResourceLocation(ModGlobals.MOD_ID,
            "block/" + ModBlocks.INTERDIMENSIONAL_TESSERACT.getRegistryName().getPath() + "_power");

    @SubscribeEvent
    public static void onTextureHandler(TextureStitchEvent.Pre e) {
        if (e.getMap().location() == PlayerContainer.BLOCK_ATLAS) {
            e.addSprite(POWER_TEXTURE_LOCATION);
        }
    }
}
