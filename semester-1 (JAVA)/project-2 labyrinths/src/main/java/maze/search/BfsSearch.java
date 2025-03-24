package maze.search;

import java.util.LinkedList;
import maze.Point;


public class BfsSearch extends BaseSearch {

    @Override
    public String[][] search(String[][] maze, int startY, int startX, int finishY, int finishX) {
        initialize(maze, startY, startX);

        LinkedList<Point> q = new LinkedList<>();
        q.push(new Point(startX, startY));

        while (!q.isEmpty()) {
            Point f = q.poll();
            processNeighbors(f, finishX, finishY, new QueueHandler() {
                @Override
                public double calculateHeuristic(int tx, int ty, int finishX, int finishY) {
                    return 0;
                }

                @Override
                public void add(Point p) {
                    q.push(p);
                }
            });
        }

        SearchUtils.makePath(aList, dist, finishY, finishX, from);
        return aList;
    }
}
