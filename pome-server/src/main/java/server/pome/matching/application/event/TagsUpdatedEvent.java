package server.pome.matching.application.event;

public record TagsUpdatedEvent(
    long userId,
    String version
) {}
