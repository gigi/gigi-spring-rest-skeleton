package gigi.restskeleton.api.v1.openapi;

import gigi.restskeleton.api.v1.response.ApiErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ApiResponse(
    responseCode = "404",
    description = "Not found",
    content =
        @Content(
            mediaType = "application/problem+json",
            examples = {
              @ExampleObject(
                  """
                        {
                          "type": "about:blank",
                          "title": "Not Found",
                          "status": 404,
                          "detail": "Post not found",
                          "instance": "/api/v1/posts/17066b94-0cc5-4906-a528-d533e0c3cb1d"
                        }
                          """)
            },
            schema = @Schema(implementation = ApiErrorResponse.class)))
public @interface ApiNotFound {}
