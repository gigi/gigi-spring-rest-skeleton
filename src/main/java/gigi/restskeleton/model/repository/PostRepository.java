package gigi.restskeleton.model.repository;

import gigi.restskeleton.model.orm.PostEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<PostEntity, UUID> {
  @Query("SELECT post FROM PostEntity post LEFT JOIN FETCH post.tags tag WHERE tag.name IN :tags")
  List<PostEntity> findByTags(Collection<String> tags);

  @Query(value = "SELECT p.id FROM PostEntity p ORDER BY p.createdAt DESC")
  Slice<UUID> findLatestPosts(Pageable pageable);

  @Query(value = "SELECT p FROM PostEntity p WHERE p.id IN (:ids) ORDER BY p.createdAt DESC")
  @EntityGraph(attributePaths = {"tags", "author"})
  List<PostEntity> findLatestPostsByIds(List<UUID> ids);

  @Override
  @EntityGraph(attributePaths = {"tags", "author"})
  Optional<PostEntity> findById(@NonNull UUID id);
}
