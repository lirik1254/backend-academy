package maze.test.generator;

import maze.generator.DfsMazeGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashSet;

public class DfsGeneratorTest {
    @Test
    public void generate() {
        String[][] emptyMaze = new String[][]{
            {"   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   "},
            {"   ", "   ", "   ",  "   ", "   ", "   ", "   ", "   ", "   "},
            {"   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   "},
            {"   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   "},
            {"   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   "},
            {"   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   "},
            {"   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   "},
            {"   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   "},
            {"   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   "}
        };

        DfsMazeGenerator dfsMazeGenerator = new DfsMazeGenerator(9, 9);
        String[][] generatedMaze = dfsMazeGenerator.getMaze();

        Assertions.assertNotEquals(Arrays.deepToString(generatedMaze), Arrays.deepToString(emptyMaze));
        Assertions.assertEquals(emptyMaze.length, generatedMaze.length);
        Assertions.assertEquals(emptyMaze[0].length, generatedMaze[0].length);

        HashSet<String> usedElementsGenerated = new HashSet<>();
        Arrays.stream(generatedMaze).forEach(s -> usedElementsGenerated.addAll(Arrays.asList(s)));

        HashSet<String> usedElementsEmpty = new HashSet<>();
        Arrays.stream(emptyMaze).forEach(s -> usedElementsEmpty.addAll(Arrays.asList(s)));

        Assertions.assertNotEquals(usedElementsEmpty.size(), usedElementsGenerated.size());
        Assertions.assertEquals(usedElementsGenerated.size(), 3);

    }
}
