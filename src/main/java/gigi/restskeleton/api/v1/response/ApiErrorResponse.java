package gigi.restskeleton.api.v1.response;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.util.Assert;

public class ApiErrorResponse extends ProblemDetail {

  public static ApiErrorResponse forStatusAndDetail(HttpStatusCode status, String detail) {
    Assert.notNull(status, "HttpStatusCode is required");
    ApiErrorResponse error = new ApiErrorResponse();
    error.setStatus(status.value());
    error.setDetail(detail);

    return error;
  }
}
