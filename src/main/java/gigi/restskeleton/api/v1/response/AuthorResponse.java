package gigi.restskeleton.api.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthorResponse(@Schema(example = "admin") String name) {}
