package gallowsTest;

import gallows.Category;
import gallows.DifficultyLevels;
import gallows.GallowVisualisation;
import gallows.GuessingTheLetter;
import gallows.InputOutputClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class InputOutputClassTest {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final InputStream sysInBackup = System.in;

    InputOutputClass inputOutputClass = new InputOutputClass();
    GuessingTheLetter guessingTheLetter = new GuessingTheLetter();

    public void setInput(String buffer) {
        ByteArrayInputStream in = new ByteArrayInputStream(buffer.getBytes());
        System.setIn(in);

        inputOutputClass.setScanner(new Scanner(System.in));
    }

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void getNumInputInBordersTest() {
        setInput("1\n");

        assertEquals(1, inputOutputClass.getNumInput(1, 3, "alo"));
    }

    @Test
    public void getNumInputOutBordersTest() {
        setInput("0\n2\n");

        assertEquals(2, inputOutputClass.getNumInput(1, 3, ""));
        assertEquals("Введенное число должно быть в диапазоне от 1 до 3", outputStreamCaptor.toString().trim());
    }

    @Test
    public void getNumInputWithAdditionalStringTest() {
        setInput("1\n");

        inputOutputClass.getNumInput(1, 3, "aloha");

        assertEquals("aloha", outputStreamCaptor.toString().trim());
    }

    @Test
    public void getDifficultyLevelTest() {

        setInput("1\n");

        inputOutputClass.getDifficultyLevel();

        assertEquals(InputOutputClass.difficultyChoice.trim(), outputStreamCaptor.toString().trim());
        assertEquals(inputOutputClass.difficultyLevel(), DifficultyLevels.EASY);
        assertEquals(inputOutputClass.difficultyRus(), "Легкая");

        setInput("2\n");

        inputOutputClass.setScanner(new Scanner(System.in));

        inputOutputClass.getDifficultyLevel();

        assertEquals(inputOutputClass.difficultyLevel(), DifficultyLevels.MEDIUM);
        assertEquals(inputOutputClass.difficultyRus(), "Средняя");

        setInput("3\n");

        inputOutputClass.setScanner(new Scanner(System.in));

        inputOutputClass.getDifficultyLevel();

        assertEquals(inputOutputClass.difficultyLevel(), DifficultyLevels.HARD);
        assertEquals(inputOutputClass.difficultyRus(), "Сложная");
        // Другие значения не тестируются, потому что некорректный ввод проверяется getNumInput методом
    }

    @Test
    public void getCategoryTest() {
        setInput("1\n");

        inputOutputClass.difficultyLevel(DifficultyLevels.HARD);

        inputOutputClass.getCategory();

        assertEquals(InputOutputClass.categoryChoice.trim(), outputStreamCaptor.toString().trim());
        assertEquals(inputOutputClass.category(), Category.FOOD);
        assertEquals(inputOutputClass.categoryRus(), "Еда");

        setInput("2\n");

        inputOutputClass.setScanner(new Scanner(System.in));

        inputOutputClass.getCategory();

        assertEquals(inputOutputClass.category(), Category.NATURE);
        assertEquals(inputOutputClass.categoryRus(), "Природа");

        setInput("3\n");

        inputOutputClass.setScanner(new Scanner(System.in));

        inputOutputClass.getCategory();

        assertEquals(inputOutputClass.category(), Category.ANIMALS);
        assertEquals(inputOutputClass.categoryRus(), "Животные");
    }

    @Test
    public void winTest() throws NoSuchFieldException, IllegalAccessException {

        setInput("\n1\n1\nа\nб\nв\nг\nд\nе\nж\nз\nи\nй\nк\nл\nм\nн\nо\nп\nр\nс\nт\nу\nф\nх\nц\nч\nш\nщ\nъ\nы\nь\nэ\nю\nя\nа\n2\n\"");

        InputOutputClass.attempts(50);
        GallowVisualisation.numberOfAttempts = 50;

        inputOutputClass.turnOnProgramm();

        assertTrue(outputStreamCaptor.toString().contains("Вы выиграли! Загаданное слово:"));

    }

    @Test
    public void loseTest() {
        setInput("\n1\n1\n2\n");

        InputOutputClass.attempts(0);

        inputOutputClass.turnOnProgramm();

        assertTrue(outputStreamCaptor.toString().contains("Увы, вы проиграли.. Загаданное слово было:"));
    }

    @Test
    public void withHintTest() {
        setInput("\n1\n1\nподсказка\nа\nб\nв\nг\nд\nе\nж\nз\nи\nй\nк\n" +
            "л\nм\nн\nо\nп\nр\nс\nт\nу\nф\nх\nц\nч\nш\nщ\nъ\nы\nь\nэ\nю\nя\nа\n2\n");

        GallowVisualisation.numberOfAttempts = 10;
        InputOutputClass.attempts = 10;

        inputOutputClass.turnOnProgramm();

        assertEquals(outputStreamCaptor.toString().indexOf("Введите букву или слово 'подсказка'"),
            outputStreamCaptor.toString().lastIndexOf("Введите букву или слово 'подсказка'"));
        assertTrue(outputStreamCaptor.toString().contains("Подсказка:"));
    }

    @Test
    public void withoutHintTest() {
        setInput("\n1\n1\nа\nб\nв\nг\nд\nе\nж\nз\nи\nй\nк\n" +
            "л\nм\nн\nо\nп\nр\nс\nт\nу\nф\nх\nц\nч\nш\nщ\nъ\nы\nь\nэ\nю\nя\nа\n2\n");

        GallowVisualisation.numberOfAttempts = 10;
        InputOutputClass.attempts = 10;

        inputOutputClass.turnOnProgramm();

        assertNotEquals(outputStreamCaptor.toString().indexOf("Введите букву или слово 'подсказка'"),
            outputStreamCaptor.toString().lastIndexOf("Введите букву или слово 'подсказка'"));
        assertFalse(outputStreamCaptor.toString().contains("Подсказка:"));
    }

    @Test
    public void noHintWhenNewGameStartsTest() {
        setInput("\n1\n1\nподсказка\nа\nб\nв\nг\nд\nе\nж\nз\nи\nй\nк\n" +
            "л\nм\nн\nо\nп\nр\nс\nт\nу\nф\nх\nц\nч\nш\nщ\nъ\nы\nь\nэ\nю\nя\n1\n\n1\n1\n" +
            "а\nб\nв\nг\nд\nе\nж\nз\nи\nй\nк\nл\nм\nн\nо\nп\nр\nс\nт\nу\nф\nх\nц\nч\nш\nщ\nъ\nы\nь\nэ\nю\nя\n2\n");

        GallowVisualisation.numberOfAttempts = 10;
        InputOutputClass.attempts = 10;

        inputOutputClass.turnOnProgramm();

        assertNotEquals(outputStreamCaptor.toString().indexOf("Введите букву или слово 'подсказка'"),
            outputStreamCaptor.toString().lastIndexOf("Введите букву или слово 'подсказка'"));
    }

    @Test
    public void correctLetterInputTest() {

        inputOutputClass.currentWord("собака");
        guessingTheLetter.guessingWordLength(6);
        guessingTheLetter.initializeDataStructureByWord("собака");
        inputOutputClass.guessingTheLetter(guessingTheLetter);

        setInput("а\n");

        inputOutputClass.getLetterInput();

        assertEquals("Введите букву или слово 'подсказка':", outputStreamCaptor.toString().trim());
    }

    @Test
    public void notCyrillicInputTest() {
        guessingTheLetter.visualizeWordMas(new char[]{' ', ' ', ' ', ' ', ' ', ' '});
        guessingTheLetter.guessingWordLength(6);
        inputOutputClass.currentWord("собака");

        setInput("f\n");

        guessingTheLetter.enteredLetters().clear();
        inputOutputClass.isHintBeenTaken(false);

        inputOutputClass.getLetterInput();

        assertEquals("Введите букву или слово 'подсказка': Допустимо вводить только символы кириллицы!",
            outputStreamCaptor.toString().trim());
    }

    @Test
    public void moreThanOneLetterInputTest() {
        guessingTheLetter.visualizeWordMas(new char[]{' ', ' ', ' ', ' ', ' ', ' '});
        guessingTheLetter.guessingWordLength(6);
        inputOutputClass.currentWord("собака");

        setInput("фвыафыва\n");

        inputOutputClass.isHintBeenTaken(false);

        inputOutputClass.getLetterInput();

        assertEquals("Введите букву или слово 'подсказка': Вы должны ввести только 1 символ русского алфавита или слово 'подсказка'!",
            outputStreamCaptor.toString().trim());
    }

}
