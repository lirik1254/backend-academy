package maze;

import lombok.Getter;
import lombok.Setter;
import maze.generator.Surface;

/**
 * Modified Point class for generating a maze by the prim algorithm */
@Getter
@Setter
public class Cell {

    public Cell(int x, int y,  Surface surface) {
        this.x = x;
        this.y = y;
        this.surface = surface;
    }

    private int x;
    private int y;

    private Surface surface;

}
