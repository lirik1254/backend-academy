package backend.academy.scrapper.services;

import dto.ListLinksResponseDTO;
import java.util.List;

public interface TagLinksService {
    ListLinksResponseDTO getLinks(Long chatId, List<String> tags);
}
