package ftm._0xfmel.itdmtrct.gameobjects.block;

import ftm._0xfmel.itdmtrct.gameobjects.block.item.BaseBlockItem;
import ftm._0xfmel.itdmtrct.gameobjects.item.ItemGroupCategory;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItems;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class BaseBlock extends Block {
    public BaseBlock(String name, Properties p_i48440_1_, ItemGroup tab, ItemGroupCategory itemGroupCategory) {
        super(p_i48440_1_);

        this.setRegistryName(ModGlobals.MOD_ID, name);
        ModBlocks.BLOCKS.add(this);

        BlockItem blockItem = new BaseBlockItem(this, new Item.Properties().tab(tab), itemGroupCategory);
        blockItem.setRegistryName(this.getRegistryName());
        ModItems.ITEMS.add(blockItem);
    }

    public BaseBlock(String name, Properties p_i48440_1_, ItemGroup tab) {
        this(name, p_i48440_1_, tab, ItemGroupCategory.DEFAULT);
    }
}
