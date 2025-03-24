package fractal.flame.transformations;

import fractal.flame.utils.PointUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RandomTransform {
    public static final List<Class<? extends Transform>> TRANSFORMATIONS = List.of(
        DiskTransform.class,
        HearthTransform.class,
        LinearTransform.class,
        PolarTransform.class,
        SinusoidalTransform.class,
        SphericalTransform.class
    );

    @SuppressWarnings("checkstyle:MagicNumber")
    public static List<Transform> getRandomTransformation() {
        int count = PointUtils.rand.nextInt(1, TRANSFORMATIONS.size() - 2);
        ArrayList<Transform> transforms = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                Class<? extends Transform> clazz = TRANSFORMATIONS.get(PointUtils.rand.nextInt(TRANSFORMATIONS.size()));
                transforms.add(clazz.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException("Error creating transformation instance", e);
            }
        }
        return transforms;
    }
}
