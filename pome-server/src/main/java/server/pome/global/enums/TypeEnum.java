package server.pome.global.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor

public enum TypeEnum {
    EDUCATIONS(1L),
    EXPERIENCES(2L),
    ACTIVITIES(3L),
    AWARDS(4L),
    QUALIFICATIONS(5L),
    PROJECTS(6L),
    ETCS(7L);

    private final Long id;

    public static TypeEnum fromId(Long id) {
        for (TypeEnum t : values()) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        throw new BaseException(BaseResponseStatus.INVALID_TYPE_ENUM);
    }

    // 기본 구조 (비공개)
    public static Map<Long, Boolean> defaultVisibilityMap() {
        Map<Long, Boolean> m = new HashMap<>();
        for (TypeEnum t : values()) m.put(t.getId(), false);
        return m;
    }
}
