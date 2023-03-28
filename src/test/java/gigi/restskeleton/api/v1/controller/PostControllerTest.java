package gigi.restskeleton.api.v1.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gigi.restskeleton.api.v1.request.PostCreateRequest;
import gigi.restskeleton.api.v1.request.PostUpdateRequest;
import gigi.restskeleton.api.v1.response.ApiErrorResponse;
import gigi.restskeleton.api.v1.response.GetPostsResponse;
import gigi.restskeleton.api.v1.response.PostResponse;
import gigi.restskeleton.model.domain.Author;
import gigi.restskeleton.model.domain.Post;
import gigi.restskeleton.model.exception.PostNotFoundException;
import gigi.restskeleton.model.service.PostService;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@EnableAutoConfiguration(
    exclude = {LiquibaseAutoConfiguration.class, DataSourceAutoConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostControllerTest {

  @Autowired protected TestRestTemplate testRestTemplate;

  @MockBean protected PostService postService;

  @MockBean Clock clock;

  @Test
  void testCreatePost() {
    HttpEntity<PostCreateRequest> request =
        new HttpEntity<>(new PostCreateRequest("title", "content", List.of("tag1", "tag2")));
    Instant now = Instant.now();
    Post post = createPost(UUID.randomUUID(), now);
    when(postService.createPost("title", "content", List.of("tag1", "tag2"))).thenReturn(post);

    ResponseEntity<PostResponse> responseEntity =
        testRestTemplate.postForEntity("/api/v1/posts", request, PostResponse.class);

    assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
    PostResponse response = responseEntity.getBody();
    assertThat(response).isNotNull();
    assertThat(response.id()).isNotNull();
    assertThat(response.title()).isEqualTo("title");
    assertThat(response.content()).isEqualTo("content");
    assertThat(response.createdAt()).isEqualTo(now);
    assertThat(response.tags()).contains("tag1", "tag2");
    assertThat(response.author().name()).isEqualTo("nickname");
  }

  @Test
  void testCreatePostWhenTitleEmpty() {
    HttpEntity<PostCreateRequest> request =
        new HttpEntity<>(new PostCreateRequest("", "content", List.of("tag1", "tag2")));
    ResponseEntity<ApiErrorResponse> responseEntity =
        testRestTemplate.postForEntity("/api/v1/posts", request, ApiErrorResponse.class);

    assertThat(responseEntity.getStatusCode().is4xxClientError()).isTrue();

    ApiErrorResponse body = responseEntity.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getStatus()).isEqualTo(400);
    assertThat(body.getProperties()).isNotNull();

    Map<String, Object> properties = body.getProperties();
    assertThat(properties).isNotNull();
    assertThat(properties.get("violations")).isInstanceOf(Map.class);

    @SuppressWarnings("unchecked")
    Map<String, List<String>> map = (Map<String, List<String>>) properties.get("violations");

    assertThat(map).isNotNull();
    assertThat(map.get("title")).contains("must not be blank", "size must be between 3 and 20");
  }

  @Test
  void testGetPost() {
    UUID id = UUID.randomUUID();
    Instant now = Instant.now();
    Post post = createPost(id, now);
    when(postService.getPost(id)).thenReturn(post);

    ResponseEntity<PostResponse> responseEntity =
        testRestTemplate.getForEntity(
            "/api/v1/posts/{id}", PostResponse.class, Collections.singletonMap("id", id));

    assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
    PostResponse response = responseEntity.getBody();
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(id.toString());
    assertThat(response.title()).isEqualTo("title");
    assertThat(response.content()).isEqualTo("content");
    assertThat(response.createdAt()).isEqualTo(now);
    assertThat(response.tags()).contains("tag1", "tag2");
    assertThat(response.author().name()).isEqualTo("nickname");
  }

  @Test
  void testGetPostNotFound() {
    UUID id = UUID.randomUUID();
    when(postService.getPost(id)).thenThrow(new PostNotFoundException());

    ResponseEntity<ApiErrorResponse> responseEntity =
        testRestTemplate.getForEntity(
            "/api/v1/posts/{id}", ApiErrorResponse.class, Collections.singletonMap("id", id));

    assertThat(responseEntity.getStatusCode().is4xxClientError()).isTrue();
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().getDetail()).isEqualTo("Post not found");
    assertThat(responseEntity.getBody().getStatus()).isEqualTo(404);
  }

  @Test
  void testGetPostWhenException() {
    UUID id = UUID.randomUUID();
    when(postService.getPost(id)).thenThrow(new RuntimeException("Error"));

    ResponseEntity<ApiErrorResponse> responseEntity =
        testRestTemplate.getForEntity(
            "/api/v1/posts/{id}", ApiErrorResponse.class, Collections.singletonMap("id", id));

    assertThat(responseEntity.getStatusCode().is5xxServerError()).isTrue();
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().getDetail()).isEqualTo("Error");
    assertThat(responseEntity.getBody().getStatus()).isEqualTo(500);
  }

  @Test
  void testGetPosts() {
    Slice<Post> slice =
        new SliceImpl<>(
            List.of(
                createPost(UUID.randomUUID(), Instant.now()),
                createPost(UUID.randomUUID(), Instant.now())));
    when(postService.getPosts(7, 5)).thenReturn(slice);

    Map<String, Integer> urlVariables = Map.of("limit", 5, "offset", 7);
    ResponseEntity<GetPostsResponse> responseEntity =
        testRestTemplate.getForEntity(
            "/api/v1/posts?limit={limit}&offset={offset}", GetPostsResponse.class, urlVariables);

    assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
    GetPostsResponse response = responseEntity.getBody();
    assertThat(response).isNotNull();
    assertThat(response.posts()).hasSize(2);
  }

  @Test
  void testUpdatePost() {
    UUID id = UUID.randomUUID();
    ResponseEntity<Void> responseEntity =
        testRestTemplate.exchange(
            "/api/v1/posts/{id}",
            HttpMethod.PUT,
            new HttpEntity<>(new PostUpdateRequest("title", "content", List.of("tag1", "tag2"))),
            Void.class,
            Collections.singletonMap("id", id));

    when(clock.instant()).thenReturn(Instant.now());
    verify(postService).updatePost(id, "title", "content", List.of("tag1", "tag2"));

    assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Test
  void testUpdateWhenPostNotFound() {
    UUID id = UUID.randomUUID();
    doThrow(new PostNotFoundException()).when(postService).updatePost(any(), any(), any(), any());

    ResponseEntity<ApiErrorResponse> responseEntity =
        testRestTemplate.exchange(
            "/api/v1/posts/{id}",
            HttpMethod.PUT,
            new HttpEntity<>(new PostUpdateRequest("title", "content", List.of("tag1", "tag2"))),
            ApiErrorResponse.class,
            Collections.singletonMap("id", id));

    assertThat(responseEntity.getStatusCode().is4xxClientError()).isTrue();
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().getDetail()).isEqualTo("Post not found");
    assertThat(responseEntity.getBody().getStatus()).isEqualTo(404);
  }

  private Post createPost(UUID id, Instant now) {
    return new Post(id, "title", "content", now, new Author("nickname"), Set.of("tag1", "tag2"));
  }
}
