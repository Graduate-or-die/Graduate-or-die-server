package server.pome.etc.dto.response;

import java.util.List;

public record EtcSectionResponse(
    List<GetEtcListResponse> items
) {}
