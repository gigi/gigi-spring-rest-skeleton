package gigi.restskeleton.model.repository;

import gigi.restskeleton.model.orm.TagEntity;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<TagEntity, UUID> {
  List<TagEntity> findByNameIn(Collection<String> names);
}
