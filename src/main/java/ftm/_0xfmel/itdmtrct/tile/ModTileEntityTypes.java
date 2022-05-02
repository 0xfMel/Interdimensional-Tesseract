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

        public static final TileEntityType<InterdimensionalTesseractTile> INTERDIMENSIONAL_TESSERACT = build(
                        "interdimensional_tesseract", InterdimensionalTesseractTile::new,
                        ModBlocks.INTERDIMENSIONAL_TESSERACT);

        public static final TileEntityType<TesseractItemInterfaceTile> TESSERACT_ITEM_INTERFACE = build(
                        "tesseract_item_interface", TesseractItemInterfaceTile::new,
                        ModBlocks.TESSERACT_ITEM_INTERFACE);
        public static final TileEntityType<TesseractFluidInterfaceTile> TESSERACT_FLUID_INTERFACE = build(
                        "tesseract_fluid_interface", TesseractFluidInterfaceTile::new,
                        ModBlocks.TESSERACT_FLUID_INTERFACE);
        public static final TileEntityType<TesseractEnergyInterfaceTile> TESSERACT_ENERGY_INTERFACE = build(
                        "tesseract_energy_interface", TesseractEnergyInterfaceTile::new,
                        ModBlocks.TESSERACT_ENERGY_INTERFACE);

        private static <T extends TileEntity> TileEntityType<T> build(String name, Supplier<T> factory,
                        Block... validBlocks) {
                TileEntityType<T> tet = TileEntityType.Builder.of(factory, validBlocks).build(null);
                tet.setRegistryName(ModGlobals.MOD_ID, name);
                TILE_ENTITY_TYPES.add(tet);
                return tet;
        }
}
