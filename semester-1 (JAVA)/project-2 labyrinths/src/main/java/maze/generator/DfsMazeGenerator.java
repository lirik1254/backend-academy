package maze.generator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import maze.Point;

public class DfsMazeGenerator implements Generator {

    private final int width;
    private final int height;
    private final boolean[][] visited;
    private final String[][] maze;

    public DfsMazeGenerator(int height, int width) {
        this.width = width;
        this.height = height;
        this.visited = new boolean[height][width];
        this.maze = new String[height][width];

        // Инициализация лабиринта стенами
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maze[y][x] = GeneratorUtils.WALL; // Стена
            }
        }

        generateMaze();
    }

    private void generateMaze() {

        int startX = width == 1 ? 0 : GeneratorUtils.RAND.nextInt(0, width / 2) * 2 + 1;
        int startY = height == 1 ? 0 : GeneratorUtils.RAND.nextInt(0, height / 2) * 2 + 1;
        ArrayDeque<Point> deque = new ArrayDeque<>();
        deque.push(new Point(startX, startY));
        visited[startY][startX] = true;

        while (!deque.isEmpty()) {
            Point current = deque.pop();
            int x = current.x();
            int y = current.y();
            maze[y][x] = GeneratorUtils.EMPTY;

            if (GeneratorUtils.RAND.nextBoolean()) {
                maze[y][x] = GeneratorUtils.SWAMP; // Ухудшающая поверхность
            }

            List<int[]> neighbors = new ArrayList<>();

            // Проверяем соседние клетки
            if (x > 1 && !visited[y][x - 2]) {
                neighbors.add(new int[]{x - 2, y});
            }
            if (x < width - 2 && !visited[y][x + 2]) {
                neighbors.add(new int[] {x + 2, y});
            }
            if (y > 1 && !visited[y - 2][x]) {
                neighbors.add(new int[] {x, y - 2});
            }
            if (y < height - 2 && !visited[y + 2][x]) {
                neighbors.add(new int[]{x, y + 2});
            }

            Collections.shuffle(neighbors);

            for (int[] neighbor : neighbors) {
                int nx = neighbor[0];
                int ny = neighbor[1];

                // Удаляем стену между текущей и соседней клетками
                maze[(y + ny) / 2][(x + nx) / 2] = GeneratorUtils.EMPTY;
                deque.push(new Point(nx, ny)); // Добавляем соседнюю клетку в стек
                visited[ny][nx] = true; // Помечаем соседнюю клетку как посещенную
            }
        }
    }

    public String[][] getMaze() {
        return maze;
    }

}
