package fractal.flame.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Point {
    private double x;
    private double y;
    @Setter
    private RGB rgb;
    @Setter
    private int hitsNumber;
    @Setter
    private double normal;

    public void hitsNumberInc() {
        this.hitsNumber += 1;
    }
}
