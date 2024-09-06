package fete.be.global.exception;

import fete.be.global.util.ApiResponse;
import fete.be.global.util.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
}
