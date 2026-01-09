package server.pome.award.dto.response;

import java.util.List;

public record AwardSectionResponse(
    List<GetAwardListResponse> items
) {}
