package gigi.restskeleton.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gigi.restskeleton.infrastructure.springdata.OffsetBasedPageRequest;
import gigi.restskeleton.model.domain.Post;
import gigi.restskeleton.model.exception.PostNotFoundException;
import gigi.restskeleton.model.orm.PostEntity;
import gigi.restskeleton.model.orm.TagEntity;
import gigi.restskeleton.model.orm.UserEntity;
import gigi.restskeleton.model.repository.PostRepository;
import gigi.restskeleton.model.repository.TagRepository;
import jakarta.persistence.EntityManager;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

class PostServiceTest {

  private final PostRepository postRepository = mock(PostRepository.class);
  private final TagRepository tagRepository = mock(TagRepository.class);
  private final UserService userService = mock(UserService.class);
  private final ModelMapper modelMapper = mock(ModelMapper.class);
  private final EntityManager entityManager = mock(EntityManager.class);
  private final Clock clock = mock(Clock.class);
  private PostService postService;

  @BeforeEach
  void setUp() {
    postService =
        new PostService(
            postRepository, tagRepository, userService, modelMapper, entityManager, clock);
  }

  @Test
  void testGetPosts() {
    OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(5, 7);
    List<UUID> listOfIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    Slice<UUID> idsSlice = new SliceImpl<>(listOfIds, pageable, false);
    List<PostEntity> entities = List.of(mock(PostEntity.class), mock(PostEntity.class));

    when(postRepository.findLatestPosts(pageable)).thenReturn(idsSlice);
    when(postRepository.findLatestPostsByIds(listOfIds)).thenReturn(entities);
    when(modelMapper.map(any(PostEntity.class)))
        .thenReturn(new Post(UUID.randomUUID(), "", "", Instant.now(), null, Set.of()));

    Slice<Post> posts = postService.getPosts(5, 7);
    assertThat(posts.getContent()).hasSize(2);
    assertThat(posts.isLast()).isTrue();
  }

  @Test
  void testGetPost() {
    UUID id = UUID.randomUUID();
    when(postRepository.findById(id)).thenReturn(Optional.of(mock(PostEntity.class)));
    when(modelMapper.map(any(PostEntity.class)))
        .thenReturn(new Post(id, "", "", Instant.now(), null, Set.of()));

    Post post = postService.getPost(id);
    assertThat(post.id()).isEqualTo(id);
  }

  @Test
  void testGetPostWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(postRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(PostNotFoundException.class, () -> postService.getPost(id));
  }

  @Test
  void testCreatePost() {
    UUID tag1Id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    when(clock.instant()).thenReturn(Instant.now());
    when(tagRepository.findByNameIn(anyList())).thenReturn(List.of(new TagEntity(tag1Id, "tag1")));

    ArgumentMatcher<PostEntity> entityMatcher =
        post -> {
          assertThat(post.getId()).isNotNull();
          assertThat(post.getCreatedAt()).isNotNull();
          assertThat(post.getTitle()).isEqualTo("title");
          assertThat(post.getContent()).isEqualTo("content");
          assertThat(post.getTags()).hasSize(2);
          assertThat(post.getTags().stream().anyMatch(tag -> tag.getId().equals(tag1Id))).isTrue();
          return true;
        };

    PostEntity postEntity =
        new PostEntity(UUID.randomUUID(), "title", "content", null, Instant.now(), Set.of());

    when(postRepository.save(argThat(entityMatcher))).thenReturn(postEntity);
    when(userService.getCurrentUserId()).thenReturn(userId);
    when(modelMapper.map(postEntity))
        .thenReturn(new Post(UUID.randomUUID(), "", "", Instant.now(), null, Set.of()));

    postService.createPost("title", "content", List.of("tag1", "tag2"));

    verify(entityManager).getReference(UserEntity.class, userId);
    verify(modelMapper).map(postEntity);
  }

  @Test
  void testUpdatePost() {
    UUID postId = UUID.randomUUID();
    UUID tag1Id = UUID.randomUUID();
    when(clock.instant()).thenReturn(Instant.now());
    when(postRepository.findById(postId))
        .thenReturn(
            Optional.of(
                new PostEntity(postId, "oldTitle", "oldContent", null, Instant.now(), Set.of())));
    when(tagRepository.findByNameIn(anyList())).thenReturn(List.of(new TagEntity(tag1Id, "tag1")));
    postService.updatePost(postId, "title", "content", List.of("tag1", "tag2"));

    ArgumentMatcher<PostEntity> entityMatcher =
        post -> {
          assertThat(post.getId()).isNotNull();
          assertThat(post.getCreatedAt()).isNotNull();
          assertThat(post.getTitle()).isEqualTo("title");
          assertThat(post.getContent()).isEqualTo("content");
          assertThat(post.getTags()).hasSize(2);
          assertThat(post.getTags().stream().anyMatch(tag -> tag.getId().equals(tag1Id))).isTrue();
          return true;
        };

    verify(postRepository).save(argThat(entityMatcher));
  }

  @Test
  void testUpdatePostWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(postRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(PostNotFoundException.class, () -> postService.updatePost(id, "", "", null));
  }
}
