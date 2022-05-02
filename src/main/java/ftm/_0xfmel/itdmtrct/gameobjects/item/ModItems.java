package ftm._0xfmel.itdmtrct.gameobjects.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;

public class ModItems {
    public static final List<Item> ITEMS = new ArrayList<>();

    public static final BaseItem KEEP_LOADED_UPGRADE = new BaseItem("keep_loaded_upgrade",
            new Item.Properties().tab(ModItemGroups.TAB).stacksTo(16), ItemGroupCategory.UPGRADE);

    public static final BaseItem IRON_GEAR = new BaseItem("iron_gear", new Item.Properties().tab(ModItemGroups.TAB),
            ItemGroupCategory.CRAFTING_ITEM);
}
