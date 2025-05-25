package backend.academy.scrapper.services.interfaces;

import dto.AddLinkDTO;
import dto.LinkResponseDTO;
import dto.ListLinksResponseDTO;

public interface LinkService {
    LinkResponseDTO addLink(Long chatId, AddLinkDTO addRequest);

    LinkResponseDTO deleteLink(Long chatId, String link);

    ListLinksResponseDTO getLinks(Long chatId);
}
