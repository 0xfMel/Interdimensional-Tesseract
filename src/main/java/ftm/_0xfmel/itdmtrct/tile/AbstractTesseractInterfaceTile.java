package ftm._0xfmel.itdmtrct.tile;

import java.util.Optional;

import ftm._0xfmel.itdmtrct.gameobjects.block.AbstractTesseractInterfaceBlock;
import ftm._0xfmel.itdmtrct.utils.interfaces.ITesseractInterfaceTile;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public abstract class AbstractTesseractInterfaceTile<C> extends TileEntity implements ITesseractInterfaceTile {
    private final Capability<C> capability;

    private LazyOptional<C> capabilityLazyOptional = null;

    private Direction direction;

    public <T extends AbstractTesseractInterfaceTile<C>> AbstractTesseractInterfaceTile(TileEntityType<T> tileType,
            Capability<C> capability) {

        super(tileType);

        this.capability = capability;
    }

    public <T extends AbstractTesseractInterfaceTile<C>> AbstractTesseractInterfaceTile(TileEntityType<T> tileType,
            Capability<C> capability, Direction direction) {

        this(tileType, capability);

        this.direction = direction;
    }

    @Override
    public void load(BlockState state, CompoundNBT p_230337_2_) {
        super.load(state, p_230337_2_);

        this.direction = state.getValue(AbstractTesseractInterfaceBlock.DIRECTION);
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Optional<InterdimensionalTesseractTile> getTesseractTile() {
        TileEntity te = this.level.getBlockEntity(this.worldPosition.relative(this.direction));

        if (te instanceof InterdimensionalTesseractTile) {
            return Optional.of((InterdimensionalTesseractTile) te);
        }

        return Optional.empty();
    }

    protected abstract C getCapabilityImplementation(InterdimensionalTesseractTile tesseractTile);

    private LazyOptional<C> getCapabilityLazyOptional() {
        Optional<InterdimensionalTesseractTile> tesseractOptional = this.getTesseractTile();
        if (tesseractOptional.isPresent()) {
            InterdimensionalTesseractTile tesseractTile = tesseractOptional.get();
            if (tesseractTile.getChannelId() >= 0) {
                return LazyOptional.of(() -> getCapabilityImplementation(tesseractTile));
            }
        }

        return LazyOptional.empty();
    }

    protected LazyOptional<C> getCapability() {
        if (this.capabilityLazyOptional == null) {
            this.capabilityLazyOptional = this.getCapabilityLazyOptional();
        }
        return this.capabilityLazyOptional;
    }

    @Override
    public void clearCache() {
        super.clearCache();

        if (this.capabilityLazyOptional != null) {
            LazyOptional<C> oldCap = this.capabilityLazyOptional;
            this.capabilityLazyOptional = null;
            oldCap.invalidate();
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == this.capability) {
            return this.getCapability().cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();

        if (this.capabilityLazyOptional != null) {
            this.capabilityLazyOptional.invalidate();
        }
    }
}
