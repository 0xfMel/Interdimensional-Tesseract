package ftm._0xfmel.itdmtrct.gameobjects.block.item;

import ftm._0xfmel.itdmtrct.gameobjects.item.IHasItemGroupCategory;
import ftm._0xfmel.itdmtrct.gameobjects.item.ItemGroupCategory;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BaseBlockItem extends BlockItem implements IHasItemGroupCategory {
    public final ItemGroupCategory itemGroupCategory;

    public BaseBlockItem(Block pBlock, Properties pProperties, ItemGroupCategory itemGroupCategory) {
        super(pBlock, pProperties);

        this.itemGroupCategory = itemGroupCategory;
    }

    public BaseBlockItem(Block pBlock, Properties pProperties) {
        this(pBlock, pProperties, ItemGroupCategory.DEFAULT);
    }

    @Override
    public ItemGroupCategory getItemGroupCategory() {
        return this.itemGroupCategory;
    }
}
