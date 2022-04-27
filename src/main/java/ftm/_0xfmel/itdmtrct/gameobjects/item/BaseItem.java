package ftm._0xfmel.itdmtrct.gameobjects.item;

import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.item.Item;

public class BaseItem extends Item {
    public BaseItem(String name, Properties pProperties) {
        super(pProperties);

        this.setRegistryName(ModGlobals.MOD_ID, name);
        ModItems.ITEMS.add(this);
    }
}
