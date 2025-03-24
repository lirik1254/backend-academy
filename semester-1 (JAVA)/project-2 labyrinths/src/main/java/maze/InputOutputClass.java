package maze;

import java.util.Objects;
import java.util.Scanner;
import maze.generator.DfsMazeGenerator;
import maze.generator.Generator;
import maze.generator.GeneratorUtils;
import maze.generator.PrimaMazeGenerator;
import maze.search.AStarSearch;
import maze.search.BaseSearch;
import maze.search.BfsSearch;


public class InputOutputClass {

    Scanner in = new Scanner(System.in);

    public static final String CHOOSE_GEN_ALG = """
        Привет! Выбери, каким алгоритмом ты хочешь сгенерировать лабиринт
        1. Алгоритм поиска в глубину
        2. Алгоритм Прима""";

    public static final String CHOOSE_SEARCH_ALG = """
        Выбери, каким алгоритмом ты хочешь найти путь от точки А до точки B
        1. Алгоритм поиска в ширину
        2. Алгоритм A*""";

    public static final String FINISH_WORD = """
        Круто, да? Что дальше?
        1. Выход
        2. Начать заново""";

    public static final String CHOOSE_MESSAGE = "\nВведите либо 1, либо 2: ";

    public static final int MAX_HEIGHT_WEIGHT_SIZE = 50;

    public static final int MIN_HEIGHT_WEIGHT_SIZE = 1;

    Generator generatorAlg;

    BaseSearch searchAlg;

    @SuppressWarnings("RegexpSinglelineJava")
    public int getNumInput(int leftBorder, int rightBorder, String message) {

        System.out.print(message);

        int someIntValue;

        while (true) {
            if (in.hasNextInt()) {
                someIntValue = in.nextInt();
                in.nextLine();
                if (someIntValue < leftBorder || someIntValue > rightBorder) {
                    System.out.printf("Введенное число должно быть в диапазоне от %d до %d\n\n%s\n",
                        leftBorder, rightBorder, message);
                } else {
                    return someIntValue;
                }
            } else {
                in.nextLine();
                System.out.printf("Вы ввели не число!\n\n%s\n", message);
            }
        }
    }

    public boolean checkPoint(int x, int y, String[][] maze) {
        return x < maze[0].length && y < maze.length && x >= 0 && y >= 0
            && !Objects.equals(maze[y][x], " ■ ");
    }

    @SuppressWarnings("RegexpSinglelineJava")
    public String[][] getMazeByChoosingAlg() {
        System.out.println(CHOOSE_GEN_ALG);
        int alg = getNumInput(1, 2, CHOOSE_MESSAGE);

        int y  = getNumInput(MIN_HEIGHT_WEIGHT_SIZE, MAX_HEIGHT_WEIGHT_SIZE,
            "\nВведите высоту лабиринта (целое число от 1 до 50): ");
        int x = getNumInput(MIN_HEIGHT_WEIGHT_SIZE, MAX_HEIGHT_WEIGHT_SIZE,
            "\nВведите ширину лабиринта (целое число от 1 до 50): ");

        if (alg == 1) {
            generatorAlg = new DfsMazeGenerator(y, x);
            System.out.println("\nГенерация лабиринта алгоритмом поиска в глубину: \n");
        } else {
            generatorAlg = new PrimaMazeGenerator(y, x);
            System.out.println("\nГенерация лабиринта алгоритмом Прима: \n");
        }

        String[][] maze = generatorAlg.getMaze();
        GeneratorUtils.drawMaze(maze);
        return maze;
    }

    @SuppressWarnings({"RegexpSinglelineJava", "MissingSwitchDefault"})
    public void printPathByPoints(int height, int width, String[][] maze) {
        System.out.println(CHOOSE_SEARCH_ALG);

        int alg = getNumInput(1, 2, CHOOSE_MESSAGE);

        int startX;
        int startY;
        int finishX;
        int finishY;

        String obstacleMessage = "\nВ этой точке препятствие!\n";

        while (true) {
            startX = getNumInput(0, width - 1, "\nВведите координату x стартовой точки: ");
            startY = getNumInput(0, height - 1, "\nВведите координату y стартовой точки: ");
            if (!checkPoint(startX, startY, maze)) {
                System.out.println(obstacleMessage);
                continue;
            }
            break;
        }

        while (true) {
            finishX = getNumInput(0, width - 1, "\nВведите координату x конечной точки: ");
            finishY = getNumInput(0, height - 1, "\nВведите координату y конечной точки: ");
            if (!checkPoint(finishX, finishY, maze)) {
                System.out.println(obstacleMessage);
                continue;
            }
            break;
        }

        if (alg == 1) {
            searchAlg = new BfsSearch();
            System.out.printf("\nПоиск пути методом поиска в ширину от точки (x:%d, y:%d} до точки {x:%d, y:%d}\n\n",
                startX, startY, finishX, finishY);
        } else {
            searchAlg = new AStarSearch();
            System.out.printf("\nПоиск пути методом A* от точки (x:%d, y:%d} до точки {x:%d, y:%d}\n\n",
                startX, startY, finishX, finishY);
        }

        String[][] outputMaze = searchAlg.search(maze, startY, startX, finishY, finishX);

        GeneratorUtils.drawMaze(outputMaze);
    }

    @SuppressWarnings("RegexpSinglelineJava")
    public void startProgram() {
        String[][] maze = getMazeByChoosingAlg();
        System.out.println();
        printPathByPoints(maze.length, maze[0].length, maze);

        System.out.println();
        System.out.println(FINISH_WORD);
        int ans = getNumInput(1, 2, CHOOSE_MESSAGE);
        System.out.println();

        if (ans == 2) {
            startProgram();
        }
    }

    public void setScanner(Scanner scanner) {
        in = scanner;
    }
}
