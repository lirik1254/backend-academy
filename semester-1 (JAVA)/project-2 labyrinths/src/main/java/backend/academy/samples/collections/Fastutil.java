package backend.academy.samples.collections;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.Char2IntRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.io.BinIO;
import it.unimi.dsi.fastutil.io.TextIO;
import it.unimi.dsi.fastutil.longs.Long2IntAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2IntSortedMap;
import it.unimi.dsi.fastutil.longs.Long2IntSortedMaps;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongMappedBigList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import lombok.extern.log4j.Log4j2;

/**
 * fastutil extends a collection framework with performance-optimized data structures.
 * <p>
 * <a href="https://fastutil.di.unimi.it/docs/">Library documentation</a>
 */
@Log4j2
public class Fastutil {
    public static void main(String[] args) throws IOException {
        LongSet openHashSet = new LongOpenHashSet();
        // Access methods avoid boxing and unboxing:
        openHashSet.add(1);
        openHashSet.contains(2);

        // Sum all elements
        long sum = 0;
        for (LongIterator i = openHashSet.iterator(); i.hasNext(); ) {
            sum += i.nextLong();
        }
        // Note that “for each” iteration must be avoided:
        for (long x : openHashSet) {
            sum += x;
        }
        // In the loop above, boxing and unboxing is happening (even if your IDE does not report it).
        // In some cases, a solution is to use a type-specific forEach():

        // Print all elements
        openHashSet.forEach(log::info);
        // Or we can use fastutil's type-specific version of Java 8's streams:
        sum = openHashSet.longStream().sum();

        // Suppose instead you want to store a sorted map from longs to integers.
        // We will use a tree of AVL type:
        Long2IntSortedMap avlTreeMap = new Long2IntAVLTreeMap();
        // Now we can easily modify and access its content:
        avlTreeMap.put(1, 5);
        avlTreeMap.put(2, 6);
        avlTreeMap.put(3, 7);
        avlTreeMap.put(1000000000L, 10);
        avlTreeMap.get(1); // This method call will return 5
        avlTreeMap.get(4); // This method call will return 0

        // We can also try to change the default return value:
        avlTreeMap.defaultReturnValue(-1);
        avlTreeMap.get(4); // This method call will return -1
        // We can obtain a type-specific iterator on the key set:
        LongBidirectionalIterator lbi = avlTreeMap.keySet().iterator();
        // Now we sum all keys
        sum = 0;
        while (lbi.hasNext()) {
            sum += lbi.nextLong();
        }

        // Or we can use again fastutil's type-specific version of Java 8's streams:
        sum = avlTreeMap.keySet().longStream().sum();

        // We now generate a head map, and iterate bidirectionally over it starting from a given point:
        // This map contains only keys smaller than 4
        Long2IntSortedMap sortedMap = avlTreeMap.headMap(4);
        // This iterator is positioned between 2 and 3
        LongBidirectionalIterator t = sortedMap.keySet().iterator(2);
        t.previous(); // This method call will return 2 (t.next() would return 3)

        // Should we need to access the map concurrently, we can wrap it:
        // This map can be safely accessed by many threads
        Long2IntSortedMap m2 = Long2IntSortedMaps.synchronize(sortedMap);

        // Linked maps are very flexible data structures which can be used to implement, for instance, queues whose content can be probed efficiently:

        // This map remembers insertion order.
        IntSortedSet sortedSet = IntLinkedOpenHashSet.of(4, 3, 2, 1);
        sortedSet.firstInt(); // This method call will return 4
        sortedSet.lastInt(); // This method call will return 1
        sortedSet.contains(5); // This method will return false
        IntBidirectionalIterator bi =
            sortedSet.iterator(sortedSet.lastInt()); // We could even cast it to a list iterator
        bi.previous(); // This method call will return 1
        bi.previous(); // This method call will return 2
        sortedSet.remove(sortedSet.lastInt()); // This will remove the last element in constant time

        // Now, we play with iterators. It is easy to create iterators over intervals or over arrays, and combine them:
        IntIterator intListIterator = IntIterators.fromTo(0, 10); // This iterator will return 0, 1, ..., 9
        int[] a = new int[] {5, 1, 9};
        int[] b = new int[] {6, 2, 3};
        IntIterator wrapped = IntIterators.wrap(a); // This iterator will return 5, 1, 9.
        IntIterator k = IntIterators.concat(intListIterator, wrapped); // This iterator will return 0, 1, ..., 9, 5, 1, 9

        // It is easy to build lists and sets on the fly using the of static factory methods.
        // For maps you can use the constructors that take key and value arrays (array-based constructors for list and set exist too):
        IntSet s = IntOpenHashSet.of(1, 2, 3); // This set will contain 1, 2, and 3
        Char2IntMap m = new Char2IntRBTreeMap(new char[] {'@', '-'}, new int[] {0, 1}); // This map will map '@' to 0 and '-' to 1

        // Whenever you have some data structure, it is easy to serialize it in an efficient (buffered) way, or to dump their content in textual form:
        BinIO.storeObject(s, "foo"); // This method call will save s in the file named "foo"
        TextIO.storeInts(s.intIterator(), "foo.txt"); // This method call will save the content of s in ASCII
        IntIterator ii =
            TextIO.asIntIterator("foo.txt"); // This iterator will parse the file and return the integers therein

        // You can also store data (of any size) in native format and access it via memory mapping:
        var longs = new long[] {1, 2, 3, 4, 5, 6};
        BinIO.storeLongs(longs, "foo",
            ByteOrder.nativeOrder()); // This method call will save the (big) array of longs l in the file named "foo" in native order
        var fileChannel = FileChannel.open(new File("foo").toPath());
        LongMappedBigList lmbl =
            LongMappedBigList.map(fileChannel); // Now you can access the data in l via memory mapping

        // Support for Java 8 primitive streams is included for primitive collections (e.g. intStream),
        // which will work in terms of primitives instead of boxing to wrapper types like the regular stream would do:
        IntList ints = IntList.of(2, 380, 1297);
        int lSum = ints.intStream().sum();  // Will be 1679
        IntList lTransformed = IntArrayList.toList(ints.intStream().map(i -> i + 40)); // Will be 42, 420, 1337

        // You can sort arrays using type-specific comparators specified by lambda expressions (no boxing/unboxing here):
        IntArrays.quickSort(a, (x, y) -> Integer.compare(y, x)); // Sorts in reverse order
        // You can also easily specify complex generic sorting, like sorting indirectly on a while swapping elements in a and b in parallel:
        Arrays.quickSort(0, a.length, (i, j) -> Integer.compare(a[i], a[j]), (i, j) -> {
            IntArrays.swap(a, i, j);
            IntArrays.swap(b, i, j);
        });

        // If you have several cores, you can do it in parallel:
        IntArrays.parallelQuickSort(a, (x, y) -> y - x); // Sorts in reverse order
        Arrays.parallelQuickSort(0, a.length, (i, j) -> Integer.compare(a[i], a[j]), (i, j) -> {
            IntArrays.swap(a, i, j);
            IntArrays.swap(b, i, j);
        });

        // Some maps provide a fast iterator on their entry set: such iterators are allowed to reuse the Map.Entry instance they return,
        // resulting is highly reduced garbage collection (e.g., for large hash maps). To easily access such iterators, we can use a helper static method:
        Int2IntOpenHashMap intOpenHashMap = new Int2IntOpenHashMap();
        for (Int2IntMap.Entry e : Int2IntMaps.fastIterable(intOpenHashMap)) {
            log.info("{}: {}", e.getIntKey(), e.getIntValue());
        }
    }
}
