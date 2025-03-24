package gallowsTest;

import gallows.GuessingTheLetter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuessingTheLetterUtilsTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    GuessingTheLetter guessingTheLetter = new GuessingTheLetter();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void initializeDataStructureByWordTest() {
        String word = "aboba";
        guessingTheLetter.initializeDataStructureByWord(word);

        char[] visualizeWordMas = guessingTheLetter.visualizeWordMas();

        assertEquals(word.length(), visualizeWordMas.length);
        assertEquals(' ', visualizeWordMas[0]);
    }

    @Test
    public void initializeWordByCharTest() {
        String word = "aboba";

        guessingTheLetter.initializeDataStructureByWord(word);

        guessingTheLetter.initializeWordByChar('a', word);

        assertEquals(word.charAt(0), guessingTheLetter.visualizeWordMas()[0]);
        assertEquals(word.charAt(4), guessingTheLetter.visualizeWordMas()[4]);
        assertEquals(' ', guessingTheLetter.visualizeWordMas()[1]);
    }

    @Test
    public void visualizeEmptyWordTest() {
        String word = "aboba";

        guessingTheLetter.initializeDataStructureByWord(word);

        guessingTheLetter.visualizeWord();

        assertEquals("‾ ‾ ‾ ‾ ‾", outputStreamCaptor.toString().trim());
    }

    @Test
    public void visualizeWordNotEmptyTest() {
        String word = "aboba";

        guessingTheLetter.initializeDataStructureByWord(word);

        guessingTheLetter.initializeWordByChar('a', word);

        guessingTheLetter.visualizeWord();

        assertEquals("a       a \n‾ ‾ ‾ ‾ ‾", outputStreamCaptor.toString().trim().replaceAll("\r", ""));
    }

    @Test
    public void isWordFilledTestNotFilled() {
        String word = "aboba";

        guessingTheLetter.initializeDataStructureByWord(word);

        assertFalse(guessingTheLetter.isWordFilled());
    }

    @Test
    public void isWordFilledTestFilledHalf() {
        String word = "aboba";

        guessingTheLetter.initializeDataStructureByWord(word);

        guessingTheLetter.initializeWordByChar('a', word);

        assertFalse(guessingTheLetter.isWordFilled());
    }

    @Test
    public void isWordFilledTestFilled() {
        String word = "aboba";

        guessingTheLetter.initializeDataStructureByWord(word);

        guessingTheLetter.initializeWordByChar('a', word);
        guessingTheLetter.initializeWordByChar('b', word);
        guessingTheLetter.initializeWordByChar('o', word);

        assertTrue(guessingTheLetter.isWordFilled());
    }

}
