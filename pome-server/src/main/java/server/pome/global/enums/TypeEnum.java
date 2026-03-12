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
    EDUCATIONS(1L, "educations"),
    EXPERIENCES(2L, "experiences"),
    ACTIVITIES(3L, "activities"),
    AWARDS(4L, "awards"),
    QUALIFICATIONS(5L, "qualifications"),
    PROJECTS(6L, "projects"),
    ETCS(7L, "etcs");

    private final Long id;
    private final String s3Dir;

    public static TypeEnum fromId(Long id) {
        if (id == null) {
            throw new BaseException(BaseResponseStatus.INVALID_TYPE_ENUM);
        }
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
