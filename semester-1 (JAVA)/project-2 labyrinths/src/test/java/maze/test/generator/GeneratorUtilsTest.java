package maze.test.generator;

import it.unimi.dsi.fastutil.Hash;
import maze.Cell;
import maze.Point;
import maze.generator.GeneratorUtils;
import maze.generator.Surface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class GeneratorUtilsTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void getAllWallsMapTest() {
        Cell[][] map = GeneratorUtils.getAllWallsMap(9, 9);

        Assertions.assertEquals(map.length, 9);
        Assertions.assertEquals(map[0].length, 9);

        HashSet<Surface> wallSet = new HashSet<>();
        Arrays.stream(map).forEach(s -> Arrays.stream(s).forEach(s1 -> wallSet.add(s1.surface())));

        Assertions.assertEquals(wallSet.size(), 1);
        Assertions.assertTrue(wallSet.contains(Surface.WALL));
    }

    @Test
    public void cellToStringListTest() {
        Cell[][] map = new Cell[][]{
            new Cell[]{
                new Cell(0,0, Surface.EMPTY),
                 new Cell(1, 0, Surface.SWAMP)},
            new Cell[]{
                new Cell(1, 1, Surface.WALL),
                new Cell(1, 1, Surface.SWAMP)}};

        String[][] stringMap = GeneratorUtils.cellToStringList(map);

        Assertions.assertEquals(stringMap.length, 2);
        Assertions.assertEquals(stringMap[0].length, 2);

        Assertions.assertEquals(GeneratorUtils.SWAMP, stringMap[0][1]);
        Assertions.assertEquals(GeneratorUtils.EMPTY, stringMap[0][0]);
        Assertions.assertEquals(GeneratorUtils.SWAMP, stringMap[1][1]);
        Assertions.assertEquals(GeneratorUtils.WALL, stringMap[1][0]);

    }

    @Test
    public void drawMazeTest() {
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

        String stringFormMaze = """
    0  1  2  3  4  5  6  7  8 \r
 0              ■             \r
 1           ■  ■  ■        ■ \r
 2     ■        ■  ■     ■  ■ \r
 3  ■  ■  ■              ■    \r
 4              ■        ■    \r
 5        ■  ■  ■     ■  ■    \r
 6        ■                 ■ \r
 7  ■           ■  ■          \r
 8     ■        ■     ■       \r
""";

        GeneratorUtils.drawMaze(maze);
        Assertions.assertEquals(stringFormMaze.replace("\n", "").replace("\r", ""),
            outputStreamCaptor.toString().replace("\n", "").replace("\r", ""));

    }

    @Test
    public void getInfDistListTest() {
        ArrayList<ArrayList<Integer>> infDistLIst = GeneratorUtils.getInfDistList(4, 8);

        Assertions.assertEquals(infDistLIst.size(), 4);
        Assertions.assertEquals(infDistLIst.getFirst().size(), 8);

        HashSet<Integer> infDistListContent = new HashSet<>();
        infDistLIst.forEach(infDistListContent::addAll);

        Assertions.assertEquals(infDistListContent.size(), 1);
        Assertions.assertTrue(infDistListContent.contains(GeneratorUtils.INF));
    }

    @Test
    public void getFillFromListTest() {
        ArrayList<ArrayList<Point>> from = GeneratorUtils.getFillFromList(4, 8);

        Assertions.assertEquals(from.size(), 4);
        Assertions.assertEquals(from.getFirst().size(), 8);

        HashSet<Point> fromContent = new HashSet<>();
        from.forEach(fromContent::addAll);

        Assertions.assertEquals(fromContent.size(), 1);
    }
}
