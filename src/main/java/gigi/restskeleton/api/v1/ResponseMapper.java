package gigi.restskeleton.api.v1;

import gigi.restskeleton.api.v1.response.AuthorResponse;
import gigi.restskeleton.api.v1.response.PostResponse;
import gigi.restskeleton.model.domain.Author;
import gigi.restskeleton.model.domain.Post;
import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResponseMapper {

  PostResponse map(Post post);

  List<PostResponse> map(Collection<Post> post);

  AuthorResponse map(Author author);
}
