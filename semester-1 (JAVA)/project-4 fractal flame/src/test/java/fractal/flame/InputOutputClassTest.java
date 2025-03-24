package fractal.flame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InputOutputClassTest extends InputOutputClass {
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    InputOutputClass inputOutputClass = new InputOutputClass();

    public void setInput(String buffer) {
        ByteArrayInputStream in = new ByteArrayInputStream(buffer.getBytes());
        System.setIn(in);

        inputOutputClass.setScanner(new Scanner(System.in));
    }

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));

    }

    @Test
    public void generateImageByInput() {
        setInput("1920\n1080\n100\n100\n1\n7\n1\n1\n6\n");
        inputOutputClass.getStart();

        File file = new File("fractalFlame.png");

        assertTrue(file.exists());
    }

    @Test
    public void generateImageWithWrongWidth() {
        setInput("-234\n1920\n1080\n100\n100\n1\n7\n1\n1\n6\n");
        inputOutputClass.getStart();

        File file = new File("fractalFlame.png");

        assertTrue(file.exists());
        assertTrue(outputStreamCaptor.toString().trim()
            .contains("Введенное число должно быть в диапазоне от 1 до 2147483647"));
    }

    @Test
    public void generateImageWithWrongHeight() {
        setInput("1920\ndsfa\n1080\n100\n100\n1\n7\n1\n1\n6\n");
        inputOutputClass.getStart();

        File file = new File("fractalFlame.png");

        assertTrue(file.exists());
        assertTrue(outputStreamCaptor.toString().trim()
            .contains("Вы ввели не целое число!"));
    }

    @Test
    public void generateImageWithWrongPoints() {
        setInput("1920\n1080\nabc\n100\n100\n1\n7\n1\n1\n6\n");
        inputOutputClass.getStart();

        File file = new File("fractalFlame.png");

        assertTrue(file.exists());
        assertTrue(outputStreamCaptor.toString().trim()
            .contains("Вы ввели не целое число!"));
    }

    @Test
    public void generateImageWithWrongIterNum() {
        setInput("-234\n1920\n1080\n100\nab\n100\n1\n7\n1\n1\n6\n");
        inputOutputClass.getStart();

        File file = new File("fractalFlame.png");

        assertTrue(file.exists());
        assertTrue(outputStreamCaptor.toString().trim()
            .contains("Вы ввели не целое число!"));
    }

    @Test
    public void generateImageWithWrongGenChoice() {
        setInput("-234\n1920\n1080\n100\n100\n4\n1\n7\n1\n1\n6\n");
        inputOutputClass.getStart();

        File file = new File("fractalFlame.png");

        assertTrue(file.exists());
        assertTrue(outputStreamCaptor.toString().trim()
            .contains("Введенное число должно быть в диапазоне от 1 до 2"));
    }

    @Test
    public void generateImageWithWrongThreadNumber() {
        setInput("-234\n1920\n1080\n100\n100\n1\n7\n1\n1\n-4\n6\n");
        inputOutputClass.getStart();

        File file = new File("fractalFlame.png");

        assertTrue(file.exists());
        assertTrue(outputStreamCaptor.toString().trim()
            .contains("Введенное число должно быть в диапазоне от 1 до 2147483647"));
    }

}
