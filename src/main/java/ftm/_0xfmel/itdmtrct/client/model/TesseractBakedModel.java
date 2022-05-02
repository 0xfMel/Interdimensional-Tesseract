package ftm._0xfmel.itdmtrct.client.model;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

@OnlyIn(Dist.CLIENT)
public class TesseractBakedModel implements IBakedModel {
    public static final ModelProperty<List<Direction>> POWERED_DIRECTIONS_PROPERTY = new ModelProperty<>();

    private final IBakedModel baseModel;
    private final Map<Direction, IBakedModel> powerBakedModelByDirection;

    public TesseractBakedModel(IBakedModel baseModel, Map<Direction, IBakedModel> powerBakedModelByDirection) {
        this.baseModel = baseModel;
        this.powerBakedModelByDirection = powerBakedModelByDirection;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
        List<BakedQuad> quads = baseModel.getQuads(state, side, rand, extraData);
        if (extraData.hasProperty(TesseractBakedModel.POWERED_DIRECTIONS_PROPERTY)) {
            quads.addAll(extraData.getData(TesseractBakedModel.POWERED_DIRECTIONS_PROPERTY).stream()
                    .map(this.powerBakedModelByDirection::get)
                    .flatMap((powerBaked) -> powerBaked.getQuads(state, side, rand, extraData).stream())
                    .collect(Collectors.toList()));
        }

        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return baseModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.baseModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.baseModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.baseModel.isCustomRenderer();
    }

    @SuppressWarnings("deprecation")
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.baseModel.getParticleIcon();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.baseModel.getOverrides();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState pState, Direction pSide, Random pRand) {
        return this.getQuads(pState, pSide, pRand, EmptyModelData.INSTANCE);
    }
}
