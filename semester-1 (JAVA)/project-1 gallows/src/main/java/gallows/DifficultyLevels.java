package gallows;

import java.util.Random;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DifficultyLevels {
    EASY("Легкая"),
    MEDIUM("Средняя"),
    HARD("Сложная");

    private final String russianRepresentation;

    public static DifficultyLevels getRandomDifficultyLevel() {
        Random rand = new Random();
        return DifficultyLevels.values()[rand.nextInt(DifficultyLevels.values().length)];
    }
}
