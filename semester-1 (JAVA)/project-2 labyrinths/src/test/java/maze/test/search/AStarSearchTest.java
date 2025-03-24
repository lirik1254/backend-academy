package maze.test.search;

import maze.search.AStarSearch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

public class AStarSearchTest {
    @Test
    public void pathExistTest() {

        String[][] maze = new String[][]{
            {"   ", "   ", "   ", "   ", " ■ ", "   ", "   ", "   ", "   "},
            {"   ", "   ", "   ",  " ■ ", " ■ ", " ■ ", "   ", "   ", " ■ "},
            {"   ", " ■ ", "   ", "   ", " ■ ", " ■ ", "   ", " ■ ", " ■ "},
            {" ■ ", " ■ ", " ■ ", "   ", "   ", "   ", "   ", " ■ ", "   "},
            {"   ", "   ", "   ", "   ", " ■ ", "   ", "   ", " ■ ", "   "},
            {"   ", "   ", " ■ ", " ■ ", " ■ ", "   ", " ■ ", " ■ ", "   "},
            {"   ", "   ", " ■ ", "   ", "   ", "   ", "   ", "   ", " ■ "},
            {" ■ ", "   ", "   ", "   ", " ■ ", " ■ ", "   ", "   ", "   "},
            {"   ", " ■ ", "   ", "   ", " ■ ", "   ", " ■ ", "   ", "   "}
        };

        String[][] mazePath = new String[][]{
            {" * ", " * ", "   ", "   ", " ■ ", "   ", "   ", "   ", "   "},
            {"   ", " * ", " * ",  " ■ ", " ■ ", " ■ ", "   ", "   ", " ■ "},
            {"   ", " ■ ", " * ", " * ", " ■ ", " ■ ", "   ", " ■ ", " ■ "},
            {" ■ ", " ■ ", " ■ ", " * ", " * ", " * ", "   ", " ■ ", "   "},
            {"   ", "   ", "   ", "   ", " ■ ", " * ", "   ", " ■ ", "   "},
            {"   ", "   ", " ■ ", " ■ ", " ■ ", " * ", " ■ ", " ■ ", "   "},
            {"   ", "   ", " ■ ", "   ", "   ", " * ", " * ", "   ", " ■ "},
            {" ■ ", "   ", "   ", "   ", " ■ ", " ■ ", " * ", " * ", " * "},
            {"   ", " ■ ", "   ", "   ", " ■ ", "   ", " ■ ", "   ", " * "}
        };

        int xStart = 0;
        int yStart = 0;
        int xFinish = 8;
        int yFinish = 8;

        AStarSearch aStarSearch = new AStarSearch();

        maze = aStarSearch.search(maze, yStart, xStart, yFinish, xFinish);

        Assertions.assertEquals(Arrays.deepToString(maze), Arrays.deepToString(mazePath));
    }

    @Test
    public void pathNoExistTest() {

        String[][] maze = new String[][]{
            {"   ", "   ", "   ", "   ", " ■ ", "   ", "   ", "   ", "   "},
            {"   ", "   ", "   ",  " ■ ", " ■ ", " ■ ", "   ", "   ", " ■ "},
            {" ■ ", " ■ ", " ■ ", " ■ ", " ■ ", " ■ ", " ■ ", " ■ ", " ■ "},
            {" ■ ", " ■ ", " ■ ", "   ", "   ", "   ", "   ", " ■ ", "   "},
            {"   ", "   ", "   ", "   ", " ■ ", "   ", "   ", " ■ ", "   "},
            {"   ", "   ", " ■ ", " ■ ", " ■ ", "   ", " ■ ", " ■ ", "   "},
            {"   ", "   ", " ■ ", "   ", "   ", "   ", "   ", "   ", " ■ "},
            {" ■ ", "   ", "   ", "   ", " ■ ", " ■ ", "   ", "   ", "   "},
            {"   ", " ■ ", "   ", "   ", " ■ ", "   ", " ■ ", "   ", "   "}
        };

        String[][] mazePath = new String[][]{
            {"   ", "   ", "   ", "   ", " ■ ", "   ", "   ", "   ", "   "},
            {"   ", "   ", "   ",  " ■ ", " ■ ", " ■ ", "   ", "   ", " ■ "},
            {" ■ ", " ■ ", " ■ ", " ■ ", " ■ ", " ■ ", " ■ ", " ■ ", " ■ "},
            {" ■ ", " ■ ", " ■ ", "   ", "   ", "   ", "   ", " ■ ", "   "},
            {"   ", "   ", "   ", "   ", " ■ ", "   ", "   ", " ■ ", "   "},
            {"   ", "   ", " ■ ", " ■ ", " ■ ", "   ", " ■ ", " ■ ", "   "},
            {"   ", "   ", " ■ ", "   ", "   ", "   ", "   ", "   ", " ■ "},
            {" ■ ", "   ", "   ", "   ", " ■ ", " ■ ", "   ", "   ", "   "},
            {"   ", " ■ ", "   ", "   ", " ■ ", "   ", " ■ ", "   ", "   "}
        };

        int xStart = 0;
        int yStart = 0;
        int xFinish = 8;
        int yFinish = 8;

        AStarSearch aStarSearch = new AStarSearch();

        maze = aStarSearch.search(maze, yStart, xStart, yFinish, xFinish);

        Assertions.assertEquals(Arrays.deepToString(maze), Arrays.deepToString(mazePath));
    }
}
