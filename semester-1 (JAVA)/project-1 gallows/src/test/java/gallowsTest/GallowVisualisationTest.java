package gallowsTest;

import gallows.GallowVisualisation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GallowVisualisationTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void visualizeNotFullTest() {
        GallowVisualisation.visualisationByAttempts(GallowVisualisation.numberOfAttempts / 3);

        assertEquals("""
            ___________________________________
              |               |__________/      |
              |___ \\      /___|                 |
                    \\    /                      |
                     |  |                       |
                     |  |                     ____
                     |  |                    /    \\
                     |  |                    \\____/
                     |  |                       |
                     |  |                     / | \\
                     |  |                    /  |  \\
                     |  |                   /   |   \\
                     |  |                       |
                     |  |                       |
                     |  |""", outputStreamCaptor.toString().trim());
    }

    @Test
    public void visualizeFullAttemptsTest() {
        GallowVisualisation.visualisationByAttempts(GallowVisualisation.numberOfAttempts);

        assertEquals("", outputStreamCaptor.toString().trim());
    }

    @Test
    public void visualizeZeroAttemptsTest() {
        GallowVisualisation.visualisationByAttempts(0);

        assertEquals(GallowVisualisation.GALLOW.trim(), outputStreamCaptor.toString().trim());
    }

}
