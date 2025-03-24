package fractal.flame;

import fractal.flame.utils.AffineCoef;
import fractal.flame.utils.Point;
import fractal.flame.utils.PointUtils;
import fractal.flame.utils.RGB;
import java.util.List;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PointUtilsTest {
    @Test
    public void getNewPointTest() {
        Point point = new Point(3, 4, PointUtils.getRandRGB(), 0, 0);
        Point newPoint = PointUtils.getNewPoint(point, new AffineCoef(1, 2, 3, 4, 5, 6, PointUtils.getRandRGB()));

        assertNotEquals(point.x(), newPoint.x());
        assertNotEquals(point.y(), newPoint.y());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 200, 10000})
    public void getAffineTransformationListSizeTest(int n) {
        assertEquals(PointUtils.getAffineTransformationList(n).size(), n);
    }

    @RepeatedTest(50)
    public void getAffineTransformationListContentTest() {
        List<AffineCoef> affineTransformationList = PointUtils.getAffineTransformationList(1);
        AffineCoef affineCoef = affineTransformationList.getFirst();
        double a = affineCoef.a();
        double b = affineCoef.b();
        double d = affineCoef.d();
        double e = affineCoef.e();
        boolean restriction = (Math.pow(a, 2) + Math.pow(b, 2) < 1)
            && (Math.pow(b, 2) + Math.pow(e, 2) < 1)
            && (Math.pow(a, 2) + Math.pow(b, 2) + Math.pow(d, 2) + Math.pow(e, 2)
            < 1 + Math.pow(a * e - b - d, 2));

        assertTrue(restriction);
    }

    @RepeatedTest(50)
    public void getRandRgbTest() {
        RGB rgb = PointUtils.getRandRGB();
        assertNotNull(rgb);
        assertTrue(rgb.r() >= 0 && rgb.r() <= 255);
        assertTrue(rgb.g() >= 0 && rgb.g() <= 255);
        assertTrue(rgb.b() >= 0 && rgb.b() <= 255);
    }
}
