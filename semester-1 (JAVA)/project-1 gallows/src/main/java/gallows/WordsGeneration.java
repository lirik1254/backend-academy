package gallows;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("MultipleStringLiterals")
public class WordsGeneration {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Getter
    private static final ArrayList<String> EASY_ANIMALS = new ArrayList<>(Arrays.asList("кот", "пес", "мышь",
        "кролик", "лошадь"));
    @Getter
    private static final ArrayList<String> MEDIUM_ANIMALS = new ArrayList<>(Arrays.asList("дельфин", "енот",
        "верблюд", "павлин", "слон"));
    @Getter
    private static final ArrayList<String> HARD_ANIMALS = new ArrayList<>(Arrays.asList("саламандра", "мангуста",
        "капибара", "медоед", "игуана"));

    @Getter
    private static final ArrayList<String> EASY_FOOD = new ArrayList<>(Arrays.asList("чай", "хлеб", "сыр",
        "суп", "молоко"));
    @Getter
    private static final ArrayList<String> MEDIUM_FOOD = new ArrayList<>(Arrays.asList("салат", "курица", "арбуз",
        "картофель", "спагетти"));
    @Getter
    private static final ArrayList<String> HARD_FOOD = new ArrayList<>(Arrays.asList("авокадо", "баклажан",
        "тирамису", "крокембуш", "паннакотта"));
    @Getter
    private static final ArrayList<String> EASY_NATURE = new ArrayList<>(Arrays.asList("лес", "снег", "река",
        "гора", "море"));
    @Getter
    private static final ArrayList<String> MEDIUM_NATURE = new ArrayList<>(Arrays.asList("поле", "озеро", "вулкан",
        "каньон"));
    @Getter
    private static final ArrayList<String> HARD_NATURE = new ArrayList<>(Arrays.asList("айсберг", "гейзер", "кристалл",
        "тундра", "рассвет"));

    @Getter
    private static final HashMap<String, String> ASSOCIATION = new HashMap<>();

    static {
        ASSOCIATION.put("кот", "всеми любимый домашний питомец");
        ASSOCIATION.put("пес", "у него 4 ноги и он не в ладах со всеми любимым домашним питомцем");
        ASSOCIATION.put("мышь", "его ест всеми любимый домашний питомец");
        ASSOCIATION.put("кролик", "у него ушки красивые");
        ASSOCIATION.put("лошадь", "я знаю что она делает фрфррфрф");
        ASSOCIATION.put("верблюд", "он плеваться любит");
        ASSOCIATION.put("павлин", "это птица с красивым оперением");
        ASSOCIATION.put("слон", "самое огромное млекопитающее вроде");
        ASSOCIATION.put("капибара", "с этим животным делают много мемов, любит воду");
        ASSOCIATION.put("дельфин", "красивое синее речное");
        ASSOCIATION.put("енот", "он снимался в стражах галактики");
        ASSOCIATION.put("саламандра", "ящерка");
        ASSOCIATION.put("мангуста", "это какие-то странные похожие на собаку животные из африки которые питаются"
            + " жучками разными");
        ASSOCIATION.put("медоед", "он наверняка любит мед");
        ASSOCIATION.put("игуанна", "ахаха еще одна ящерка прикольная");
        ASSOCIATION.put("чай", "ну я думаю это пьет каждый человек каждый день");
        ASSOCIATION.put("хлеб", "это каждый человек ест каждый день если нет аллергии на глютен");
        ASSOCIATION.put("сыр", "молочный продукт некий");
        ASSOCIATION.put("суп", "с капусткой.. но не красный");
        ASSOCIATION.put("молоко", "муууууууу");
        ASSOCIATION.put("салат", "третье блюдо");
        ASSOCIATION.put("курица", "кудах кудах");
        ASSOCIATION.put("арбуз", "зеленое снаружи, красное внутри..");
        ASSOCIATION.put("картофель", "выкопал в этом году 3 ведра всего");
        ASSOCIATION.put("спагетти", "длинное глютеновое блюдо");
        ASSOCIATION.put("авокадо", "зеленое яйцо");
        ASSOCIATION.put("баклажан", "фиолетовое, большое");
        ASSOCIATION.put("тирамису", "объективно самый вкусный десерт на свете");
        ASSOCIATION.put("паннакотта", "вообще говоря это название пишется с дефисом, но мне пофик");
        ASSOCIATION.put("лес", "иногда хочется бросить всё и уйти туда");
        ASSOCIATION.put("снег", "скоро уже пойдет");
        ASSOCIATION.put("река", "по слухам там много H2o");
        ASSOCIATION.put("гора", "умный туда не пойдет");
        ASSOCIATION.put("море", "вот тут реально очень много H2o");
        ASSOCIATION.put("поле", "там пшеницу растят");
        ASSOCIATION.put("озеро", "Здесь H2o не много, но имеется");
        ASSOCIATION.put("вулкан", "жижа красная присутствует");
        ASSOCIATION.put("каньон", "глубокая долина речная");
        ASSOCIATION.put("айсберг", "на дне его находится дарквеб, на верхушке - сурфейс веб"
            + ", дарквеб посещать не советую, всю зарплату истратите");
        ASSOCIATION.put("гейзер", "горачая H2o почему-то поднимается вверх");
        ASSOCIATION.put("кристалл", "главная валюта в танках онлайн");
        ASSOCIATION.put("тундра", "как говорит гугл - пространство приполярных областей с мелкой растительностью");
        ASSOCIATION.put("рассвет", "и мы встречаем ... .. . .. .. . . укутавшись в облакаааа");
        ASSOCIATION.put("крокембуш", "какой-то треугольник из профитролей");
    }

    private static final HashMap<Category, ArrayList<String>> EASY = new HashMap<>();

    static {
        EASY.put(Category.ANIMALS, EASY_ANIMALS);
        EASY.put(Category.FOOD, EASY_FOOD);
        EASY.put(Category.NATURE, EASY_NATURE);
    }

    private static final HashMap<Category, ArrayList<String>> MEDIUM = new HashMap<>();

    static {
        MEDIUM.put(Category.ANIMALS, MEDIUM_ANIMALS);
        MEDIUM.put(Category.FOOD, MEDIUM_FOOD);
        MEDIUM.put(Category.NATURE, MEDIUM_NATURE);
    }

    private static final HashMap<Category, ArrayList<String>> HARD = new HashMap<>();

    static {
        HARD.put(Category.ANIMALS, HARD_ANIMALS);
        HARD.put(Category.FOOD, HARD_FOOD);
        HARD.put(Category.NATURE, HARD_NATURE);
    }


    private static final HashMap<DifficultyLevels, HashMap<Category, ArrayList<String>>> GUESSING_WORDS =
        new HashMap<>();

    static {
        GUESSING_WORDS.put(DifficultyLevels.EASY, EASY);
        GUESSING_WORDS.put(DifficultyLevels.MEDIUM, MEDIUM);
        GUESSING_WORDS.put(DifficultyLevels.HARD, HARD);
    }

    public static String getRandomWordByDifficultyAndCategory(DifficultyLevels difficultyLevels, Category category) {
        ArrayList<String> wordByLevelCategory = GUESSING_WORDS.get(difficultyLevels).get(category);
        int length = wordByLevelCategory.size();
        return wordByLevelCategory.get(RANDOM.nextInt(length));
    }
}
