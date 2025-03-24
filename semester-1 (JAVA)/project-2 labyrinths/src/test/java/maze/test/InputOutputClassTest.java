package maze.test;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import maze.InputOutputClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;


@Slf4j
public class InputOutputClassTest {
    String[][] maze = new String[][]{
        {"   ", "   ", "   ", "   ", " ■ ", "   ", "   ", "   ", "   "},
        {"   ", "   ", "   ", " ■ ", " ■ ", " ■ ", "   ", "   ", " ■ "},
        {"   ", " ■ ", "   ", "   ", " ■ ", " ■ ", "   ", " ■ ", " ■ "},
        {" ■ ", " ■ ", " ■ ", "   ", "   ", "   ", "   ", " ■ ", "   "},
        {"   ", "   ", "   ", "   ", " ■ ", "   ", "   ", " ■ ", "   "},
        {"   ", "   ", " ■ ", " ■ ", " ■ ", "   ", " ■ ", " ■ ", "   "},
        {"   ", "   ", " ■ ", "   ", "   ", "   ", "   ", "   ", " ■ "},
        {" ■ ", "   ", "   ", "   ", " ■ ", " ■ ", "   ", "   ", "   "},
        {"   ", " ■ ", "   ", "   ", " ■ ", "   ", " ■ ", "   ", "   "}
    };

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    InputOutputClass inputOutputClass = new InputOutputClass();

    public void setInput(String buffer) {
        ByteArrayInputStream in = new ByteArrayInputStream(buffer.getBytes());
        System.setIn(in);
        log.info("set");

        inputOutputClass.setScanner(new Scanner(System.in));
    }

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void getNumInputInBordersTest() {
        setInput("1\n");

        assertEquals(1, inputOutputClass.getNumInput(1, 3, "alo"));
    }

    @Test
    public void getNumInputOutBordersTest() {
        setInput("0\n2\n");

        assertEquals(2, inputOutputClass.getNumInput(1, 3, ""));
        assertEquals("Введенное число должно быть в диапазоне от 1 до 3", outputStreamCaptor.toString().trim());
    }

    @Test
    public void getNumInputWithAdditionalStringTest() {
        setInput("1\n");

        inputOutputClass.getNumInput(1, 3, "aloha");

        assertEquals("aloha", outputStreamCaptor.toString().trim());
    }

    @Test
    public void checkPointTest() {

        boolean falsePoint = inputOutputClass.checkPoint(4, 0, maze);
        Assertions.assertFalse(falsePoint);

        boolean truePoint = inputOutputClass.checkPoint(0, 0, maze);
        Assertions.assertTrue(truePoint);
    }

    @Test
    public void printMazeByChoosingAlgDfsTest() {
        setInput("1\n3\n10\n");

        String[][] maze = inputOutputClass.getMazeByChoosingAlg();

        Assertions.assertEquals(3, maze.length);
        Assertions.assertEquals(10, maze[0].length);

        HashSet<String> mazeContent = new HashSet<>();

        Arrays.stream(maze).forEach(s -> mazeContent.addAll(Arrays.asList(s)));

        Assertions.assertEquals(3, mazeContent.size());
    }

    @Test
    public void printMazeByChoosingAlgPrimaTest() {
        setInput("2\n3\n10\n");

        String[][] maze = inputOutputClass.getMazeByChoosingAlg();

        Assertions.assertEquals(3, maze.length);
        Assertions.assertEquals(10, maze[0].length);

        HashSet<String> mazeContent = new HashSet<>();

        Arrays.stream(maze).forEach(s -> mazeContent.addAll(Arrays.asList(s)));

        Assertions.assertEquals(3, mazeContent.size());
    }

