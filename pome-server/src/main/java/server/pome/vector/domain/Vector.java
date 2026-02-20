package server.pome.vector.domain;

import java.util.List;
import server.pome.vector.infrastructure.vectorstore.VectorDimension;
import server.pome.vector.infrastructure.vectorstore.VectorStoreNamespace;

public record Vector(
    List<Float> values
) {

  public Vector {
  // null, empty 방지
    if (values == null || values.isEmpty()) {
      throw new IllegalArgumentException("vector is empty");
    }
  }

  // 기대 차원과 실제 차원이 다르면 예외 처리
  public Vector validateDimension(int expected) {
    if (values.size() != expected) {
      throw new IllegalArgumentException(
          "vector dimension mismatch: expected=" + expected + "actual=" + values.size()
      );
    }
    return this;
  }

  // 네임스페이스 기반 차원 검증 헬퍼
  public Vector validateDimension(VectorStoreNamespace ns) {
    return validateDimension(VectorDimension.expectedFor(ns));
  }

  // 벡터 내 value 반환
  public List<Float> asList() {
    return values;
  }
}
