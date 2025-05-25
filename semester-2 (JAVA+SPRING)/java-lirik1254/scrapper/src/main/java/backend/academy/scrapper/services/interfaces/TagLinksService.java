package backend.academy.scrapper.services.interfaces;

import dto.ListLinksResponseDTO;
import java.util.List;

public interface TagLinksService {
    ListLinksResponseDTO getLinks(Long chatId, List<String> tags);
}
