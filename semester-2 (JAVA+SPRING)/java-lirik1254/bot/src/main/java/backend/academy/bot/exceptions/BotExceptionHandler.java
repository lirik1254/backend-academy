package backend.academy.bot.exceptions;

import dto.ApiErrorResponseDTO;
import general.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class BotExceptionHandler {
    private final ExceptionUtils exceptionUtils;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalid(Exception ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
                "Некорректные параметры запроса",
                "400",
                ex.getClass().getName(),
                ex.getMessage(),
                exceptionUtils.getStacktrace(ex));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
