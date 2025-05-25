package backend.academy.scrapper.clients;

import dto.ContentDTO;

public interface UpdateLinkClient {
    void sendUpdate(Long chatId, String link, ContentDTO contentDTO);
}
