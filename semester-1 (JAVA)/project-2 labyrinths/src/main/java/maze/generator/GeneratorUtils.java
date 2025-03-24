package maze.generator;

import java.util.ArrayList;
import java.util.Random;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import maze.Cell;
import maze.Point;


@UtilityClass
@Slf4j
public class GeneratorUtils {

    public static final Random RAND = new Random();

    public static final int INF = Integer.MAX_VALUE;

    public static final String SWAMP = " □ ";

    public static final String EMPTY = "   ";

    public static final String WALL = " ■ ";

    /**
     * A method for creating a maze consisting entirely of walls */
    public Cell[][] getAllWallsMap(int height, int width) {

        Cell[][] map = new Cell[height][width];

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = new Cell(i, j, Surface.WALL);
            }
        }

        return map;
    }

    /** Transforms a Cell-type maze into a two-dimensional array of strings
     * (for identical return values using DFS and Prima methods)
     */
    public String[][] cellToStringList(Cell[][] map) {

        String[][] stringMap = new String[map.length][map[0].length];

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                stringMap[i][j] = switch (map[i][j].surface()) {
                    case EMPTY -> GeneratorUtils.EMPTY;
                    case WALL -> GeneratorUtils.WALL;
                    case SWAMP -> GeneratorUtils.SWAMP;
                };
            }
        }
        log.info("settext");
        return stringMap;
    }

    @SuppressWarnings({"RegexpSinglelineJava", "MagicNumber", "MultipleStringLiterals"})
    public void drawMaze(String[][] maze) {
        for (int i = 0; i < maze[0].length; i++) {
            if (i == 0) {
                System.out.print("    0 ");
            } else {
                System.out.printf(i > 9 ? " %d" : " %d ", i);
            }

        }
        System.out.println();
        for (int i = 0; i < maze.length; i++) {
            System.out.printf(i > 9 ? " %d" : " %d ", i);
            for (int z = 0; z < maze[i].length; z++) {
                if (z == maze[i].length - 1) {
                    System.out.println(maze[i][z]);
                } else {
                    System.out.print(maze[i][z]);
                }
            }
        }
    }

    /** Fills the list of distances from the starting point with infinity
     */
    public ArrayList<ArrayList<Integer>> getInfDistList(int h, int w) {
        ArrayList<ArrayList<Integer>> dist = new ArrayList<>();

        for (int i = 0; i < h; i++) {
            dist.add(i, new ArrayList<>());
            for (int j = 0; j < w; j++) {
                dist.get(i).add(INF);
            }
        }

        return dist;
    }


    /** Fills in the list of paths
     */
    public ArrayList<ArrayList<Point>> getFillFromList(int h, int w) {
        ArrayList<ArrayList<Point>> from = new ArrayList<>();

        for (int i = 0; i < h; i++) {
            from.add(new ArrayList<>());
            for (int j = 0; j < w; j++) {
                from.get(i).add(new Point(-1, -1));
            }
        }

        return from;
    }

}
