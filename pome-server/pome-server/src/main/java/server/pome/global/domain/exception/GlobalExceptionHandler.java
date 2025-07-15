package server.pome.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import server.pome.global.domain.BaseResponse;

import static org.springframework.http.HttpStatus.*;
import static server.pome.global.exception.BaseResponseStatus.*;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  // 커스텀 BaseException 처리
  @ExceptionHandler(BaseException.class)
  public ResponseEntity<BaseResponse<?>> handlerHttpMessageException(BaseException e) {
    BaseResponseStatus status = e.getStatus();
    String message = e.getCustomMessage() != null ? e.getCustomMessage() : e.getMessage();

    log.warn("[BaseException] code = {}, message = {}", status.getCode(), message);

    return ResponseEntity
        .status(status.getHttpStatus())
        .body(BaseResponse.error(status, message));
  }

  // Valid 예외 처리
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<?>> handleValidationException(
      MethodArgumentNotValidException e) {
    String errorMessage = e.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .findFirst()
        .orElse("입력값이 올바르지 않습니다.");

    log.warn("[ValidationException] {}", errorMessage);

    return ResponseEntity
        .badRequest()
        .body(BaseResponse.error(REQUEST_ERROR, errorMessage));
  }

  // 처리되지 않은 예외 처리
  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<?>> handleUnhandledException(Exception e) {
    log.error("[UnhandledException] {}", e.getMessage(), e);

    return ResponseEntity
        .status(SERVER_ERROR.getHttpStatus())
        .body(BaseResponse.error(SERVER_ERROR));
  }
}
