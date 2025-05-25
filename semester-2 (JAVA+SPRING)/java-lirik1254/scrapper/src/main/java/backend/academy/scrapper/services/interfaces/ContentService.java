package backend.academy.scrapper.services.interfaces;

import dto.ContentDTO;
import java.util.List;

public interface ContentService {
    List<ContentDTO> getContentByRandomUrl(Long chatId);
}
