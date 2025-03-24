package maze.generator;

import java.util.ArrayList;
import java.util.List;
import maze.Cell;
import maze.Direction;
import maze.Point;


public class PrimaMazeGenerator implements Generator {

    private Cell[][] map;
    private final int width;
    private final int height;

    public PrimaMazeGenerator(int height, int width) {
        this.width = width;
        this.height = height;
        map = GeneratorUtils.getAllWallsMap(this.height, this.width);
        generateMaze();
    }

    @SuppressWarnings({"CyclomaticComplexity", "MissingSwitchDefault"})
    private void generateMaze() {

        int x = GeneratorUtils.RAND.nextInt(0, height / 2) * 2 + 1;
        int y = GeneratorUtils.RAND.nextInt(0, width / 2) * 2 + 1;

        map[x][y].surface(Surface.EMPTY);

        ArrayList<Point> toCheck = new ArrayList<>();
        if (y - 2 >=  0) {
            toCheck.add(new Point(x, y - 2));
        }
        if (y + 2 < width) {
            toCheck.add(new Point(x, y + 2));
        }
        if (x - 2 >= 0) {
            toCheck.add(new Point(x - 2, y));
        }
        if (x + 2 < height) {
            toCheck.add(new Point(x + 2, y));
        }

        while (!toCheck.isEmpty()) {
            int index = GeneratorUtils.RAND.nextInt(0, toCheck.size());
            Point cell = toCheck.get(index);

            x = cell.x();
            y = cell.y();

            map[x][y].surface(Surface.EMPTY);
            toCheck.remove(index);

            if (GeneratorUtils.RAND.nextBoolean()) {
                map[x][y].surface(Surface.SWAMP); // Ухудшающая поверхность
            }

            ArrayList<Direction> directions = new ArrayList<>(List.of(Direction.NORTH, Direction.EAST,
                Direction.WEST, Direction.SOUTH));

            while (!directions.isEmpty()) {

                int dirIndex = GeneratorUtils.RAND.nextInt(0, directions.size());

                switch (directions.get(dirIndex)) {
                    case Direction.NORTH -> {
                        if (y - 2 >= 0 && (map[x][y - 2].surface() == Surface.EMPTY
                            || map[x][y - 2].surface() == Surface.SWAMP)) {
                            map[x][y - 1].surface(Surface.EMPTY);
                            directions.clear();
                        }
                    }

                    case Direction.SOUTH -> {
                        if (y + 2 < width && (map[x][y + 2].surface() == Surface.EMPTY
                            || map[x][y + 2].surface() == Surface.SWAMP)) {
                            map[x][y + 1].surface(Surface.EMPTY);
                            directions.clear();
                        }
                    }

                    case Direction.EAST -> {
                        if (x - 2 >= 0 && (map[x - 2][y].surface() == Surface.EMPTY
                            || map[x - 2][y].surface() == Surface.SWAMP)) {
                            map[x - 1][y].surface(Surface.EMPTY);
                            directions.clear();
                        }
                    }

                    case Direction.WEST -> {
                        if (x + 2 < height && (map[x + 2][y].surface() == Surface.EMPTY
                            || map[x + 2][y].surface() == Surface.SWAMP)) {
                            map[x + 1][y].surface(Surface.EMPTY);
                            directions.clear();
                        }
                    }
                }

                if (!directions.isEmpty()) {
                    directions.remove(dirIndex);
                }
            }

            if (y - 2 >= 0 && map[x][y - 2].surface() == Surface.WALL) {
                toCheck.add(new Point(x, y - 2));
            }

            if (y + 2 < width && map[x][y + 2].surface() == Surface.WALL) {
                toCheck.add(new Point(x, y + 2));
            }

            if (x - 2 >= 0 && map[x - 2][y].surface() == Surface.WALL) {
                toCheck.add(new Point(x - 2, y));
            }

            if (x + 2 < height && map[x + 2][y].surface() == Surface.WALL) {
                toCheck.add(new Point(x + 2, y));
            }
        }
    }

    @Override
    public String[][] getMaze() {
        return GeneratorUtils.cellToStringList(map);
    }

}