    @Test
    public void printPathByPointsBfsTest() {
        setInput("1\n0\n0\n8\n8\n");

        inputOutputClass.printPathByPoints(maze.length, maze[0].length, maze);

        String output = """
            Выбери, каким алгоритмом ты хочешь найти путь от точки А до точки B
            1. Алгоритм поиска в ширину
            2. Алгоритм A*\n

            Введите либо 1, либо 2: \nВведите координату x стартовой точки: \nВведите координату y стартовой точки: \nВведите координату x конечной точки: \nВведите координату y конечной точки: \nПоиск пути методом поиска в ширину от точки (x:0, y:0} до точки {x:8, y:8}    0  1  2  3  4  5  6  7  8 \n
             0  *           ■             \n
             1  *  *  *  ■  ■  ■        ■ \n
             2     ■  *  *  ■  ■     ■  ■ \n
             3  ■  ■  ■  *  *  *     ■    \n
             4              ■  *     ■    \n
             5        ■  ■  ■  *  ■  ■    \n
             6        ■        *  *     ■ \n
             7  ■           ■  ■  *  *    \n
             8     ■        ■     ■  *  * \n
            """;

        Assertions.assertEquals(output.replace("\n", ""),
            outputStreamCaptor.toString().replace("\n", "").replace("\r", ""));
    }

    @Test
    public void printPathByPointsAStarTest() {
        setInput("2\n0\n0\n8\n8\n");

        inputOutputClass.printPathByPoints(maze.length, maze[0].length, maze);

        String output = """
            Выбери, каким алгоритмом ты хочешь найти путь от точки А до точки B
            1. Алгоритм поиска в ширину
            2. Алгоритм A*\n

            Введите либо 1, либо 2: \nВведите координату x стартовой точки: \nВведите координату y стартовой точки: \nВведите координату x конечной точки: \nВведите координату y конечной точки: \nПоиск пути методом A* от точки (x:0, y:0} до точки {x:8, y:8}\n\n    0  1  2  3  4  5  6  7  8 \n
             0  *  *        ■             \n
             1     *  *  ■  ■  ■        ■ \n
             2     ■  *  *  ■  ■     ■  ■ \n
             3  ■  ■  ■  *  *  *     ■    \n
             4              ■  *     ■    \n
             5        ■  ■  ■  *  ■  ■    \n
             6        ■        *  *     ■ \n
             7  ■           ■  ■  *  *  * \n
             8     ■        ■     ■     * \n
            """;

        Assertions.assertEquals(output.replace("\n", ""),
            outputStreamCaptor.toString().replace("\n", "").replace("\r", ""));
    }

    @Test
    public void printPathIncorrectPointTest() {

        setInput("2\n1\n3\n0\n0\n8\n8\n");

        inputOutputClass.printPathByPoints(maze.length, maze[0].length, maze);

        String output = """
            Выбери, каким алгоритмом ты хочешь найти путь от точки А до точки B
            1. Алгоритм поиска в ширину
            2. Алгоритм A*\n

            Введите либо 1, либо 2:\s
            Введите координату x стартовой точки:\s
            Введите координату y стартовой точки:\s
            В этой точке препятствие!
            \n

            Введите координату x стартовой точки:\s
            Введите координату y стартовой точки:\s
            Введите координату x конечной точки:\s
            Введите координату y конечной точки:\s
            Поиск пути методом A* от точки (x:0, y:0} до точки {x:8, y:8}

                0  1  2  3  4  5  6  7  8 \n
             0  *  *        ■             \n
             1     *  *  ■  ■  ■        ■ \n
             2     ■  *  *  ■  ■     ■  ■ \n
             3  ■  ■  ■  *  *  *     ■    \n
             4              ■  *     ■    \n
             5        ■  ■  ■  *  ■  ■    \n
             6        ■        *  *     ■ \n
             7  ■           ■  ■  *  *  * \n
             8     ■        ■     ■     * \n
            """;

        Assertions.assertEquals(output.replace("\n", ""),
            outputStreamCaptor.toString().replace("\n", "").replace("\r", ""));
    }

