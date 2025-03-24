package log.analyzer.text.report;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TextReportUtils {
    public ArrayList<Map.Entry<String, Integer>> getTopList(HashMap<String, Integer> map) {
        final int top3 = 3;
        return new ArrayList<>(map.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(top3)
            .toList());
    }
}
