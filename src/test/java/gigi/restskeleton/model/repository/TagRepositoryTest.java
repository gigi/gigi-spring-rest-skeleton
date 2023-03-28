package gigi.restskeleton.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gigi.restskeleton.AbstractDataSourceTest;
import gigi.restskeleton.model.orm.TagEntity;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

class TagRepositoryTest extends AbstractDataSourceTest {

  @Autowired private TagRepository tagRepository;

  @Test
  @Sql("/posts-with-tags.sql")
  @Transactional
  void testFindByNames() {
    List<TagEntity> tags = tagRepository.findByNameIn(List.of("tag1", "tag2"));
    assertThat(tags).hasSize(2);
  }
}
