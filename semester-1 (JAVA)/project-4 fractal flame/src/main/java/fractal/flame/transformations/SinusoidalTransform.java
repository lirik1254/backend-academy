package fractal.flame.transformations;

import fractal.flame.utils.Point;

public class SinusoidalTransform implements Transform {
    public Point transformatePoint(Point p) {
        return new Point(Math.sin(p.x()), Math.sin(p.y()), p.rgb(), p.hitsNumber(), p.normal());
    }

    @Override
    public String toString() {
        return "SINUSOIDAL";
    }
}
