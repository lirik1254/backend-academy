package maze.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import maze.Point;
import maze.generator.GeneratorUtils;


public abstract class BaseSearch {
    protected int h;
    protected int w;
    protected String[][] aList;
    protected ArrayList<ArrayList<Integer>> dist;
    protected ArrayList<ArrayList<Point>> from;
    protected ArrayList<Integer> dy = new ArrayList<>(List.of(-1, 0, 1, 0));
    protected ArrayList<Integer> dx = new ArrayList<>(List.of(0, 1, 0, -1));

    public void initialize(String[][] maze, int startY, int startX) {
        h = maze.length;
        w = maze[0].length;
        aList = SearchUtils.deepClone(maze);
        dist = GeneratorUtils.getInfDistList(h, w);
        from = GeneratorUtils.getFillFromList(h, w);
        dist.get(startY).set(startX, 0);
    }

    protected void processNeighbors(Point f, int finishX, int finishY, QueueHandler q) {
        int y = f.y();
        int x = f.x();

        for (int d = 0; d < dy.size(); d++) {
            int ty = (y + dy.get(d) != h) && (y + dy.get(d) != -1) ? y + dy.get(d) : y;
            int tx = (x + dx.get(d) != w) && (x + dx.get(d) != -1) ? x + dx.get(d) : x;

            double moveCost = Objects.equals(aList[ty][tx], GeneratorUtils.SWAMP) ? 2 : 1;
            double heuristic = q.calculateHeuristic(tx, ty, finishX, finishY) + dist.get(y).get(x) + 1 + moveCost;

            if (0 <= ty && ty < h && 0 <= tx && tx < w && !Objects.equals(aList[ty][tx], GeneratorUtils.WALL)
                && dist.get(ty).get(tx) > dist.get(y).get(x) + 1) {
                    dist.get(ty).set(tx, dist.get(y).get(x) + 1);
                    from.get(ty).set(tx, new Point(x, y));
                    q.add(new Point(tx, ty, heuristic));
            }
        }
    }

    //        for (int i = 0; i < resourcesTop3.size(); i++) {
//            Map.Entry<String, Integer> entry = resourcesTop3.get(i);
//            requestedResourcesTop.append(String.format("| %s | %d |\n", entry.getKey(), entry.getValue()));
//
//            entry = responseTop3.get(i);
//            responseCodesTop.append(String.format("| %s | %s | %d |\n", entry.getKey(),
//                textReportUtils.responseNotation.get(entry.getKey()), entry.getValue()));
//        }

    public abstract String[][] search(String[][] maze, int startY, int startX, int finishY, int finishX);

    protected interface QueueHandler {
        double calculateHeuristic(int tx, int ty, int finishX, int finishY);

        void add(Point p);
    }
}
