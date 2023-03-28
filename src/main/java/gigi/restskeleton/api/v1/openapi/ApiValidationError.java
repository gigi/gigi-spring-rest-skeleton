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
    responseCode = "422",
    description = "Validation error",
    content =
        @Content(
            mediaType = "application/problem+json",
            examples = {
              @ExampleObject(
                  """
                        {
                          "type": "about:blank",
                          "title": "Bad Request",
                          "status": 422,
                          "detail": "Validation failed for argument [0] in public void gigi.restskeleton.api.v1.controller.PostController.updatePost(gigi.restskeleton.api.v1.request.PostUpdateRequest,java.util.UUID) with 2 errors: [Field error in object 'postUpdateRequest' on field 'title': rejected value []; codes [NotBlank.postUpdateRequest.title,NotBlank.title,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [postUpdateRequest.title,title]; arguments []; default message [title]]; default message [must not be blank]] [Field error in object 'postUpdateRequest' on field 'title': rejected value []; codes [Size.postUpdateRequest.title,Size.title,Size.java.lang.String,Size]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [postUpdateRequest.title,title]; arguments []; default message [title],20,3]; default message [size must be between 3 and 20]] ",
                          "instance": "/api/v1/posts/119bf0be-1f6e-4df0-8efb-b7affeee4703",
                          "violations": {
                            "title": [
                              "must not be blank",
                              "size must be between 3 and 20"
                            ]
                          }
                        }
                          """)
            },
            schema = @Schema(implementation = ApiErrorResponse.class)))
public @interface ApiValidationError {}
