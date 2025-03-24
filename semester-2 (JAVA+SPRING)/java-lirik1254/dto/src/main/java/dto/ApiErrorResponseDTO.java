package dto;

import java.util.List;

public record ApiErrorResponseDTO(
        String description, String code, String exceptionName, String exceptionMessage, List<String> stacktrace) {}
