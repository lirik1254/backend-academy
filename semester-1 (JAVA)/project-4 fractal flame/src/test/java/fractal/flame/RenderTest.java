package fractal.flame;

import fractal.flame.transformations.RandomTransform;
import fractal.flame.transformations.Transform;
import fractal.flame.utils.AffineCoef;
import fractal.flame.utils.Point;
import fractal.flame.utils.PointUtils;
import fractal.flame.utils.RGB;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RenderTest {

    static Render render = new Render();
    static List<List<Point>> pointList;

    static ArrayList<AffineCoef> affineCoefs;
    static ArrayList<Transform> transformations;

    @BeforeAll
    public static void init() {
        initRender();
        initTransformationsList();
        initAffineCoefsList();
    }

    public static void initRender() {
        pointList = render.render(100, 7, 100, 1920, 1080, false, PointUtils.getAffineTransformationList(7),
            RandomTransform.getRandomTransformation(), 6);
    }

    public static void initAffineCoefsList() {
        affineCoefs = new ArrayList<>(Arrays.asList(
            new AffineCoef(0.3, 0.8, -0.2, -0.5, 0.75, -0.13, new RGB((short) 100, (short) 200, (short) 52)),
            new AffineCoef(0.1, 0.2, -0.43, -0.14, 0.81, -0.18, new RGB((short) 72, (short) 111, (short) 3)),
            new AffineCoef(-0.9, 0.34, 0.71, -0.1, 0.25, -0.11, new RGB((short) 11, (short) 105, (short) 34))
        ));
    }

    public static void initTransformationsList() {
        transformations = (ArrayList<Transform>) RandomTransform.getRandomTransformation();
    }

    @Test
    public void pixelsListSizeTest() {
        assertEquals(pointList.size(), 1080);
        assertEquals(pointList.getFirst().size(), 1920);
    }

    @Test
    public void pixelsContentTest() {
        pointList = pointList.stream()
            .map(innerList -> innerList.stream()
                .filter(point -> point.rgb().r() > 0
                    || point.rgb().g() > 0 || point.rgb().b() > 0)
                .toList())
            .filter(innerList -> !innerList.isEmpty())
            .toList();

        assertFalse(pointList.isEmpty());
    }

    @Test
    public void speedTest() {
        Instant oneThreadStart = Instant.now();
        render.render(5000, 3, 5000, 1920, 1080, true, affineCoefs, transformations, 1);
        Instant oneThreadStop = Instant.now();

        Instant availableProcessorsStart = Instant.now();
        render.render(5000, 3, 5000, 1920, 1080, true, affineCoefs, transformations, Runtime.getRuntime().availableProcessors());
        Instant availableProcessorsStop = Instant.now();

        assertTrue(Duration.between(oneThreadStart, oneThreadStop).toNanos() >
            Duration.between(availableProcessorsStart, availableProcessorsStop).toNanos());
    }

}
