package backend.academy.scrapper.controllers;

import backend.academy.scrapper.services.interfaces.TagLinksService;
import dto.ListLinksResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/link-tags/{chatId}", produces = "application/json")
@RequiredArgsConstructor
public class TagLinksController {

    private final TagLinksService tagLinksService;

    @PostMapping
    public ListLinksResponseDTO getLinks(@RequestBody List<String> tags, @PathVariable Long chatId) {
        return tagLinksService.getLinks(chatId, tags);
    }
}
