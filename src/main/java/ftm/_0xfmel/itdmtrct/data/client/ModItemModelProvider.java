package ftm._0xfmel.itdmtrct.data.client;

import ftm._0xfmel.itdmtrct.gameobjects.item.ModItems;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ModGlobals.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.singleTexture(ModItems.KEEP_LOADED_UPGRADE);
        this.singleTexture(ModItems.IRON_GEAR);
    }

    private void singleTexture(Item item) {
        String regName = item.getRegistryName().getPath();
        this.singleTexture(regName, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + regName));
    }
}
