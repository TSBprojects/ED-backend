package ru.sstu.vak.edbackend.config;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.sstu.vak.edbackend.dto.ExceptionResponse;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlingWrapper {


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleOtherExceptions(HttpServletRequest request, Exception ex) {
        logException(ex);
        return new ExceptionResponse("Something went wrong: "+ex.getMessage());
    }

    private void logException(Exception ex) {
        log.error("Got exception: " + ex.getClass().getSimpleName(), ex);
    }
}
