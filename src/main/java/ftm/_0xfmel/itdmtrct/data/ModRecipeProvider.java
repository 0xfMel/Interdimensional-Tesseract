package ftm._0xfmel.itdmtrct.data;

import java.util.function.Consumer;

import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItems;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ModRecipeProvider extends RecipeProvider {
	public ModRecipeProvider(DataGenerator pGenerator) {
		super(pGenerator);
	}

	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> finish) {
		ShapedRecipeBuilder.shaped(ModItems.IRON_GEAR)
				.define('I', ItemTags.bind("forge:ingots/iron"))
				.define('N', ItemTags.bind("forge:nuggets/iron"))
				.pattern(" I ")
				.pattern("INI")
				.pattern(" I ")
				.unlockedBy("has_iron_ingot", RecipeProvider.has(ItemTags.bind("forge:ingots/iron")))
				.save(finish);
		ShapedRecipeBuilder.shaped(ModBlocks.TESSERACT_FRAME)
				.define('E', ItemTags.bind("forge:ender_pearls"))
				.define('D', ItemTags.bind("forge:gems/diamond"))
				.define('I', ItemTags.bind("forge:gears/iron"))
				.define('B', ItemTags.bind("forge:storage_blocks/iron"))
				.pattern("EDE")
				.pattern("IBI")
				.pattern("EDE")
				.unlockedBy("has_ender_pearl", RecipeProvider.has(ItemTags.bind("forge:ender_pearls")))
				.save(finish);
		ShapedRecipeBuilder.shaped(ModBlocks.INTERDIMENSIONAL_TESSERACT)
				.define('D', ItemTags.bind("forge:gems/diamond"))
				.define('G', ItemTags.bind("forge:ingots/gold"))
				.define('Y', Items.ENDER_EYE)
				.define('T', ModBlocks.TESSERACT_FRAME)
				.define('I', ItemTags.bind("forge:gears/iron"))
				.define('R', ItemTags.bind("forge:dusts/redstone"))
				.pattern("DGD")
				.pattern("YTY")
				.pattern("IRI")
				.unlockedBy("has_diamond", RecipeProvider.has(ItemTags.bind("forge:gems/diamond")))
				.save(finish);
		ShapedRecipeBuilder.shaped(ModItems.KEEP_LOADED_UPGRADE)
				.define('Y', Items.ENDER_EYE)
				.define('I', ItemTags.bind("forge:gears/iron"))
				.define('E', ItemTags.bind("forge:gems/emerald"))
				.define('G', Items.GRASS_BLOCK)
				.pattern("YEY")
				.pattern("EGE")
				.pattern("IEI")
				.unlockedBy("has_ender_eye", RecipeProvider.has(Items.ENDER_EYE))
				.save(finish);
		ShapedRecipeBuilder.shaped(ModBlocks.TESSERACT_ITEM_INTERFACE)
				.define('I', ItemTags.bind("forge:gears/iron"))
				.define('C', ItemTags.bind("forge:chests"))
				.define('E', ItemTags.bind("forge:ender_pearls"))
				.define('#', ItemTags.bind("forge:storage_blocks/iron"))
				.define('D', ItemTags.bind("forge:chests/ender"))
				.pattern("ICI")
				.pattern("E#E")
				.pattern("IDI")
				.unlockedBy("has_ender_pearl", RecipeProvider.has(ItemTags.bind("forge:ender_pearls")))
				.save(finish, this.getSuffixedLocation(ModBlocks.TESSERACT_ITEM_INTERFACE, "_1"));
		ShapedRecipeBuilder.shaped(ModBlocks.TESSERACT_ITEM_INTERFACE)
				.define('I', ItemTags.bind("forge:gears/iron"))
				.define('C', ItemTags.bind("forge:chests"))
				.define('E', ItemTags.bind("forge:ender_pearls"))
				.define('#', ItemTags.bind("forge:storage_blocks/iron"))
				.define('D', ItemTags.bind("forge:chests/ender"))
				.pattern("IDI")
				.pattern("E#E")
				.pattern("ICI")
				.unlockedBy("has_ender_pearl", RecipeProvider.has(ItemTags.bind("forge:ender_pearls")))
				.save(finish, this.getSuffixedLocation(ModBlocks.TESSERACT_ITEM_INTERFACE, "_2"));
		ShapedRecipeBuilder.shaped(ModBlocks.TESSERACT_FLUID_INTERFACE)
				.define('I', ItemTags.bind("forge:gears/iron"))
				.define('B', Items.BUCKET)
				.define('E', ItemTags.bind("forge:ender_pearls"))
				.define('#', ItemTags.bind("forge:storage_blocks/iron"))
				.pattern("IBI")
				.pattern("E#E")
				.pattern("IBI")
				.unlockedBy("has_ender_pearl", RecipeProvider.has(ItemTags.bind("forge:ender_pearls")))
				.save(finish);
		ShapedRecipeBuilder.shaped(ModBlocks.TESSERACT_ENERGY_INTERFACE)
				.define('I', ItemTags.bind("forge:gears/iron"))
				.define('G', ItemTags.bind("forge:storage_blocks/gold"))
				.define('E', ItemTags.bind("forge:ender_pearls"))
				.define('#', ItemTags.bind("forge:storage_blocks/iron"))
				.define('R', ItemTags.bind("forge:storage_blocks/redstone"))
				.pattern("IGI")
				.pattern("E#E")
				.pattern("IRI")
				.unlockedBy("has_ender_pearl", RecipeProvider.has(ItemTags.bind("forge:ender_pearls")))
				.save(finish, this.getSuffixedLocation(ModBlocks.TESSERACT_ENERGY_INTERFACE, "_1"));
		ShapedRecipeBuilder.shaped(ModBlocks.TESSERACT_ENERGY_INTERFACE)
				.define('I', ItemTags.bind("forge:gears/iron"))
				.define('G', ItemTags.bind("forge:storage_blocks/gold"))
				.define('E', ItemTags.bind("forge:ender_pearls"))
				.define('#', ItemTags.bind("forge:storage_blocks/iron"))
				.define('R', ItemTags.bind("forge:storage_blocks/redstone"))
				.pattern("IRI")
				.pattern("E#E")
				.pattern("IGI")
				.unlockedBy("has_ender_pearl", RecipeProvider.has(ItemTags.bind("forge:ender_pearls")))
				.save(finish, this.getSuffixedLocation(ModBlocks.TESSERACT_ENERGY_INTERFACE, "_2"));
	}

	private ResourceLocation getSuffixedLocation(ForgeRegistryEntry<?> entry, String suffix) {
		return new ResourceLocation(ModGlobals.MOD_ID, entry.getRegistryName().getPath() + suffix);
	}
}
