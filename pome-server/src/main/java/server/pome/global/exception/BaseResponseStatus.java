package server.pome.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseStatus {

  // 1000번대: 성공
  SUCCESS(true, HttpStatus.OK, 1000, "요청에 성공하였습니다."),

  // 2000번대: 요청 오류
  REQUEST_ERROR(false, HttpStatus.BAD_REQUEST, 2000, "입력값을 확인해주세요."),
  INVALID_USER(false, HttpStatus.UNAUTHORIZED, 2001, "유효하지 않은 사용자입니다."),
  INVALID_TOKEN(false, HttpStatus.UNAUTHORIZED, 2002, "유효하지 않은 토큰입니다."),

  // users
  USER_NOT_FOUND(false, HttpStatus.NOT_FOUND, 2003, "존재하지 않는 사용자입니다."),
  DUPLICATE_USER(false, HttpStatus.BAD_REQUEST, 2004, "중복된 닉네임입니다."),

  // portfolios
  INVALID_TYPE_ENUM(false, HttpStatus.BAD_REQUEST, 2005, "존재하지 않는 TypeEnum입니다."),

  // education
  EDUCATION_NOT_FOUND(false, HttpStatus.NOT_FOUND, 2006, "해당 유저의 학력을 찾을 수 없습니다."),
  DUPLICATE_EDUCATION(false, HttpStatus.BAD_REQUEST, 2007, "해당 유저의 학력이 이미 존재합니다."),

  // experience
  INVALID_DATE_RANGE(false, HttpStatus.BAD_REQUEST, 2008, "마감일이 시작일보다 빠릅니다"),
  EXPERIENCE_NOT_FOUND(false, HttpStatus.NOT_FOUND, 2009, "해당 경력을 찾을 수 없습니다."),

  // activity
  ACTIVITY_NOT_FOUND(false, HttpStatus.NOT_FOUND, 2020, "해당 활동을 찾을 수 없습니다."),

  // award
  AWARD_NOT_FOUND(false, HttpStatus.NOT_FOUND, 2025, "해당 수상 경력을 찾을 수 없습니다."),

  // mate
  CANNOT_MATE_SELF_REQUEST(false, HttpStatus.BAD_REQUEST, 2010, "본인에게는 메이트 신청을 할 수 없습니다."),
  ALREADY_HAVE_MATE(false, HttpStatus.BAD_REQUEST, 2011, "해당 유저는 이미 메이트가 있습니다."),
  ALREADY_REQUEST_MATE(false, HttpStatus.BAD_REQUEST, 2012, "해당 유저에게 이미 메이트를 신청했습니다."),
  PROPOSER_NOT_FOUND(false, HttpStatus.BAD_REQUEST, 2013, "신청자 리스트에 메이트가 존재하지 않습니다."),
  NOT_MATCHED_MATE(false, HttpStatus.BAD_REQUEST, 2014, "매칭된 메이트가 아닙니다."),
  CONFLICT_STATE(false, HttpStatus.BAD_REQUEST, 2015, "상태가 불일치하여 해제 실패하였습니다."),
  MATCHING_DISABLED(false, HttpStatus.BAD_REQUEST, 2016, "매칭 비활성화 상태입니다."),

  // like
  CANNOT_LIKE_SELF(false, HttpStatus.BAD_REQUEST, 2030, "본인에게는 좋아요를 누를 수 없습니다."),
  ALREADY_LIKED(false, HttpStatus.BAD_REQUEST, 2031, "이미 좋아요를 누른 유저입니다."),
  ALREADY_UNLIKED(false, HttpStatus.BAD_REQUEST, 2032, "이미 좋아요 취소를 누른 유저입니다."),

  // 3000번대: 응답 오류
  RESPONSE_ERROR(false, HttpStatus.BAD_REQUEST, 3000, "값을 불러오는데 실패하였습니다."),

  // 4000번대: 서버/DB 오류
  DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR, 4000, "데이터베이스 연결에 실패하였습니다."),
  SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR, 4001, "서버와의 연결에 실패하였습니다.");
  private final boolean isSuccess;
  private final HttpStatus httpStatus;
  private final int code;
  private final String message;

  BaseResponseStatus(boolean isSuccess, HttpStatus httpStatus, int code, String message) {
    this.isSuccess = isSuccess;
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
