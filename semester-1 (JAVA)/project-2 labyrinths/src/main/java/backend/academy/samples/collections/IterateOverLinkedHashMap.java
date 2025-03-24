package backend.academy.samples.collections;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class IterateOverLinkedHashMap {
    public static void main(String[] args) {
        LinkedHashMap<String, String> userCityMapping = new LinkedHashMap<>();

        userCityMapping.put("Rajeev", "Bengaluru");
        userCityMapping.put("Chris", "London");
        userCityMapping.put("David", "Paris");
        userCityMapping.put("Jesse", "California");

        log.info("=== Iterating over the LinkedHashMap's entrySet using Java 8 forEach and lambda ===");
        userCityMapping.forEach((key, value) -> log.info("{} => {}", key, value));

        log.info("=== Iterating over the entrySet of a LinkedHashMap using iterator() ===");
        Iterator<Map.Entry<String, String>> userCityMappingIterator = userCityMapping.entrySet().iterator();
        while (userCityMappingIterator.hasNext()) {
            Map.Entry<String, String> entry = userCityMappingIterator.next();
            log.info("{} => {}", entry.getKey(), entry.getValue());
        }

        log.info("=== Iterating over the entrySet of a LinkedHashMap using iterator() and forEachRemaining ===");
        userCityMappingIterator = userCityMapping.entrySet().iterator();
        userCityMappingIterator.forEachRemaining(entry -> log.info("{} => {}", entry.getKey(), entry.getValue()));
    }
}
