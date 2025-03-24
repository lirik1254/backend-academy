package gallows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuessingTheLetter {

    public static ArrayList<String> acceptableCharacters = new ArrayList<>();

    private Integer guessingWordLength;

    private char[] visualizeWordMas;

    private HashSet<Character> enteredLetters = new HashSet<>();

    public ArrayList<Character> getEnteredLettersArr() {
        ArrayList<Character> arr = new ArrayList<>(enteredLetters.stream().toList());
        Collections.sort(arr);
        return arr;
    }

    public void initializeDataStructureByWord(String guessingWord) {
        guessingWordLength = guessingWord.length();
        visualizeWordMas = new char[guessingWordLength];
        for (int i = 0; i < guessingWordLength; i++) {
            visualizeWordMas[i] = ' ';
        }
        enteredLetters.clear();
        for (char c = 'а'; c <= 'я'; c++) {
            acceptableCharacters.add(String.valueOf(c));
        }
    }

    @SuppressWarnings("RegexpSinglelineJava")
    public void initializeWordByChar(char c, String guessingWord) {
        if (!enteredLetters.contains(c) && guessingWord.indexOf(c) != -1) {
            for (int i = 0; i < guessingWordLength; i++) {
                if (guessingWord.charAt(i) == c) {
                    visualizeWordMas[i] = c;
                }
            }
        } else if (enteredLetters.contains(c)) {
            System.out.println("Вы уже вводили эту букву!\n");
        } else {
            InputOutputClass.attempts -= 1;
            GallowVisualisation.visualisationByAttempts(InputOutputClass.attempts);
        }

        enteredLetters.add(c);
    }

    @SuppressWarnings({"RegexpSinglelineJava", "NoWhitespaceBefore", "EmptyStatement", "MultipleStringLiterals"})
    public void visualizeWord() {
        for (int i = 0; i < guessingWordLength; i ++) {
            System.out.printf("%c ", visualizeWordMas[i]);
        };
        System.out.println();
        for (int i = 0; i < guessingWordLength; i++) {
            System.out.printf("%c ", '‾');
        }
    }

    public boolean isWordFilled() {
        for (int i = 0; i < guessingWordLength; i++) {
            if (visualizeWordMas[i] == ' ') {
                return false;
            }
        }
        return true;
    }


//    public static void main(String[] args) {
//
//    }

}
