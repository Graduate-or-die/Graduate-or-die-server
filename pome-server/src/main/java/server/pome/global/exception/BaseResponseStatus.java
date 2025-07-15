package server.pome.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseResponseStatus {

  // 1000번대: 성공
  SUCCESS(true, HttpStatus.OK, 1000, "요청에 성공하였습니다."),

  // 2000번대: 요청 오류
  REQUEST_ERROR(false, HttpStatus.BAD_REQUEST, 2000, "입력값을 확인해주세요."),
  INVALID_USER(false, HttpStatus.UNAUTHORIZED, 2001, "유효하지 않은 사용자입니다."),
  INVALID_TOKEN(false, HttpStatus.UNAUTHORIZED, 2002, "유효하지 않은 토큰입니다."),

  // 3000번대: 응답 오류
  RESPONSE_ERROR(false, HttpStatus.BAD_REQUEST, 3000, "값을 불러오는데 실패하였습니다."),

  // 4000번대: DB/서버 오류
  DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR, 4000, "데이터베이스 연결에 실패하였습니다."),
  SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR, 4001, "서버와의 연결에 실패하였습니다.");

  private final boolean isSuccess;
  private final HttpStatus httpStatus;
  private final int code;
  private final String message;
}
