package fete.be.global.exception;

import fete.be.global.util.ApiResponse;
import fete.be.global.util.ResponseMessage;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ApiResponse handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("[exceptionHandle] NoResourceFoundException", e);
        return new ApiResponse(ResponseMessage.NO_RESOURCE_ERROR.getCode(), ResponseMessage.NO_RESOURCE_ERROR.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse handleException(Exception e) {
        log.error("[exceptionHandle] Exception", e);
        return new ApiResponse(ResponseMessage.INTERNAL_ERROR.getCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("[exceptionHandle] DataIntegrityViolationException", e);
        return new ApiResponse(ResponseMessage.BAD_REQUEST.getCode(), ResponseMessage.BAD_REQUEST.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse handleConstraintViolationException(ConstraintViolationException e) {
        log.error("[exceptionHandle] ConstraintViolationException", e);

        // ConstraintViolation 객체에서 오류 메시지만 추출
        String errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.joining(", "));

        return new ApiResponse(ResponseMessage.BAD_CONSTRAINT.getCode(), errorMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("[exceptionHandle] MethodArgumentNotValidException", e);

        // fieldError에서 오류 메시지만 추출
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ApiResponse(ResponseMessage.BAD_CONSTRAINT.getCode(), errorMessage);
    }
}
