package ftm._0xfmel.itdmtrct.utils.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerUtil {
    public static int getRedstoneSignalFromItemHandler(IItemHandler itemHandler) {
        int i = 0;
        float f = 0.0F;

        for (int j = 0; j < itemHandler.getSlots(); ++j) {
            ItemStack itemstack = itemHandler.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                f += (float) itemstack.getCount()
                        / (float) Math.min(itemHandler.getSlotLimit(j), itemstack.getMaxStackSize());
                ++i;
            }
        }

        f = f / (float) itemHandler.getSlots();
        return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
    }
}
