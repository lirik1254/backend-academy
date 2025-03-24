package fractal.flame.transformations;

import fractal.flame.utils.Point;

public class SphericalTransform implements Transform {
    public Point transformatePoint(Point p) {
        double squaresSum = ((Math.pow(p.x(), 2) + Math.pow(p.y(), 2)));
        double newX = p.x() / squaresSum;
        double newY = p.y() / squaresSum;
        return new Point(newX, newY, p.rgb(), p.hitsNumber(), p.normal());
    }

    @Override
    public String toString() {
        return "SPHERICAL";
    }
}
