package server.pome.experience.dto.response;

import java.util.List;

public record ExperienceSectionResponse(
    List<GetExperienceListResponse> items
) {}
