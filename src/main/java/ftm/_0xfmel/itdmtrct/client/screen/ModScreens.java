package ftm._0xfmel.itdmtrct.client.screen;

import ftm._0xfmel.itdmtrct.containers.ModContainerTypes;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModScreens {
    public static void registerScreens() {
        ScreenManager.register(ModContainerTypes.TESSERACT, TesseractScreen::new);
    }
}
