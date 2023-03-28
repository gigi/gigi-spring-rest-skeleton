package gigi.restskeleton.model.domain;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record Post(
    UUID id, String title, String content, Instant createdAt, Author author, Set<String> tags) {}
