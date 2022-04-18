package ftm._0xfmel.itdmtrct.gameobjects.item;

import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroups {
    public static final ItemGroup TAB = new ItemGroup(ModGlobals.MOD_ID + "_tab") {
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.INTERDIMENSIONAL_TESSERACT);
        };
    };
}
