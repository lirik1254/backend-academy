package gallows;

import java.util.ArrayList;
import java.util.Scanner;
import lombok.Getter;
import lombok.Setter;



@Getter
public class InputOutputClass {

    @Setter
    private DifficultyLevels difficultyLevel;
    private Category category;

    @Setter
    public static int attempts = GallowVisualisation.numberOfAttempts;

    private String difficultyRus;
    private String categoryRus;

    @Setter
    private String currentWord;

    public static String rules = """
        Приветствую тебя в виселице!

            Правила игры:
            Тебе нужно будет отгадать загаданное слово, называя разные буквы.
            Если ты называешь букву, которой нет в загаданном слове, добавляется
            1 часть виселицы.
            Игра продолжается до тех пор, пока ты не разгадаешь слово, либо
            пока виселица не соберется целиком.
            Также, если слово отгадать уж слишком трудно, ты можешь использовать подсказку

            Нажми энтер, чтобы продолжить
        """;

    public static String difficultyChoice = """
         Выбери уровень сложности

            1. Легкий
            2. Средний
            3. Сложный
            4. Случайная

            Введи число 1, 2, 3 или 4 в зависимости от выбранного уровня
        """;

    public static String categoryChoice = """
         Выбери категорию слов

            1. Еда
            2. Природа
            3. Животные
            4. Случайная

        Введи число 1, 2, 3 или 4 в зависимости от выбранной категории""";

    public static String endWords = """
         1. Заново
         2. Выход""";

    @Setter
    private Boolean isHintBeenTaken = false;

    private static Scanner in = new Scanner(System.in);

    @Setter
    GuessingTheLetter guessingTheLetter = new GuessingTheLetter();

    public void setScanner(Scanner scanner) {
        in = scanner;
    }

    @SuppressWarnings("RegexpSinglelineJava")
    public int getNumInput(int leftBorder, int rightBorder, String message) {

        System.out.println(message);

        int someIntValue;

        while (true) {
            if (in.hasNextInt()) {
                someIntValue = in.nextInt();
                in.nextLine();
                if (someIntValue < leftBorder || someIntValue > rightBorder) {
                    System.out.printf("Введенное число должно быть в диапазоне от %d до %d\n\n%s\n",
                        leftBorder, rightBorder, message);
                } else {
                    return someIntValue;
                }
            } else {
                in.nextLine();
                System.out.printf("Вы ввели не число!\n\n%s\n", message);
            }
        }
    }

    @SuppressWarnings({"RegexpSinglelineJava", "MultipleStringLiterals"})
    public void getLetterInput() {

        if (!isHintBeenTaken) {
            System.out.print("\n\nВведите букву или слово 'подсказка': ");

            String c = in.nextLine().toLowerCase();

            if (c.length() != 1 && !c.equals("подсказка")) {
                System.out.println("Вы должны ввести только 1 символ русского алфавита или слово 'подсказка'!\n");
            } else if (!GuessingTheLetter.acceptableCharacters.contains(c) && !c.equals("подсказка")) {
                System.out.println("Допустимо вводить только символы кириллицы!\n");
            } else {
                if (c.equals("подсказка")) {
                    isHintBeenTaken = true;
                } else {
                    guessingTheLetter.initializeWordByChar(c.charAt(0), currentWord);
                }
            }
        } else {
            System.out.print("\n\nВведите букву: ");

            String c = in.nextLine().toLowerCase();

            if (c.length() != 1) {
                System.out.println("Вы должны ввести только 1 символ русского алфавита!\n");
            } else if (!GuessingTheLetter.acceptableCharacters.contains(c)) {
                System.out.println("Допустимо вводить только символы кириллицы!\n");
            } else {
                guessingTheLetter.initializeWordByChar(c.charAt(0), currentWord);
            }
        }
    }

    @SuppressWarnings({"RegexpSinglelineJava", "MultipleStringLiterals"})
    public void printRules() {
        System.out.println(rules);
        in.nextLine();
    }

    @SuppressWarnings({"RegexpSinglelineJava", "MagicNumber", "MissingSwitchDefault"})
    public void getDifficultyLevel() {

       switch (getNumInput(1, 4, difficultyChoice)) {
           case 1 -> {
               difficultyLevel = DifficultyLevels.EASY;
               difficultyRus = difficultyLevel.russianRepresentation();
           }
           case 2 -> {
               difficultyLevel = DifficultyLevels.MEDIUM;
               difficultyRus = difficultyLevel.russianRepresentation();
               }
           case 3 -> {
               difficultyLevel = DifficultyLevels.HARD;
               difficultyRus = difficultyLevel.russianRepresentation();
           }
           case 4 -> {
               difficultyLevel = DifficultyLevels.getRandomDifficultyLevel();
               difficultyRus = difficultyLevel.russianRepresentation();
           }
       }

    }

    @SuppressWarnings({"MissingSwitchDefault", "MagicNumber"})
    public void getCategory() {

        switch (getNumInput(1, 4, categoryChoice)) {
            case 1 -> {
                category = Category.FOOD;
                categoryRus = category.russianRepresentation();
            }
            case 2 -> {
                category = Category.NATURE;
                categoryRus = category.russianRepresentation();
            }
            case 3 -> {
                category = Category.ANIMALS;
                categoryRus = category.russianRepresentation();
            }
            case 4 -> {
                category = Category.getRandomCategory();
                categoryRus = category.russianRepresentation();
            }
        }

        currentWord = WordsGeneration.getRandomWordByDifficultyAndCategory(difficultyLevel, category);
        guessingTheLetter.initializeDataStructureByWord(currentWord);
    }

    @SuppressWarnings("RegexpSinglelineJava")
    public void getStart() {

        ArrayList<Character> enteredLettersArr = guessingTheLetter.getEnteredLettersArr();

        if (isHintBeenTaken) {
            System.out.printf("Подсказка: %s\n", WordsGeneration.ASSOCIATION().get(currentWord));
        }

        System.out.printf("Сложность: %s\n", difficultyRus);
        System.out.printf("Категория: %s\n", categoryRus);
        System.out.printf("Осталось попыток: %d\n", attempts);
        System.out.printf("Список введенных букв: %s\n", enteredLettersArr);

        guessingTheLetter.visualizeWord();

        getLetterInput();

    }

    @SuppressWarnings("RegexpSinglelineJava")
    public void turnOnProgramm() {
        printRules();
        getDifficultyLevel();
        getCategory();
        while (attempts > 0) {
            if (guessingTheLetter.isWordFilled()) {
                System.out.printf("Вы выиграли! Загаданное слово: %s\n", currentWord);
                end();
                return;
            }
            getStart();
        }
        System.out.printf("Увы, вы проиграли.. Загаданное слово было: %s\n\n", currentWord);
        end();

    }

    public void end() {
        if (getNumInput(1, 2, endWords) == 1) {
            attempts = GallowVisualisation.numberOfAttempts;
            isHintBeenTaken = false;
            turnOnProgramm();
        }
    }


//    public static void main(String[] args) {
//
//    }
}
