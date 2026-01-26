package server.pome.education.dto.response;

import java.util.List;

public record EducationSectionResponse(
    List<GetEducationListResponse> items
) {}
