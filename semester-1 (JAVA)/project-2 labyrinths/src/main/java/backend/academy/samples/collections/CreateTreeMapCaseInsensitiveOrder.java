package backend.academy.samples.collections;

import java.util.SortedMap;
import java.util.TreeMap;

public class CreateTreeMapCaseInsensitiveOrder {
    public static void main(String[] args) {
        // TreeMap with keys sorted by ignoring case
        SortedMap<String, String> fileExtensions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        fileExtensions.put("PYTHON", ".py");
        fileExtensions.put("c++", ".cpp");
        fileExtensions.put("KOTLIN", ".kt");
        fileExtensions.put("Golang", ".go");

        // The keys will be sorted ignoring the case (Try removing String.CASE_INSENSITIVE_ORDER and see the output)
        System.out.println(fileExtensions);
    }
}
