package ftm._0xfmel.itdmtrct.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class ModTileEntityTypes {
    public static final List<TileEntityType<?>> TILE_ENTITY_TYPES = new ArrayList<>();

    public static final TileEntityType<InterdimenstionalTesseractTile> INTERDIMENSIONAL_TESSERACT = build(
            "interdimensional_tesseract", InterdimenstionalTesseractTile::new, ModBlocks.INTERDIMENSIONAL_TESSERACT);

    private static <T extends TileEntity> TileEntityType<T> build(String name, Supplier<T> factory,
            Block... validBlocks) {
        TileEntityType<T> tet = TileEntityType.Builder.of(factory, validBlocks).build(null);
        tet.setRegistryName(ModGlobals.MOD_ID, name);
        TILE_ENTITY_TYPES.add(tet);
        return tet;
    }
}
