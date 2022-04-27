package ftm._0xfmel.itdmtrct.data.tags;

import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
        super(pGenerator, ModGlobals.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
    }
}
