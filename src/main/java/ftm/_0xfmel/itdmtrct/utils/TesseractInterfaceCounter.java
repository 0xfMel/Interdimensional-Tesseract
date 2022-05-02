package ftm._0xfmel.itdmtrct.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ftm._0xfmel.itdmtrct.gameobjects.block.AbstractTesseractInterfaceBlock;
import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import ftm._0xfmel.itdmtrct.utils.interfaces.IInterfaceCounterListener;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.util.INBTSerializable;

public class TesseractInterfaceCounter implements INBTSerializable<CompoundNBT> {
    private final Map<AbstractTesseractInterfaceBlock, Integer> counts = new HashMap<>();
    private int total = 0;

    private IInterfaceCounterListener listener = null;

    public TesseractInterfaceCounter() {
    }

    public TesseractInterfaceCounter(IInterfaceCounterListener listener) {
        this.listener = listener;
    }

    public int addCount(AbstractTesseractInterfaceBlock interfaceBlock, int amount) {
        int newCount = this.getCount(interfaceBlock) + amount;
        this.counts.put(interfaceBlock, newCount);
        this.total += amount;
        this.changed();
        return newCount;
    }

    public int addCount(AbstractTesseractInterfaceBlock interfaceBlock) {
        return this.addCount(interfaceBlock, 1);
    }

    public int removeCount(AbstractTesseractInterfaceBlock interfaceBlock, int amount) {
        return this.addCount(interfaceBlock, -amount);
    }

    public int removeCount(AbstractTesseractInterfaceBlock interfaceBlock) {
        return this.removeCount(interfaceBlock, 1);
    }

    public void setCount(AbstractTesseractInterfaceBlock interfaceBlock, int amount) {
        int current = this.getCount(interfaceBlock);
        this.counts.put(interfaceBlock, amount);
        this.total += amount - current;
        this.changed();
    }

    public void addAll(TesseractInterfaceCounter interfaceCounter) {
        this.total += interfaceCounter.total;
        interfaceCounter.counts.entrySet().forEach((entry) -> this.addCount(entry.getKey(), entry.getValue()));
        this.changed();
    }

    public void removeAll(TesseractInterfaceCounter interfaceCounter) {
        this.total -= interfaceCounter.total;
        this.counts.entrySet()
                .forEach((entry) -> this.counts.put(entry.getKey(), this.getCount(entry.getKey()) - entry.getValue()));
        this.changed();
    }

    public int getCount(AbstractTesseractInterfaceBlock interfaceBlock) {
        return this.counts.getOrDefault(interfaceBlock, 0);
    }

    public int getTotal() {
        return this.total;
    }

    private void changed() {
        if (this.listener != null) {
            this.listener.onInterfaceCounterChange();
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("total", this.total);

        int entries = 0;
        for (Entry<AbstractTesseractInterfaceBlock, Integer> entry : this.counts.entrySet()) {
            int value = entry.getValue();
            if (value > 0) {
                String key = entry.getKey().getRegistryName().getPath();
                nbt.putString("key" + entries++, key);
                nbt.putInt(key, entry.getValue());
            }
        }

        nbt.putInt("entries", entries);
        return nbt;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.total = nbt.getInt("total");

        int entries = nbt.getInt("entries");
        for (int i = 0; i < entries; i++) {
            String key = nbt.getString("key" + i);
            Block block = Registry.BLOCK.get(new ResourceLocation(ModGlobals.MOD_ID, key));
            if (block instanceof AbstractTesseractInterfaceBlock) {
                this.counts.put((AbstractTesseractInterfaceBlock) block, nbt.getInt(key));
            }
        }

        this.changed();
    }
}
