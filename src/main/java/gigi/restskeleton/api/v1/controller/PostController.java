package gigi.restskeleton.api.v1.controller;

import gigi.restskeleton.api.v1.ResponseMapper;
import gigi.restskeleton.api.v1.openapi.ApiCommon;
import gigi.restskeleton.api.v1.openapi.ApiNotFound;
import gigi.restskeleton.api.v1.openapi.ApiValidationError;
import gigi.restskeleton.api.v1.request.PostCreateRequest;
import gigi.restskeleton.api.v1.request.PostUpdateRequest;
import gigi.restskeleton.api.v1.response.GetPostsResponse;
import gigi.restskeleton.api.v1.response.PostResponse;
import gigi.restskeleton.model.domain.Post;
import gigi.restskeleton.model.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@ApiCommon
@Tag(name = "post", description = "the Post API")
public class PostController {

  private static final Integer DEFAULT_LIMIT = 10;

  private final PostService postService;
  private final ResponseMapper responseMapper;

  @PostMapping("/posts")
  @Operation(summary = "Create post")
  @ApiValidationError
  public PostResponse createPost(@Valid @RequestBody PostCreateRequest request) {
    Post post = postService.createPost(request.title(), request.content(), request.tags());
    return responseMapper.map(post);
  }

  @GetMapping(value = "/posts")
  @Operation(summary = "Get posts")
  public GetPostsResponse getPosts(Optional<Integer> offset, Optional<Integer> limit) {
    Slice<Post> postSlice = postService.getPosts(offset.orElse(0), limit.orElse(DEFAULT_LIMIT));
    List<PostResponse> posts = responseMapper.map(postSlice.getContent());

    return new GetPostsResponse(posts, postSlice.hasNext());
  }

  @GetMapping("/posts/{id}")
  @Operation(summary = "Get post by id")
  @ApiNotFound
  public PostResponse getPost(@PathVariable UUID id) {
    Post post = postService.getPost(id);
    return responseMapper.map(post);
  }

  @PutMapping("/posts/{id}")
  @Operation(summary = "Update post by id")
  @ApiNotFound
  @ApiValidationError
  public void updatePost(@Valid @RequestBody PostUpdateRequest request, @PathVariable UUID id) {
    postService.updatePost(id, request.title(), request.content(), request.tags());
  }
}
