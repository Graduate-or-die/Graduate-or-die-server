package server.pome.project.dto.response;

import java.util.List;

public record ProjectSectionResponse(
    List<GetProjectListResponse> items
) {}
