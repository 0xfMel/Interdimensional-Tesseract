package ftm._0xfmel.itdmtrct.data.client;

import ftm._0xfmel.itdmtrct.gameobjects.block.AbstractTesseractInterfaceBlock;
import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.block.Block;
import net.minecraft.block.SixWayBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ModelTextures;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ConfiguredModel.Builder;
import net.minecraftforge.client.model.generators.ModelBuilder.FaceRotation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    private static final String TESSERACT_POWER_NAME = ModBlocks.INTERDIMENSIONAL_TESSERACT.getRegistryName().getPath()
            + "_power";

    private final ResourceLocation tesseractConnectedLocation = this.modLoc("block/tesseract_interface_connected");

    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ModGlobals.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.models().getBuilder(ModBlockStateProvider.TESSERACT_POWER_NAME)
                .texture("0", this.modLoc("block/" + ModBlockStateProvider.TESSERACT_POWER_NAME))
                .texture("particle", this.modLoc("block/" + ModBlockStateProvider.TESSERACT_POWER_NAME))
                .element().from(3, 3, 0).to(13, 13, 2)
                .face(Direction.NORTH).texture("#0").uvs(0, 0, 10, 10).end()
                .face(Direction.EAST).texture("#0").uvs(10, 0, 12, 10).rotation(FaceRotation.UPSIDE_DOWN).end()
                .face(Direction.WEST).texture("#0").uvs(10, 0, 12, 10).end()
                .face(Direction.UP).texture("#0").uvs(12, 10, 10, 0).rotation(FaceRotation.COUNTERCLOCKWISE_90).end()
                .face(Direction.DOWN).texture("#0").uvs(10, 10, 12, 0).rotation(FaceRotation.COUNTERCLOCKWISE_90).end()
                .end();

        ModelFile interdimensionalTesseractBase = this.models()
                .getExistingFile(ModelTextures.getBlockTexture(ModBlocks.INTERDIMENSIONAL_TESSERACT, "_base"));

        MultiPartBlockStateBuilder interdimensionalTesseractMultipart = getMultipartBuilder(
                ModBlocks.INTERDIMENSIONAL_TESSERACT)
                .part().modelFile(interdimensionalTesseractBase).addModel().end();

        this.sixWayMultipart(interdimensionalTesseractMultipart,
                this.models().getExistingFile(
                        ModelTextures.getBlockTexture(ModBlocks.INTERDIMENSIONAL_TESSERACT, "_connection")),
                false);

        this.simpleBlockItem(ModBlocks.INTERDIMENSIONAL_TESSERACT, interdimensionalTesseractBase);

        this.simpleBlockAndItem(ModBlocks.TESSERACT_FRAME);

        this.tesseractInterface(ModBlocks.TESSERACT_ITEM_INTERFACE);
        this.tesseractInterface(ModBlocks.TESSERACT_FLUID_INTERFACE);
        this.tesseractInterface(ModBlocks.TESSERACT_ENERGY_INTERFACE);
    }

    private void sixWayMultipart(MultiPartBlockStateBuilder builder, ModelFile side, boolean uvLock) {
        SixWayBlock.PROPERTY_BY_DIRECTION.entrySet().forEach(e -> {
            Direction dir = e.getKey();
            Builder<PartBuilder> partBuilder = builder.part().modelFile(side);
            if (dir.getAxis().isHorizontal()) {
                partBuilder = partBuilder.rotationY((((int) dir.toYRot()) + 180) % 360);
            } else {
                partBuilder = partBuilder.rotationX(dir == Direction.UP ? -90 : 90);
            }
            partBuilder.uvLock(uvLock).addModel().condition(e.getValue(), true);
        });
    }

    private void tesseractInterface(AbstractTesseractInterfaceBlock block) {
        ModelFile interfaceConnected = this.models()
                .getBuilder(block.getRegistryName().getPath() + "_connected")
                .texture("base", this.blockTexture(block))
                .texture("connected", this.tesseractConnectedLocation)
                .texture("particle", this.blockTexture(block))
                .element().cube("#base")
                .face(Direction.NORTH).texture("#connected").end()
                .end();

        ModelFile interfaceBase = this.cubeAll(block);

        VariantBlockStateBuilder interfaceBuilder = this.getVariantBuilder(block);
        PartialBlockstate connectedState = interfaceBuilder.partialState()
                .with(AbstractTesseractInterfaceBlock.CONNECTED, true);

        interfaceBuilder.forAllStates((blockState) -> {
            if (connectedState.test(blockState)) {
                Direction dir = blockState.getValue(AbstractTesseractInterfaceBlock.DIRECTION);

                return new ConfiguredModel[] {
                        new ConfiguredModel(interfaceConnected,
                                dir.getAxis().isVertical() ? dir == Direction.UP ? -90 : 90 : 0,
                                dir.getAxis().isHorizontal() ? (((int) dir.toYRot()) + 180) % 360 : 0,
                                true) };
            }

            return new ConfiguredModel[] { new ConfiguredModel(interfaceBase) };
        });

        this.simpleBlockItem(block, interfaceBase);
    }

    private void simpleBlockAndItem(Block block, ModelFile model) {
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    private void simpleBlockAndItem(Block block) {
        this.simpleBlockAndItem(block, this.cubeAll(block));
    }
}
