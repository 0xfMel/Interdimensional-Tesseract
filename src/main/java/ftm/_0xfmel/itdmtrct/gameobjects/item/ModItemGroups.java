package ftm._0xfmel.itdmtrct.gameobjects.item;

import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ModItemGroups {
    public static final ItemGroup TAB = new ItemGroup(ModGlobals.MOD_ID + "_tab") {
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.INTERDIMENSIONAL_TESSERACT);
        };

        public void fillItemList(NonNullList<ItemStack> pItems) {
            super.fillItemList(pItems);
            pItems.sort((a, b) -> {
                Item aItem = a.getItem();
                Item bItem = b.getItem();
                if (aItem instanceof IHasItemGroupCategory && bItem instanceof IHasItemGroupCategory) {
                    return ((IHasItemGroupCategory) aItem).getItemGroupCategory().ordinal()
                            - ((IHasItemGroupCategory) bItem).getItemGroupCategory().ordinal();
                }

                return 0;
            });
        };
    };
}
