package server.pome.activity.dto.response;

import java.util.List;

public record ActivitySectionResponse(
    List<GetActivityListResponse> items
) {}
