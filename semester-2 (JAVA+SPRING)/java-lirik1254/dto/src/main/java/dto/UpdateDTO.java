package dto;

import java.util.List;

public record UpdateDTO(Long id, String url, ContentDTO contentDTO, List<Long> tgChatIds) {}
