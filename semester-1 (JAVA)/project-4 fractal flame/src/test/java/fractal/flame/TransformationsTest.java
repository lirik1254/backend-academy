package fractal.flame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import fractal.flame.transformations.RandomTransform;
import fractal.flame.transformations.Transform;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransformationsTest {

    List<Transform> randomTransforms = RandomTransform.getRandomTransformation();

    @RepeatedTest(50)
    public void getRandomTransformListSizeTest() {
        assertTrue(!randomTransforms.isEmpty() && randomTransforms.size() <= RandomTransform.TRANSFORMATIONS.size());
    }

    @Test
    public void getRandomTransformListContentTest() {
        randomTransforms.forEach(transform -> {
            assertTrue(RandomTransform.TRANSFORMATIONS.stream()
                    .anyMatch(clazz -> clazz.isInstance(transform)),
                "Transform not found in available transforms: " + transform.getClass().getSimpleName());
        });
    }
}
