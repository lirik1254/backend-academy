package fractal.flame.transformations;

import fractal.flame.utils.Point;

public class PolarTransform implements Transform {
    public Point transformatePoint(Point p) {
        double newX = Math.atan(p.y() / p.x()) / Math.PI;
        double newY = Math.sqrt(Math.pow(p.x(), 2) + Math.pow(p.y(), 2)) - 1;
        return new Point(newX, newY, p.rgb(), p.hitsNumber(), p.normal());
    }

    @Override
    public String toString() {
        return "POLAR";
    }
}
