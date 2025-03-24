package fractal.flame.transformations;

import fractal.flame.utils.Point;

public class HearthTransform implements Transform {
    public Point transformatePoint(Point p) {
        double squareSumSqrt = Math.sqrt(Math.pow(p.x(), 2) + Math.pow(p.y(), 2));
        double squareSumSqrtMultipleAtan = Math.sqrt(Math.pow(p.x(), 2) + Math.pow(p.y(), 2))
            * Math.atan(p.y() / p.x());
        double newX = squareSumSqrt * Math.sin(squareSumSqrtMultipleAtan);
        double newY = squareSumSqrt * Math.cos(squareSumSqrtMultipleAtan);
        return new Point(newX, newY, p.rgb(), p.hitsNumber(), p.normal());
    }

    @Override
    public String toString() {
        return "HEARTH";
    }
}
