package maze.search;

import java.util.ArrayList;
import lombok.experimental.UtilityClass;
import maze.Point;
import maze.generator.GeneratorUtils;



@UtilityClass
public class SearchUtils {

    /** Draws a path along the points specified in the maze
     */
    public void makePath(String[][] aList, ArrayList<ArrayList<Integer>> dist,
        int finishY, int finishX, ArrayList<ArrayList<Point>> from) {
        if (dist.get(finishY).get(finishX) != GeneratorUtils.INF) {
            int y = finishY;
            int x = finishX;
            while (y != -1 && x != -1) {
                aList[y][x] = " * ";
                Point point = from.get(y).get(x);
                y = point.y();
                x = point.x();
            }
        }
    }

    public String[][] deepClone(String[][] maze) {
        String[][] clone = new String[maze.length][maze[0].length];
        for (int i = 0; i < clone.length; i++) {
            clone[i] = maze[i].clone();
        }

        return clone;
     }
}
