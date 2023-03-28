package gigi.restskeleton.model.service;

import gigi.restskeleton.infrastructure.springdata.OffsetBasedPageRequest;
import gigi.restskeleton.model.domain.Post;
import gigi.restskeleton.model.exception.PostNotFoundException;
import gigi.restskeleton.model.orm.PostEntity;
import gigi.restskeleton.model.orm.TagEntity;
import gigi.restskeleton.model.orm.UserEntity;
import gigi.restskeleton.model.repository.PostRepository;
import gigi.restskeleton.model.repository.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.time.Clock;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;

  private final TagRepository tagRepository;

  private final UserService userService;
  private final ModelMapper modelMapper;

  private final EntityManager entityManager;

  private final Clock clock;

  // we need two queries to handle pagination and sorting properly in hibernate < 6.2
  // https://github.com/spring-projects/spring-data-jpa/issues/2744
  // https://vladmihalcea.com/fix-hibernate-hhh000104-entity-fetch-pagination-warning-message/
  // https://stackoverflow.com/questions/64799564/spring-data-jpa-pagination-hhh000104
  public Slice<Post> getPosts(int offset, int limit) {
    OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(offset, limit);
    Slice<UUID> idsSlice = postRepository.findLatestPosts(pageable);
    if (!idsSlice.hasContent()) {
      return new SliceImpl<>(List.of(), pageable, idsSlice.hasNext());
    }

    List<PostEntity> entities = postRepository.findLatestPostsByIds(idsSlice.getContent());
    List<Post> posts = entities.stream().map(modelMapper::map).toList();

    return new SliceImpl<>(posts, pageable, idsSlice.hasNext());
  }

  public Post getPost(UUID id) throws PostNotFoundException {
    PostEntity post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    return modelMapper.map(post);
  }

  @Transactional
  public Post createPost(String title, String content, @NotNull Collection<String> tags) {
    PostEntity postEntity =
        new PostEntity(
            UUID.randomUUID(),
            title,
            content,
            entityManager.getReference(UserEntity.class, userService.getCurrentUserId()),
            clock.instant(),
            obtainTagEntities(tags));
    PostEntity savedPost = postRepository.save(postEntity);
    return modelMapper.map(savedPost);
  }

  @Transactional
  public void updatePost(UUID id, String title, String content, Collection<String> tags)
      throws PostNotFoundException {
    PostEntity entity = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    entity.setTitle(title);
    entity.setContent(content);
    entity.setTags(obtainTagEntities(tags));
    postRepository.save(entity);
  }

  private Set<TagEntity> obtainTagEntities(Collection<String> tags) {
    List<TagEntity> tagEntities = tagRepository.findByNameIn(tags);
    Map<String, TagEntity> tagEntityMap =
        tagEntities.stream().collect(Collectors.toMap(TagEntity::getName, Function.identity()));
    tags.forEach(
        tag -> {
          if (!tagEntityMap.containsKey(tag)) {
            tagEntityMap.put(tag, new TagEntity(UUID.randomUUID(), tag));
          }
        });

    return new HashSet<>(tagEntityMap.values());
  }
}
