package ftm._0xfmel.itdmtrct.tile;

import java.util.Optional;

import ftm._0xfmel.itdmtrct.gameobjects.block.TesseractInterfaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TesseractItemInterfaceTile extends TileEntity {
    private LazyOptional<IItemHandler> itemHandlerLazyOptional = null;

    private Direction direction;

    public TesseractItemInterfaceTile() {
        super(ModTileEntityTypes.TESSERACT_ITEM_INTERFACE);
    }

    public TesseractItemInterfaceTile(Direction direction) {
        this();

        this.direction = direction;
    }

    @Override
    public void load(BlockState state, CompoundNBT p_230337_2_) {
        super.load(state, p_230337_2_);

        this.direction = state.getValue(TesseractInterfaceBlock.DIRECTION);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Optional<InterdimensionalTesseractTile> getTesseractTile() {
        TileEntity te = this.level.getBlockEntity(this.worldPosition.relative(this.direction));

        if (te instanceof InterdimensionalTesseractTile) {
            return Optional.of((InterdimensionalTesseractTile) te);
        }

        return Optional.empty();
    }

    private LazyOptional<IItemHandler> getItemHandlerLazyOptional() {
        Optional<InterdimensionalTesseractTile> tesseractOptional = this.getTesseractTile();
        if (tesseractOptional.isPresent()) {
            InterdimensionalTesseractTile tesseractTile = tesseractOptional.get();
            if (tesseractTile.getChannelId() >= 0) {
                return LazyOptional.of(() -> new ItemStackHandler(tesseractTile.getStacksForInterface()));
            }
        }

        return LazyOptional.empty();
    }

    @Override
    public void clearCache() {
        super.clearCache();

        if (this.itemHandlerLazyOptional != null) {
            LazyOptional<IItemHandler> oldCap = this.itemHandlerLazyOptional;
            this.itemHandlerLazyOptional = null;
            oldCap.invalidate();
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.itemHandlerLazyOptional == null) {
                this.itemHandlerLazyOptional = this.getItemHandlerLazyOptional();
            }
            return this.itemHandlerLazyOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        if (this.itemHandlerLazyOptional != null) {
            this.itemHandlerLazyOptional.invalidate();
        }
    }
}
