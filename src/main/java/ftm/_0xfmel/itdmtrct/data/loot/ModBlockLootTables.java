package ftm._0xfmel.itdmtrct.data.loot;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ModBlockLootTables extends BlockLootTables {
    private final Map<ResourceLocation, LootTable.Builder> map = Maps.newHashMap();

    @Override
    protected void addTables() {
        this.dropSelf(ModBlocks.INTERDIMENSIONAL_TESSERACT);
        this.dropSelf(ModBlocks.TESSERACT_ENERGY_INTERFACE);
        this.dropSelf(ModBlocks.TESSERACT_FLUID_INTERFACE);
        this.dropSelf(ModBlocks.TESSERACT_ITEM_INTERFACE);
        this.dropSelf(ModBlocks.TESSERACT_FRAME);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void accept(BiConsumer<ResourceLocation, Builder> p_accept_1_) {
        this.addTables();
        Set<ResourceLocation> set = Sets.newHashSet();

        for (Block block : ModBlocks.BLOCKS) {
            ResourceLocation resourcelocation = block.getLootTable();
            if (resourcelocation != LootTables.EMPTY && set.add(resourcelocation)) {
                LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
                if (loottable$builder == null) {
                    throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourcelocation,
                            Registry.BLOCK.getKey(block)));
                }

                p_accept_1_.accept(resourcelocation, loottable$builder);
            }
        }

        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
        }
    }

    @Override
    protected void add(Block pBlock, LootTable.Builder pLootTableBuilder) {
        this.map.put(pBlock.getLootTable(), pLootTableBuilder);
    }
}
