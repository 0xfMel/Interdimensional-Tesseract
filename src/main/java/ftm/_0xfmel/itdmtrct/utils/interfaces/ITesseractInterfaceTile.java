package ftm._0xfmel.itdmtrct.utils.interfaces;

import java.util.Optional;

import ftm._0xfmel.itdmtrct.tile.InterdimensionalTesseractTile;
import net.minecraft.util.Direction;

public interface ITesseractInterfaceTile {
    void setDirection(Direction direction);

    Optional<InterdimensionalTesseractTile> getTesseractTile();
}
