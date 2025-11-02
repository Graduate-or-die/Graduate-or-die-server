package server.pome.global.enums;

// 메이트 신청 상태
public enum MateRequestStatus {
  PENDING,  // 대기
  ACCEPTED, // 수락(매칭)
  REJECTED, // 거절
  UNMATCHED // 매칭 해제
}
