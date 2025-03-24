package backend.academy.samples.java21;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * <a href="https://docs.oracle.com/en/java/javase/22/core/creating-sequenced-collections-sets-and-maps.html">Feature documentation</a>
 */
public class SequencedCollections {

    public static void main(String[] args) {

        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        int first = numbers.get(0);
        int last = numbers.get(numbers.size() - 1);

        // new API
        first = numbers.getFirst();
        last = numbers.getLast();
        System.out.println("First element: " + first); // 1
        System.out.println("Last element: " + last); // 9

        // SequencedCollection<E> extends Collection<E>
        // SequencedSet<E> extends SequencedCollection<E>, Set<E>
        // SequencedMap<K, V> extends Map<K, V>

        // https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/SequencedCollection.html

        numbers.addFirst(0);
        numbers.addLast(10);

        var reverseNumbers = numbers.reversed();
        System.out.println("reverseNumbers: " + reverseNumbers); // [10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0]

        numbers.removeFirst();
        numbers.removeLast();

        // A sequenced set is a Set that is a SequencedCollection that contains no duplicate elements.
        //     SequencedSet<E> reversed();    // covariant override
        LinkedHashSet<Integer> integerSet = new LinkedHashSet<>(numbers);
        System.out.println("integerSet: " + integerSet.reversed()); // [9, 8, 7, 6, 5, 4, 3, 2, 1]

        // SequencedMap<K, V> extends Map<K, V>
        LinkedHashMap<String, Integer> sequencedMap = new LinkedHashMap<>();
        sequencedMap.put("one", 1);
        sequencedMap.put("two", 2);
        sequencedMap.put("three", 3);

        System.out.println("sequencedMap: " + sequencedMap); // {one=1, two=2, three=3}
        System.out.println("firstEntry: " + sequencedMap.firstEntry()); // one=1
        System.out.println("lastEntry: " + sequencedMap.lastEntry()); // three=3
        System.out.println("pollFirstEntry: " + sequencedMap.pollFirstEntry()); // one=1
        System.out.println("pollLastEntry: " + sequencedMap.pollLastEntry()); // three=3
        System.out.println("sequencedMap: " + sequencedMap); // {two=2}

    }
}
