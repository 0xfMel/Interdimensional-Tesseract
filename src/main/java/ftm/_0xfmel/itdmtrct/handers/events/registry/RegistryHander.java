package ftm._0xfmel.itdmtrct.handers.events.registry;

import ftm._0xfmel.itdmtrct.containers.ModContainerTypes;
import ftm._0xfmel.itdmtrct.gameobjects.block.ModBlocks;
import ftm._0xfmel.itdmtrct.gameobjects.item.ModItems;
import ftm._0xfmel.itdmtrct.tile.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.MOD)
public class RegistryHander {
    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> e) {
        e.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void onTileEntityTypesRegistry(final RegistryEvent.Register<TileEntityType<?>> e) {
        e.getRegistry().registerAll(ModTileEntityTypes.TILE_ENTITY_TYPES.toArray(new TileEntityType<?>[0]));
    }

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> e) {
        e.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
    }

    @SubscribeEvent
    public static void onContainerTypeRegistry(final RegistryEvent.Register<ContainerType<?>> e) {
        e.getRegistry().registerAll(ModContainerTypes.CONTAINER_TYPES.toArray(new ContainerType<?>[0]));
    }
}
