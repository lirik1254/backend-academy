package backend.academy.samples.guava;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.google.common.io.Files;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import lombok.extern.log4j.Log4j2;

/**
 * Guava is a set of core Java libraries that includes a lot of useful utility classes (analogous to Apache Commons).
 * <p>
 * <a href="https://github.com/google/guava/wiki">Library documentation</a>
 */
@Log4j2
public class GuavaExample {
    public static void main(String[] args) {
        // Immutable collections throw exception on mutating API methods
        ImmutableList<String> fruits = ImmutableList.of("Apple", "Banana", "Orange");
        try {
            fruits.add("Watermelon"); // also, such methods are marked as deprecated
        } catch (Exception e) {
            log.info("Caught exception of type {} after adding new element", e.getClass());
        }

        // unmodifiableList() is also possible, but clients will not know if the list is actually unmodifiable
        List<String> fruitsArrayList = Collections.unmodifiableList(new ArrayList<>(fruits));
        try {
            fruitsArrayList.add("Watermelon"); // possibly highlighted by IDE inspections
        } catch (Exception e) {
            log.info("Caught exception of type {} after adding new element", e.getClass());
        }

        Multimap<String, String> groceries = ArrayListMultimap.create();
        groceries.put("Fruit", "Apple");
        groceries.put("Fruit", "Banana");
        groceries.put("Fruit", "Cherry");
        groceries.put("Vegetable", "Carrot");
        groceries.put("Vegetable", "Broccoli");
        log.info("Fruits: {}", groceries.get("Fruit"));
        log.info("Vegetables: {}", groceries.get("Vegetable"));

        Set<Integer> firstSet = Set.of(1, 2, 3);
        Set<Integer> secondSet = Set.of(1, 10, 11, 12);
        Set<Integer> difference = Sets.difference(firstSet, secondSet);
        Set<Integer> union = Sets.union(firstSet, secondSet);
        Set<Integer> intersection = Sets.intersection(firstSet, secondSet);
        log.info("Difference of two sets: {}", difference);
        log.info("Union of two sets: {}", union);
        log.info("Intersection of two sets: {}", intersection);

        Iterable<File> filesInCurrentDir = Files.fileTraverser().breadthFirst(new File("."));
        log.info("First 10 XML files from current dir:");
        Streams.stream(filesInCurrentDir)
            .filter(Files.isFile())
            .filter(Predicate.not(file -> file.getAbsolutePath().contains(".idea")))
            .filter(file -> "xml".equals(Files.getFileExtension(file.getName())))
            .limit(10)
            .forEach(log::info);
    }
}
