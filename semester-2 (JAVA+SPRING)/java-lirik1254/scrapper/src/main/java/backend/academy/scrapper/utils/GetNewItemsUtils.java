package backend.academy.scrapper.utils;

import dto.ContentDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class GetNewItemsUtils {

    public List<ContentDTO> getNewItems(List<ContentDTO> oldList, List<ContentDTO> newList) {
        return newList.stream().filter(newItem -> !oldList.contains(newItem)).collect(Collectors.toList());
    }
}
