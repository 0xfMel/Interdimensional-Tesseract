package ftm._0xfmel.itdmtrct.utils.items;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class DisableableItemStackHandler extends ItemStackHandler {
    private Supplier<Boolean> canTransfer;

    public DisableableItemStackHandler(NonNullList<ItemStack> stacks, Supplier<Boolean> canTransfer) {
        super(stacks);
        this.canTransfer = canTransfer;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!this.canTransfer.get())
            return ItemStack.EMPTY;

        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!this.canTransfer.get())
            return stack;

        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (!this.canTransfer.get())
            return false;

        return super.isItemValid(slot, stack);
    }
}
