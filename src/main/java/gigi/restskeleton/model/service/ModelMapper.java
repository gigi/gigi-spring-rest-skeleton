package gigi.restskeleton.model.service;

import gigi.restskeleton.model.domain.Author;
import gigi.restskeleton.model.domain.Post;
import gigi.restskeleton.model.orm.PostEntity;
import gigi.restskeleton.model.orm.TagEntity;
import gigi.restskeleton.model.orm.UserEntity;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModelMapper {

  @Mapping(target = "name", source = "nickname")
  Author map(UserEntity userEntity);

  Post map(PostEntity postEntity);

  default Set<String> map(Set<TagEntity> entities) {
    return entities.stream().map(TagEntity::getName).collect(Collectors.toSet());
  }
}
