package maze;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode

public class Point {
    private int x;
    private int y;
    private double f = 0;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
