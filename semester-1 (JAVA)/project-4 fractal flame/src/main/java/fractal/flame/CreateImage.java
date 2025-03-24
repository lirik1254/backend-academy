package fractal.flame;

import fractal.flame.utils.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@NoArgsConstructor
public class CreateImage {
    public void createImage(List<List<Point>> renderList, String path) {
        BufferedImage fractalFlameImage = generateImage(renderList);
        saveBufferedImage(fractalFlameImage, path);
    }

    private void saveBufferedImage(BufferedImage bufferedImage, String path) {
        File output = new File(path);
        try {
            ImageIO.write(bufferedImage, "png", output);
            log.debug("The image was saved successfully as {}", path);
        } catch (IOException e) {
            log.error("Error while save image");
        }
    }

    @SuppressWarnings("MagicNumber")
    private BufferedImage generateImage(List<List<Point>> renderList) {
        int height = renderList.size();
        int width = renderList.getFirst().size();
        BufferedImage bufferedImage = new BufferedImage(renderList.getFirst().size(), renderList.size(),
            BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                short r = renderList.get(y).get(x).rgb().r();
                short g = renderList.get(y).get(x).rgb().g();
                short b = renderList.get(y).get(x).rgb().b();

                int rgb = (r << 16) | (g << 8) | b;
                bufferedImage.setRGB(x, y, rgb);
            }
        }
        return bufferedImage;
    }

}
