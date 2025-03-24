package fractal.flame;

import fractal.flame.transformations.RandomTransform;
import fractal.flame.utils.Point;
import fractal.flame.utils.PointUtils;
import fractal.flame.utils.RGB;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CorrectionTest {

    @Test
    public void correctionChangeRgbTest() {
        Render render = new Render();
        List<List<Point>> pixels = render.render(1000, 7, 1, 30, 30, true,
            PointUtils.getAffineTransformationList(7), RandomTransform.getRandomTransformation(), 6);

        List<List<Point>> pixelsClone = getClone(pixels);

        Correction correction = new Correction();
        correction.correction(pixels);

        for (int i = 0; i < pixels.size(); i++) {
            for (int j = 0; j < pixels.getFirst().size(); j++) {
                Point afterCorrPoint = pixels.get(i).get(j);
                Point beforeCorrPoint = pixelsClone.get(i).get(j);
                if (afterCorrPoint.hitsNumber() > 1 && afterCorrPoint.normal() != 1.0) {
                    assertEquals(afterCorrPoint.x(), beforeCorrPoint.x());
                    assertEquals(afterCorrPoint.y(), beforeCorrPoint.y());
                    if (beforeCorrPoint.rgb().r() != 0) {
                        assertNotEquals(afterCorrPoint.rgb().r(), beforeCorrPoint.rgb().r());
                    }
                    if (beforeCorrPoint.rgb().g() != 0) {
                        assertNotEquals(afterCorrPoint.rgb().g(), beforeCorrPoint.rgb().g());
                    }
                    if (beforeCorrPoint.rgb().b() != 0) {
                        assertNotEquals(afterCorrPoint.rgb().b(), beforeCorrPoint.rgb().b());
                    }
                }
            }
        }
    }

    private List<List<Point>> getClone(List<List<Point>> pixels) {
        return pixels.stream()
            .map(row -> row.stream()
                .map(point -> new Point(
                    point.x(),
                    point.y(),
                    new RGB(point.rgb().r(), point.rgb().g(), point.rgb().b()),
                    0,
                    0
                ))
                .toList()
            )
            .toList();
    }
}
