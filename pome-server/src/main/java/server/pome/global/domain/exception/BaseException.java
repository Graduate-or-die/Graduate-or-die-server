package server.pome.global.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

  private final BaseResponseStatus status;
  private final String customMessage;

  public BaseException(BaseResponseStatus status) {
    super(status.getMessage());
    this.status = status;
    this.customMessage = null;
  }

  public BaseException(BaseResponseStatus status, String customMessage) {
    super(customMessage);
    this.status = status;
    this.customMessage = customMessage;
  }

}
