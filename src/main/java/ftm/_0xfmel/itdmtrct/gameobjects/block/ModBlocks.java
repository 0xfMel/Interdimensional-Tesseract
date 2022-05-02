package ftm._0xfmel.itdmtrct.gameobjects.block;

import java.util.ArrayList;
import java.util.List;

import ftm._0xfmel.itdmtrct.gameobjects.item.ItemGroupCategory;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItemGroups;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class ModBlocks {
        public static final List<Block> BLOCKS = new ArrayList<>();

        public static final InterdimensionalTesseractBlock INTERDIMENSIONAL_TESSERACT = new InterdimensionalTesseractBlock();

        public static final ItemTesseractInterfaceBlock TESSERACT_ITEM_INTERFACE = new ItemTesseractInterfaceBlock();
        public static final FluidTesseractInterfaceBlock TESSERACT_FLUID_INTERFACE = new FluidTesseractInterfaceBlock();
        public static final EnergyTesseractInterfaceBlock TESSERACT_ENERGY_INTERFACE = new EnergyTesseractInterfaceBlock();

        public static final BaseBlock TESSERACT_FRAME = new BaseBlock("tesseract_frame",
                        AbstractBlock.Properties.of(Material.HEAVY_METAL).strength(8f, 9f)
                                        .sound(SoundType.METAL),
                        ModItemGroups.TAB, ItemGroupCategory.CRAFTING_ITEM);
}
