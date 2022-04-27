package ftm._0xfmel.itdmtrct.utils;

import ftm._0xfmel.itdmtrct.capabilities.ITesseractChannels.TesseractChannel;
import ftm._0xfmel.itdmtrct.tile.InterdimensionalTesseractTile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class ChannelUtil {
    private static final double VALID_RANGE = Math.pow(16.0d * 3 / 2, 2); // 3x3 chunk area

    public static boolean isChannelDistanceValid(TesseractChannel channel, TileEntity te) {
        RegistryKey<World> dimensionKey = RegistryKey.create(Registry.DIMENSION_REGISTRY,
                new ResourceLocation(channel.dimension));
        World teLevel = te.getLevel();
        DimensionType channelDimType = teLevel.getServer().getLevel(dimensionKey).dimensionType();
        double tpScale = DimensionType.getTeleportationScale(teLevel.dimensionType(), channelDimType);
        BlockPos tePos = te.getBlockPos();

        if (tpScale < 1) {
            return channel.pos.distSqr(tePos.getX() * tpScale, channel.pos.getY(), tePos.getZ() * tpScale,
                    false) <= ChannelUtil.VALID_RANGE;
        }

        return tePos.distSqr(channel.pos.getX() / tpScale, tePos.getY(), channel.pos.getZ() / tpScale,
                false) <= ChannelUtil.VALID_RANGE;
    }

    public static boolean isChannelDimensionValid(TesseractChannel channel, TileEntity te) {
        return !channel.dimension.equals(te.getLevel().dimension().location().toString());
    }

    public static boolean isValid(TesseractChannel channel, InterdimensionalTesseractTile te,
            ServerPlayerEntity player) {

        return ChannelUtil.isChannelDimensionValid(channel, te)
                && ChannelUtil.isChannelDistanceValid(channel, te)
                && (!channel.isSelected || channel.id == te.getChannelId())
                && (!channel.isPrivate || channel.playerUuid.equals(player.getUUID()));
    }
}
