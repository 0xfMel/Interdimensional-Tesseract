package ftm._0xfmel.itdmtrct.gameobjects.item;

import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.item.Item;

public class BaseItem extends Item implements IHasItemGroupCategory {
    public final ItemGroupCategory itemGroupCategory;

    public BaseItem(String name, Properties pProperties, ItemGroupCategory itemGroupCategory) {
        super(pProperties);

        this.itemGroupCategory = itemGroupCategory;
        this.setRegistryName(ModGlobals.MOD_ID, name);
        ModItems.ITEMS.add(this);
    }

    public BaseItem(String name, Properties pProperties) {
        this(name, pProperties, ItemGroupCategory.DEFAULT);
    }

    @Override
    public ItemGroupCategory getItemGroupCategory() {
        return this.itemGroupCategory;
    }
}
