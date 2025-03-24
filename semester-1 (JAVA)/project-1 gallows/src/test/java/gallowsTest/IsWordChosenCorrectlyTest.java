package gallowsTest;

import gallows.Category;
import gallows.DifficultyLevels;
import gallows.WordsGeneration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IsWordChosenCorrectlyTest {
    @Test
    public void getEasyAnimalsWordTest() {
        String easyAnimal = WordsGeneration.getRandomWordByDifficultyAndCategory(DifficultyLevels.EASY, Category.ANIMALS);
        Assertions.assertTrue(WordsGeneration.EASY_ANIMALS().contains(easyAnimal));
    }

    @Test
    public void getMediumAnimalWordTest() {
        String mediumAnimal = WordsGeneration.getRandomWordByDifficultyAndCategory(DifficultyLevels.MEDIUM, Category.ANIMALS);
        Assertions.assertTrue(WordsGeneration.MEDIUM_ANIMALS().contains(mediumAnimal));
    }

    @Test
    public void getHardAnimalWordTest() {
        String hardAnimal = WordsGeneration.getRandomWordByDifficultyAndCategory(DifficultyLevels.HARD, Category.ANIMALS);
        Assertions.assertTrue(WordsGeneration.HARD_ANIMALS().contains(hardAnimal));
    }

    @Test
    public void getEasyNatureWordTest() {
        String easyNature = WordsGeneration.getRandomWordByDifficultyAndCategory(DifficultyLevels.EASY, Category.NATURE);
        Assertions.assertTrue(WordsGeneration.EASY_NATURE().contains(easyNature));
    }

    @Test
    public void getMediumNatureWordTest() {
        String mediumNature = WordsGeneration.getRandomWordByDifficultyAndCategory(DifficultyLevels.MEDIUM, Category.NATURE);
        Assertions.assertTrue(WordsGeneration.MEDIUM_NATURE().contains(mediumNature));
    }

    @Test
    public void getHardNatureWordTest() {
        String hardNature = WordsGeneration.getRandomWordByDifficultyAndCategory(DifficultyLevels.HARD, Category.NATURE);
        Assertions.assertTrue(WordsGeneration.HARD_NATURE().contains(hardNature));
    }

    @Test
    public void getEasyFoodWordTest() {
        String easyFood = WordsGeneration.getRandomWordByDifficultyAndCategory(DifficultyLevels.EASY, Category.FOOD);
        Assertions.assertTrue(WordsGeneration.EASY_FOOD().contains(easyFood));
    }

    @Test
    public void getMediumFoodWordTest() {
        String mediumFood = WordsGeneration.getRandomWordByDifficultyAndCategory(DifficultyLevels.MEDIUM, Category.FOOD);
        Assertions.assertTrue(WordsGeneration.MEDIUM_FOOD().contains(mediumFood));
    }

    @Test
    public void getHardFoodWordTest() {
        String hardFood = WordsGeneration.getRandomWordByDifficultyAndCategory(DifficultyLevels.HARD, Category.FOOD);
        Assertions.assertTrue(WordsGeneration.HARD_FOOD().contains(hardFood));
    }
}
