package server.pome.global.exception;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import server.pome.global.domain.BaseResponse;
import server.pome.global.enums.TypeEnum;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
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
  public ResponseEntity<BaseResponse<?>> handleValidationException(
      MethodArgumentNotValidException e) {
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

  // Request에서 ENUM과 바인딩 실패한 경우
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<BaseResponse<?>> handleTypeMisMatch(HttpMessageNotReadableException e) {
    Throwable root = e.getMostSpecificCause();

    if (root instanceof InvalidFormatException invalidFormatException) {
      Class<?> raw = extractRawTargetClass(invalidFormatException);

      // TYPENUM 매칭 실패
      if (raw != null && raw.isEnum() && raw == TypeEnum.class) {
        return ResponseEntity
            .badRequest()
            .body(BaseResponse.error(BaseResponseStatus.INVALID_TYPE_ENUM));
      }
    }

    return ResponseEntity
        .badRequest()
        .body(BaseResponse.error(BaseResponseStatus.INVALID_REQUEST_FORM));
  }

  /**
   * 헬퍼 메서드
   */
  private static Class<?> extractRawTargetClass(InvalidFormatException invalidFormatException) {
    try {
      Object target = invalidFormatException.getTargetType();
      if (target instanceof JavaType jt) {
        return jt.getRawClass();
      }
      if (target instanceof Class<?> c) {
        return c;
      }
    } catch (Throwable ignore) { }

    return null;

  }
}
