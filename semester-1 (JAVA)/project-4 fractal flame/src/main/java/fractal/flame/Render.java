package fractal.flame;

import fractal.flame.transformations.Transform;
import fractal.flame.utils.AffineCoef;
import fractal.flame.utils.Point;
import fractal.flame.utils.PointUtils;
import fractal.flame.utils.RGB;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Render {
    private double xMin;
    public double xMax;
    public double yMin;
    public double yMax;
    private final int minutesToTimeOut = 60;

    @SuppressWarnings({"ParameterNumber", "MagicNumber"})
    public List<List<Point>> render(
        int n, int eqCount, int it, int xRes, int yRes, boolean isSymmetry, List<AffineCoef> affineTransformations,
        List<Transform> transformations, int threadNumber
    ) {
        setMinMax(xRes, yRes);

        List<List<Point>> pixels = new ArrayList<>();
        initPixels(xRes, yRes, pixels);

        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);

        for (int num = 0; num < n; num++) {
            final int currentNum = num;
            executorService.submit(() -> {
                log.info("n = {}", currentNum);
                double newX = PointUtils.rand.nextDouble(xMin, xMax);
                double newY = PointUtils.rand.nextDouble(yMin, yMax);
                Transform transform = transformations.get(PointUtils.rand.nextInt(0, transformations.size()));
                Point newPoint = new Point(newX, newY, PointUtils.getRandRGB(), 0, 0);
                for (int step = -20; step < it; step++) {
                    int i = PointUtils.rand.nextInt(0, eqCount);
                    newPoint = PointUtils.getNewPoint(newPoint, affineTransformations.get(i));
                    newPoint = transform.transformatePoint(newPoint);
                    if (step >= 0 && newPoint.x() >= xMin && newPoint.x() <= xMax
                        && newPoint.y() >= yMin && newPoint.y() <= yMax) {
                        int xCoord = calculateCoordinate(newPoint.x(), xMin, xMax, xRes);
                        int yCoord = calculateCoordinate(newPoint.y(), yMin, yMax, yRes);
                        int ySymmetryCoord = 0;
                        if (isSymmetry) {
                            ySymmetryCoord = calculateSymmetryCoordinate(newPoint.y(), yMin, yMax, yRes);
                        }
                        if (xCoord < xRes && yCoord < yRes && ySymmetryCoord < yRes) {
                            synchronized (pixels.get(yCoord).get(xCoord)) {
                                updatePixel(pixels.get(yCoord).get(xCoord), affineTransformations.get(i).rgb());
                            }
                            if (isSymmetry) {
                                synchronized (pixels.get(ySymmetryCoord).get(xCoord)) {
                                    updatePixel(pixels.get(ySymmetryCoord).get(xCoord),
                                        affineTransformations.get(i).rgb());
                                }
                            }
                        }
                    }
                }
            });
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(minutesToTimeOut, TimeUnit.MINUTES)) {
                log.error("Потоки не завершились вовремя.");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Ожидание завершения потоков прервано.", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return pixels;
    }

    private void setMinMax(int xRes, int yRes) {
        if (xRes > yRes) {
            this.xMin = (double) -xRes / yRes;
            this.xMax = (double) xRes / yRes;
            this.yMin = -1;
            this.yMax = 1;
        } else {
            this.yMin = (double) -yRes / xRes;
            this.yMax = (double) yRes / xRes;
            this.xMin = -1;
            this.xMax = 1;
        }
    }

    private void initPixels(int xRes, int yRes, List<List<Point>> pixels) {
        for (int i = 0; i < yRes; i++) {
            pixels.add(i, new ArrayList<>());
            for (int j = 0; j < xRes; j++) {
                pixels.get(i).add(j, new Point(0, 0, new RGB((short) 0, (short) 0, (short) 0), 0, 0));
            }
        }
    }

    private void updatePixel(Point pixel, RGB rgb) {
        if (pixel.hitsNumber() == 0) {
            pixel.rgb(rgb);
        } else {
            short r = (short) ((pixel.rgb().r() + rgb.r()) / 2);
            short g = (short) ((pixel.rgb().g() + rgb.g()) / 2);
            short b = (short) ((pixel.rgb().b() + rgb.b()) / 2);
            pixel.rgb(new RGB(r, g, b));
        }
        pixel.hitsNumberInc();
    }

    private int calculateCoordinate(double value, double min, double max, int resolution) {
        return (int) (resolution - Math.floor(((max - value) / (max - min)) * resolution));
    }

    private int calculateSymmetryCoordinate(double value, double min, double max, int resolution) {
        return (int) (resolution - Math.floor(((max + value) / (max - min)) * resolution));
    }

}