    @Test
    public void startProgramExitAfterEndTest() {

        InputOutputClass spyInputOutputClass = spy(inputOutputClass);

        doReturn(maze).when(spyInputOutputClass).getMazeByChoosingAlg();

        setInput("1\n0\n0\n8\n8\n1\n");
        spyInputOutputClass.setScanner(new Scanner(System.in));

        spyInputOutputClass.startProgram();

        String output = """
            \n
            Выбери, каким алгоритмом ты хочешь найти путь от точки А до точки B
            1. Алгоритм поиска в ширину
            2. Алгоритм A*\n

            Введите либо 1, либо 2:\s
            Введите координату x стартовой точки:\s
            Введите координату y стартовой точки:\s
            Введите координату x конечной точки:\s
            Введите координату y конечной точки:\s
            Поиск пути методом поиска в ширину от точки (x:0, y:0} до точки {x:8, y:8}    0  1  2  3  4  5  6  7  8 \n
             0  *           ■             \n
             1  *  *  *  ■  ■  ■        ■ \n
             2     ■  *  *  ■  ■     ■  ■ \n
             3  ■  ■  ■  *  *  *     ■    \n
             4              ■  *     ■    \n
             5        ■  ■  ■  *  ■  ■    \n
             6        ■        *  *     ■ \n
             7  ■           ■  ■  *  *    \n
             8     ■        ■     ■  *  * \n
            \n\nКруто, да? Что дальше?
            1. Выход
            2. Начать заново\n

            Введите либо 1, либо 2: \n
            """;

        Assertions.assertEquals(output.replace("\n", ""),
            outputStreamCaptor.toString().replace("\n", "").replace("\r", ""));
    }

    @Test
    public void startProgramAfreshAfterEndTest() {

        InputOutputClass spyInputOutputClass = spy(inputOutputClass);

        doReturn(maze).when(spyInputOutputClass).getMazeByChoosingAlg();

        setInput("1\n0\n0\n8\n8\n2\n2\n0\n0\n8\n8\n1\n");
        spyInputOutputClass.setScanner(new Scanner(System.in));

        spyInputOutputClass.startProgram();

        String output = """
            \n\nВыбери, каким алгоритмом ты хочешь найти путь от точки А до точки B
            1. Алгоритм поиска в ширину
            2. Алгоритм A*\n

            Введите либо 1, либо 2:\s
            Введите координату x стартовой точки:\s
            Введите координату y стартовой точки:\s
            Введите координату x конечной точки:\s
            Введите координату y конечной точки:\s
            Поиск пути методом поиска в ширину от точки (x:0, y:0} до точки {x:8, y:8}    0  1  2  3  4  5  6  7  8 \n
             0  *           ■             \n
             1  *  *  *  ■  ■  ■        ■ \n
             2     ■  *  *  ■  ■     ■  ■ \n
             3  ■  ■  ■  *  *  *     ■    \n
             4              ■  *     ■    \n
             5        ■  ■  ■  *  ■  ■    \n
             6        ■        *  *     ■ \n
             7  ■           ■  ■  *  *    \n
             8     ■        ■     ■  *  * \n
            \n
            Круто, да? Что дальше?
            1. Выход
            2. Начать заново\n

            Введите либо 1, либо 2: \n
            \n
            Выбери, каким алгоритмом ты хочешь найти путь от точки А до точки B
            1. Алгоритм поиска в ширину
            2. Алгоритм A*\n

            Введите либо 1, либо 2:\s
            Введите координату x стартовой точки:\s
            Введите координату y стартовой точки:\s
            Введите координату x конечной точки:\s
            Введите координату y конечной точки:\s
            Поиск пути методом A* от точки (x:0, y:0} до точки {x:8, y:8}

                0  1  2  3  4  5  6  7  8 \n
             0  *  *        ■             \n
             1     *  *  ■  ■  ■        ■ \n
             2     ■  *  *  ■  ■     ■  ■ \n
             3  ■  ■  ■  *  *  *     ■    \n
             4              ■  *     ■    \n
             5        ■  ■  ■  *  ■  ■    \n
             6        ■        *  *     ■ \n
             7  ■           ■  ■  *  *  * \n
             8     ■        ■     ■     * \n
            \n
            Круто, да? Что дальше?
            1. Выход
            2. Начать заново\n

            Введите либо 1, либо 2: \n
            """;

        Assertions.assertEquals(output.replace("\n", ""),
            outputStreamCaptor.toString().replace("\n", "").replace("\r", ""));
    }
}
