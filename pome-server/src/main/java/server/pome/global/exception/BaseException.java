package server.pome.global.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

  private final BaseResponseStatus status;

  public BaseException(BaseResponseStatus status) {
    super(status.getMessage());
    this.status = status;
  }

  public BaseException(BaseResponseStatus status, String customMessage) {
    super(customMessage);
    this.status = status;
  }
}
