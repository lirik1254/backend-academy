package fractal.flame;

import fractal.flame.transformations.RandomTransform;
import fractal.flame.utils.Point;
import fractal.flame.utils.PointUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImageCreationTest {

    public static int xRes = 1920;
    public static int yRes = 1080;
    static List<List<Point>> pixels = new ArrayList<>();
    static Render render = new Render();
    static String path = "fractalFlame.png";

    @BeforeAll
    public static void initPointList() {
        pixels = render.render(100, 5, 100, xRes, yRes, true, PointUtils.getAffineTransformationList(5),
            RandomTransform.getRandomTransformation(), 6);
        createFile();
    }

    @AfterAll
    public static void deleteFile() {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void createFile() {
        CreateImage image = new CreateImage();
        image.createImage(pixels, "fractalFlame.png");
    }

    @Test
    public void createImageTest() {
        File file = new File(path);
        assertTrue(file.exists());
    }

    @Test
    public void sizeImageTest() {
        File file = new File(path);
        assertTrue(file.length() > 0);
    }

    @Test
    public void widthHeightImageTest() throws IOException {
        File file = new File(path);
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        assertEquals(xRes, width);
        assertEquals(yRes, height);
    }
}
