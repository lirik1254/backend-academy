package fractal.flame.transformations;

import fractal.flame.utils.Point;

public class LinearTransform implements Transform {
    public Point transformatePoint(Point p) {
        return new Point(p.x(), p.y(), p.rgb(), p.hitsNumber(), p.normal());
    }

    @Override
    public String toString() {
        return "LINEAR";
    }
}
