package gallows;

import java.util.Random;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum Category {
    ANIMALS("Животные"),
    FOOD("Еда"),
    NATURE("Природа");


    private final String russianRepresentation;

    public static Category getRandomCategory() {
        Random random = new Random();
        return Category.values()[random.nextInt(Category.values().length)];
    }
}
