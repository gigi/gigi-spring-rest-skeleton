package gigi.restskeleton.api.v1.controller;

import gigi.restskeleton.api.v1.response.ApiErrorResponse;
import gigi.restskeleton.model.exception.ApplicationRuntimeException;
import gigi.restskeleton.model.exception.PostNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {
    ApiErrorResponse problemDetail = buildProblemDetail(ex, HttpStatus.BAD_REQUEST);

    if (!ex.hasFieldErrors()) {
      return createResponseEntity(problemDetail, headers, status, request);
    }

    Map<String, List<String>> violationsMap =
        ex.getFieldErrors().stream()
            .collect(
                Collectors.groupingBy(
                    FieldError::getField,
                    Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));

    problemDetail.setProperty("violations", violationsMap);

    return createResponseEntity(problemDetail, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
  }

  @ExceptionHandler({IllegalArgumentException.class, ApplicationRuntimeException.class})
  ApiErrorResponse handleIllegalArgument(IllegalArgumentException exception) {
    return buildProblemDetail(exception, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({PostNotFoundException.class})
  ApiErrorResponse handleNotFound(PostNotFoundException exception) {
    return buildProblemDetail(exception, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({Exception.class})
  ApiErrorResponse handleAll(Exception exception) {
    return buildProblemDetail(exception, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ApiErrorResponse buildProblemDetail(Exception exception, HttpStatus httpStatus) {
    log.error("Application exception caught", exception);

    return ApiErrorResponse.forStatusAndDetail(httpStatus, exception.getMessage());
  }
}
