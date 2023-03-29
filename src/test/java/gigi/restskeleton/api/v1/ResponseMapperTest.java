package gigi.restskeleton.api.v1;

import static org.assertj.core.api.Assertions.assertThat;

import gigi.restskeleton.api.v1.response.AuthorResponse;
import gigi.restskeleton.api.v1.response.PostResponse;
import gigi.restskeleton.model.domain.Author;
import gigi.restskeleton.model.domain.Post;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ResponseMapperTest {

  private final ResponseMapper responseMapper = Mappers.getMapper(ResponseMapper.class);

  @Test
  void testPostMapping() {

    UUID postId = UUID.randomUUID();

    Post source =
        new Post(
            postId,
            "title",
            "content",
            Instant.now(),
            new Author("nickname"),
            Set.of("tag1", "tag2"));

    PostResponse result = responseMapper.map(source);

    PostResponse expected =
        new PostResponse(
            postId.toString(),
            "title",
            "content",
            source.createdAt(),
            new AuthorResponse("nickname"),
            Set.of("tag1", "tag2"));

    assertThat(result).isEqualTo(expected);
  }
}
