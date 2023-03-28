package gigi.restskeleton.api.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Set;

public record PostResponse(
    @Schema(example = "17066b94-0cc5-4906-a528-d533e0c3cb1d") String id,
    @Schema(example = "Post Title") String title,
    @Schema(example = "Here is content") String content,
    Instant createdAt,
    AuthorResponse author,
    @Schema(example = "[\"tag1\", \"tag2\"]") Set<String> tags) {}
