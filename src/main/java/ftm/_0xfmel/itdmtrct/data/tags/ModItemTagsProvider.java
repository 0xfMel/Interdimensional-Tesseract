package ftm._0xfmel.itdmtrct.data.tags;

import ftm._0xfmel.itdmtrct.gameobjects.item.ModItems;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider,
            ExistingFileHelper existingFileHelper) {
        super(pGenerator, pBlockTagsProvider, ModGlobals.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(ItemTags.bind("forge:gears/iron")).add(ModItems.IRON_GEAR);
    }
}
