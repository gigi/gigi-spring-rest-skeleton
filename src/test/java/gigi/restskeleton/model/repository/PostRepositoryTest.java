package gigi.restskeleton.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gigi.restskeleton.AbstractDataSourceTest;
import gigi.restskeleton.model.orm.PostEntity;
import gigi.restskeleton.model.orm.TagEntity;
import gigi.restskeleton.model.orm.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

class PostRepositoryTest extends AbstractDataSourceTest {

  @Autowired private PostRepository postRepository;

  @Autowired private EntityManager entityManager;

  @Test
  @Sql("/posts-with-tags.sql")
  @Transactional
  void testFindByTags() {
    List<PostEntity> posts = postRepository.findByTags(List.of("tag1", "tag2"));
    assertThat(posts).hasSize(2);
  }

  @Test
  @Sql("/user.sql")
  @Transactional
  void testSavePostWithTags() {
    PostEntity postEntity =
        new PostEntity(
            UUID.randomUUID(),
            "title",
            "content",
            entityManager.getReference(
                UserEntity.class, UUID.fromString("00000000-0000-1000-8000-100000000001")),
            Instant.now(),
            Set.of(
                new TagEntity(UUID.randomUUID(), "tag1"),
                new TagEntity(UUID.randomUUID(), "tag2")));

    postRepository.saveAndFlush(postEntity);
    entityManager.clear();

    PostEntity savedEntity = entityManager.find(PostEntity.class, postEntity.getId());

    assertThat(postEntity).isNotSameAs(savedEntity);
    assertThat(savedEntity).usingRecursiveComparison().isEqualTo(postEntity);
  }
}
