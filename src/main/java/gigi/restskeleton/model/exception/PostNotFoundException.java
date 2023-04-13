package gigi.restskeleton.model.exception;

public class PostNotFoundException extends ApplicationRuntimeException {

  public PostNotFoundException() {
    super("Post not found");
  }
}
