package maze.search;


import java.util.Comparator;
import java.util.PriorityQueue;
import maze.Point;


public class AStarSearch extends BaseSearch {

    @Override
    public String[][] search(String[][] maze, int startY, int startX, int finishY, int finishX) {
        initialize(maze, startY, startX);

        PriorityQueue<Point> q = new PriorityQueue<>(Comparator.comparingDouble(Point::f));
        q.add(new Point(startX, startY, Math.sqrt(Math.pow(finishX - startX, 2) + Math.pow(finishY - startY, 2))));

        while (!q.isEmpty()) {
            Point f = q.poll();
            processNeighbors(f, finishX, finishY, new QueueHandler() {
                @Override
                public double calculateHeuristic(int tx, int ty, int finishX, int finishY) {
                    return Math.sqrt(Math.pow(finishX - tx, 2) + Math.pow(finishY - ty, 2));
                }

                @Override
                public void add(Point p) {
                    q.add(p);
                }
            });
        }

        SearchUtils.makePath(aList, dist, finishY, finishX, from);
        return aList;
    }
}
