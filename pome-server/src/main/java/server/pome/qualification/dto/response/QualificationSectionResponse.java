package server.pome.qualification.dto.response;

import java.util.List;

public record QualificationSectionResponse(
    List<GetQualificationListResponse> items
) {}
