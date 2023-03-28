package gigi.restskeleton.api.v1.response;

import java.util.List;

public record GetPostsResponse(List<PostResponse> posts, boolean hasNext) {}
