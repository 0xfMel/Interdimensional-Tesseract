package ftm._0xfmel.itdmtrct.data;

import ftm._0xfmel.itdmtrct.data.client.ModBlockStateProvider;
import ftm._0xfmel.itdmtrct.data.client.ModItemModelProvider;
import ftm._0xfmel.itdmtrct.data.tags.ModBlockTagsProvider;
import ftm._0xfmel.itdmtrct.data.tags.ModItemTagsProvider;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent e) {
        DataGenerator gen = e.getGenerator();
        ExistingFileHelper exFileHelper = e.getExistingFileHelper();

        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(gen, exFileHelper);

        gen.addProvider(new ModBlockStateProvider(gen, exFileHelper));
        gen.addProvider(new ModItemModelProvider(gen, exFileHelper));
        gen.addProvider(new ModRecipeProvider(gen));
        gen.addProvider(blockTagsProvider);
        gen.addProvider(new ModItemTagsProvider(gen, blockTagsProvider, exFileHelper));
    }
}
