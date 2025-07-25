package server.pome.global.portfolio.type;

public enum TypeEnum {
    EDUCATION(0L),
    EXPERIENCE(1L),
    ACTIVITIES(2L),
    AWARDS(3L),
    QUALIFICATIONS(4L),
    PROJECT(5L),
    ETC(6L);

    private final Long id; 

    TypeEnum(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
