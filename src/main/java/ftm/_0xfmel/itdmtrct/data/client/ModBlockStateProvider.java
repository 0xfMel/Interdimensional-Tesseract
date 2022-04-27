package ftm._0xfmel.itdmtrct.data.client;

import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ModGlobals.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile interdimensionalTesseract = this.cubeAll(ModBlocks.INTERDIMENSIONAL_TESSERACT);
        this.simpleBlock(ModBlocks.INTERDIMENSIONAL_TESSERACT, interdimensionalTesseract);
        this.simpleBlockItem(ModBlocks.INTERDIMENSIONAL_TESSERACT, interdimensionalTesseract);

        ModelFile tesseractFrame = this.cubeAll(ModBlocks.TESSERACT_FRAME);
        this.simpleBlock(ModBlocks.TESSERACT_FRAME, tesseractFrame);
        this.simpleBlockItem(ModBlocks.TESSERACT_FRAME, tesseractFrame);
    }
}
