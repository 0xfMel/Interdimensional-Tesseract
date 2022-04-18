package ftm._0xfmel.itdmtrct.gameobjects.block;

import ftm._0xfmel.itdmtrct.gameobjects.item.ModItems;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class BaseBlock extends Block {
    public BaseBlock(String name, Properties p_i48440_1_, ItemGroup tab) {
        super(p_i48440_1_);

        this.setRegistryName(ModGlobals.MOD_ID, name);
        ModBlocks.BLOCKS.add(this);

        BlockItem blockItem = new BlockItem(this, new Item.Properties().tab(tab));
        blockItem.setRegistryName(this.getRegistryName());
        ModItems.ITEMS.add(blockItem);
    }
}
