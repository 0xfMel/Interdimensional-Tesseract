package ftm._0xfmel.itdmtrct.handers.events.entity.player;

import java.util.List;

import ftm._0xfmel.itdmtrct.gameobjects.block.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerInteractHandler {
    private static final INamedTag<Item> WRENCH_TAG = ItemTags.bind("forge:tools/wrench");

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock e) {
        if (e.getItemStack().getItem().is(PlayerInteractHandler.WRENCH_TAG) && e.getPlayer().isCrouching()) {
            World world = e.getWorld();
            BlockPos pos = e.getPos();
            BlockState blockState = world.getBlockState(pos);
            if (blockState.getBlock() instanceof IWrenchable) {
                if (world instanceof ServerWorld) {
                    blockState.getBlock();

                    List<ItemStack> stacks = Block.getDrops(blockState, (ServerWorld) world, pos,
                            world.getBlockEntity(pos));

                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

                    stacks.stream().forEach((stack) -> world.addFreshEntity(
                            new ItemEntity(world, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, stack)));
                }

                e.setCanceled(true);
                e.setCancellationResult(ActionResultType.sidedSuccess(world.isClientSide));
            }
        }
    }
}
