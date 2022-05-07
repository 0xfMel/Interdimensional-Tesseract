package ftm._0xfmel.itdmtrct.handers.events.client;

import java.util.Map;

import com.google.common.collect.Maps;

import ftm._0xfmel.itdmtrct.client.model.TesseractBakedModel;
import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import ftm._0xfmel.itdmtrct.utils.Logging;
import ftm._0xfmel.itdmtrct.utils.client.ModelUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@SuppressWarnings("resource")
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Bus.MOD, value = { Dist.CLIENT })
public class ModelHandler {
    private static final ResourceLocation POWER_MODEL_LOCATION = new ResourceLocation(ModGlobals.MOD_ID,
            "block/" + ModBlocks.INTERDIMENSIONAL_TESSERACT.getRegistryName().getPath() + "_power");

    @SubscribeEvent
    public static void onModelBakeEvent(ModelBakeEvent e) {
        ModelLoader modelLoader = e.getModelLoader();

        Map<Direction, IBakedModel> powerBakedModelByDirection = Util
                .make(Maps.newEnumMap(Direction.class), (p_203421_0_) -> {
                    ModelUtil.ROTATION_BY_DIRECTION.forEach((direction, rotation) -> p_203421_0_.put(direction,
                            ModelHandler.bakeModel(modelLoader, ModelHandler.POWER_MODEL_LOCATION, rotation)));
                });

        for (BlockState state : ModBlocks.INTERDIMENSIONAL_TESSERACT.getStateDefinition().getPossibleStates()) {
            ModelResourceLocation model = BlockModelShapes.stateToModelLocation(state);
            IBakedModel existingModel = e.getModelRegistry().get(model);
            if (existingModel == null) {
                Logging.LOGGER.warn("No vanilla baked model");
            } else if (existingModel instanceof TesseractBakedModel) {
                Logging.LOGGER.warn("Tried to replace model twice");
            } else {
                TesseractBakedModel newModel = new TesseractBakedModel(existingModel, powerBakedModelByDirection);
                e.getModelRegistry().put(model, newModel);
            }
        }
    }

    private static IBakedModel bakeModel(ModelBakery bakery, ResourceLocation location, ModelRotation rotation) {
        return bakery.getBakedModel(
                location,
                rotation,
                bakery.getSpriteMap()::getSprite);
    }
}
