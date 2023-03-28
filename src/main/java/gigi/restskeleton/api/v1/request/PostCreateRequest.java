package gigi.restskeleton.api.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PostCreateRequest(
    @NotBlank @Size(min = 3, max = 20) @Schema(example = "Post Title") String title,
    @Schema(example = "Here is content") String content,
    @Schema(example = "[\"tag1\", \"tag2\"]") @NotNull List<String> tags) {}
