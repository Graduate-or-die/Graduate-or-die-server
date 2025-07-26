package server.pome.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import server.pome.global.domain.BaseResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // 커스텀 예외 처리
  @ExceptionHandler(BaseException.class)
  public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e) {
    log.warn("[BaseException] code={}, message={}", e.getStatus().getCode(), e.getMessage());
    return ResponseEntity
        .status(e.getStatus().getHttpStatus())
        .body(BaseResponse.error(e.getStatus(), e.getMessage()));
  }

  // 유효성 검증 실패 처리
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .findFirst()
        .orElse("입력값이 올바르지 않습니다.");
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(BaseResponseStatus.REQUEST_ERROR, message));
  }

  // 처리되지 않음 모든 예외에 대한 기본 처리
  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<?>> handleUnhandledException(Exception e) {
    log.error("[UnhandledException] ", e);
    return ResponseEntity
        .status(BaseResponseStatus.SERVER_ERROR.getHttpStatus())
        .body(BaseResponse.error(BaseResponseStatus.SERVER_ERROR));
  }
}
