package server.pome.global.domain;

import static server.pome.global.exception.BaseResponseStatus.SUCCESS;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import server.pome.global.exception.BaseResponseStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {

  @Schema(title = "성공 여부", example = "true", requiredMode = RequiredMode.REQUIRED)
  private final Boolean isSuccess;

  @Schema(title = "식별 코드", example = "1000", requiredMode = RequiredMode.REQUIRED)
  private final int code;

  @Schema(title = "메시지", example = "요청에 성공했습니다.", requiredMode = RequiredMode.REQUIRED)
  private final String message;

  @Schema(title = "응답 데이터", description = "요청 결과 데이터", nullable = true)
  @JsonInclude(Include.NON_NULL)
  private T result;

  // 요청 성공 응답
  public static <T> BaseResponse<T> success(T result) {
    return new BaseResponse<>(SUCCESS, result);
  }

  // 요청 실패 응답
  public static BaseResponse<?> error(BaseResponseStatus status) {
    return new BaseResponse<>(status, null);
  }

  // 커스텀 메시지 실패 응답
  public static BaseResponse<?> error(BaseResponseStatus status, String customMessage) {
    return new BaseResponse<>(status.isSuccess(), status.getCode(), customMessage, null);
  }

  private BaseResponse(BaseResponseStatus status, T result) {
    this.isSuccess = status.isSuccess();
    this.code = status.getCode();
    this.message = status.getMessage();
    this.result = result;
  }

  private BaseResponse(boolean isSuccess, int code, String message, T result) {
    this.isSuccess = isSuccess;
    this.code = code;
    this.message = message;
    this.result = result;
  }
}
