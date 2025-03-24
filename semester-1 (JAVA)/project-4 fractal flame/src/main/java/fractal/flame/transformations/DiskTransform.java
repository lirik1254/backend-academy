package fractal.flame.transformations;

import fractal.flame.utils.Point;

public class DiskTransform implements Transform {
    public Point transformatePoint(Point p) {
        double oneDivPiMultipleAtan = 1 / Math.PI * Math.atan(p.y() / p.x());
        double piMultipleSquareSumSqrt = Math.PI * Math.sqrt(Math.pow(p.x(), 2) + Math.pow(p.y(), 2));
        double newX = oneDivPiMultipleAtan * Math.sin(piMultipleSquareSumSqrt);
        double newY = oneDivPiMultipleAtan * Math.cos(piMultipleSquareSumSqrt);
        return new Point(newX, newY, p.rgb(), p.hitsNumber(), p.normal());
    }

    @Override
    public String toString() {
        return "DISK";
    }
}
