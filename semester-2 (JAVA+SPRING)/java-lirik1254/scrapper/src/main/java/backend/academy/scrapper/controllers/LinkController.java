package backend.academy.scrapper.controllers;

import backend.academy.scrapper.services.LinkService;
import dto.AddLinkDTO;
import dto.LinkResponseDTO;
import dto.ListLinksResponseDTO;
import dto.RemoveLinkRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/links", produces = "application/json")
public class LinkController {
    private final LinkService linkService;

    @PostMapping
    public LinkResponseDTO addLink(
            @RequestHeader("Tg-Chat-Id") Long chatId, @RequestBody @Valid AddLinkDTO addRequest) {
        return linkService.addLink(chatId, addRequest);
    }

    @DeleteMapping
    public LinkResponseDTO deleteLink(
            @RequestHeader("Tg-Chat-Id") Long chatId, @RequestBody @Valid RemoveLinkRequest removeLinkRequest) {
        return linkService.deleteLink(chatId, removeLinkRequest.link());
    }

    @GetMapping
    public ListLinksResponseDTO getLinks(@RequestHeader("Tg-Chat-Id") Long chatId) {
        return linkService.getLinks(chatId);
    }
}
