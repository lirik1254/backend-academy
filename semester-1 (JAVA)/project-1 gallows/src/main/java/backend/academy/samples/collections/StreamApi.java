package backend.academy.samples.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamApi {
    public enum Gender {MALE, FEMALE}

    public record Footballer(String name, int age, Gender gender, List<String> positions) {
    }

    private final List<Footballer> footballerList = List.of(
        new Footballer("Messi", 32, Gender.MALE, List.of("CF", "CAM", "RF")),
        new Footballer("Griezmann", 28, Gender.MALE, List.of("CF", "CAM", "LF")),
        new Footballer("Arthur", 23, Gender.MALE, List.of("CM", "CAM")),
        new Footballer("Ter Stegen", 27, Gender.MALE, List.of("GK")),
        new Footballer("Puig", 20, Gender.MALE, List.of("CM", "CDM")),
        new Footballer("Jennifer", 29, Gender.FEMALE, List.of("CF", "CAM")),
        new Footballer("Jana", 17, Gender.FEMALE, List.of("CB")),
        new Footballer("Alexia", 25, Gender.FEMALE, List.of("CAM", "RF", "LF"))
    );

    public List<Footballer> filter() {
        return footballerList.stream()
            .filter(footballer -> footballer.gender().equals(Gender.FEMALE))
            .filter(footballer -> footballer.age() > 23)
            .toList();
    }

    public long femalesMoreThan24y() {
        return footballerList.stream()
            .filter(footballer -> footballer.gender().equals(Gender.FEMALE))
            .map(Footballer::age)
            .filter(age -> age > 24)
            .count();
    }

    String allPositionsOfMaleLessThan30y() {
        return footballerList.stream()
            .filter(footballer -> footballer.gender().equals(Gender.MALE))
            .filter(footballer -> footballer.age() < 30)
            .map(Footballer::positions)
            .flatMap(Collection::stream)
            .collect(Collectors.joining(","));
    }

    String allUniquePositionsOfMaleLessThan30y() {
        return footballerList.stream()
            .filter(footballer -> footballer.gender().equals(Gender.MALE))
            .filter(footballer -> footballer.age() < 30)
            .map(Footballer::positions)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.joining(","));
    }

    List<Footballer> sortByGenderAndName() {
        return footballerList.stream()
            .sorted(Comparator.comparing(Footballer::gender).thenComparing(Footballer::name))
            .collect(Collectors.toList());
    }

    long malePlayerCount() {
        return footballerList.stream()
            .filter(footballer -> footballer.gender().equals(Gender.MALE))
            .sorted(Comparator.comparing(Footballer::age))
            .peek(footballer -> {
                System.out.println("footballer = " + footballer);
            })
            .count();
    }

    List<Footballer> twoMaleFootballers() {
        return footballerList.stream()
            .filter(footballer -> footballer.gender().equals(Gender.MALE))
            .limit(2)
            .collect(Collectors.toList());
    }

    //    # Skip
    List<Footballer> sortByGenderAndNameSkipping5() {
        return footballerList.stream()
            .sorted(Comparator.comparing(Footballer::gender).thenComparing(Footballer::name))
            .skip(5)
            .collect(Collectors.toList());
    }

    List<Integer> filteredList() {
        return Stream.of(2, 4, 6, 8, 9, 10, 11, 12)
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
    }

    //Take, While ...
    List<Integer> takeAWhile() {
        return Stream.of(2, 4, 6, 8, 9, 10, 11, 12)
            .takeWhile(n -> n % 2 == 0)
            .collect(Collectors.toList());
    }

    List<Integer> dropWhile() {
        return Stream.of(2, 4, 6, 8, 9, 10, 11, 12)
            .dropWhile(n -> n % 2 == 0)
            .collect(Collectors.toList());
    }

    public void foreach() {
        Stream.of(4, 1, 6, 7, 19, 2, 3, 81, 64)
            .parallel()
            .filter(number -> number < 65)
            .forEach(number -> System.out.println("number = " + number));
    }

    public void foreachOrdered() {
        Stream.of(4, 1, 6, 7, 19, 2, 3, 81, 64)
            .parallel()
            .filter(number -> number < 65)
            .forEachOrdered(number -> System.out.println("number = " + number));
    }

    Footballer[] femaleFootballersToArray() {
        return footballerList.stream()
            .filter(footballer -> footballer.gender().equals(Gender.FEMALE))
            .toArray(Footballer[]::new);
    }

    Optional<Integer> minAge() {
        return footballerList.stream()
            .min(Comparator.comparing(Footballer::age))
            .map(Footballer::age);
    }

    Optional<Integer> maxAge() {
        return footballerList.stream()
            .max(Comparator.comparing(Footballer::age))
            .map(Footballer::age);
    }

    boolean anyMatch() {
        return footballerList
            .stream()
            .anyMatch(footballer -> footballer.age() > 25);
    }

    boolean allMatch() {
        return footballerList.stream()
            .allMatch(footballer -> footballer.age() > 25);
    }

    boolean noneMatch() {
        return footballerList.stream()
            .noneMatch(footballer -> footballer.age() > 100);
    }

    Optional<Integer> findFirst() {
        return List.of(4, 1, 3, 7, 5, 6, 2, 28, 15, 29)
            .parallelStream()
            .filter(number -> number > 5)
            .findFirst();
    }

    Optional<Integer> findAny() {
        return List.of(4, 1, 3, 7, 5, 6, 2, 28, 15, 29)
            .parallelStream()
            .filter(number -> number > 5)
            .findAny();
    }

    Optional<String> longestName() {
        return footballerList.stream()
            .map(Footballer::name)
            .reduce((name1, name2)
                -> name1.length() > name2.length()
                ? name1 : name2);
    }

    List<Footballer> collect() {
        return footballerList.stream()
            .filter(footballer -> footballer.gender().equals(Gender.FEMALE))
            .filter(footballer -> footballer.age() > 23)
            .toList();
    }
}
