package ftm._0xfmel.itdmtrct.containers;

import java.util.ArrayList;
import java.util.List;

import ftm._0xfmel.itdmtrct.globals.ModGlobals;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.IContainerFactory;

public class ModContainerTypes {
    public static final List<ContainerType<?>> CONTAINER_TYPES = new ArrayList<>();

    public static final ContainerType<TesseractContainer> TESSERACT = build(
            "tesseract", TesseractContainer::factory);

    private static <T extends Container> ContainerType<T> build(String name, IContainerFactory<T> factory) {
        ContainerType<T> containerType = IForgeContainerType.create(factory);
        containerType.setRegistryName(ModGlobals.MOD_ID, name);
        ModContainerTypes.CONTAINER_TYPES.add(containerType);
        return containerType;
    }
}
