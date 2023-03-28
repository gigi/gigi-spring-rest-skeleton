package gigi.restskeleton.model.exception;

import java.util.NoSuchElementException;

public class PostNotFoundException extends NoSuchElementException {

  public PostNotFoundException() {
    super("Post not found");
  }
}
