package com.family.bill.config;

import com.family.bill.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器
 * 
 * @author family-bill
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：", e);
        return Result.error(e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error("系统异常，请联系管理员");
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errorMsg.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        return Result.error(400, errorMsg.toString());
    }
    
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errorMsg.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        return Result.error(400, errorMsg.toString());
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        StringBuilder errorMsg = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            errorMsg.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
        }
        return Result.error(400, errorMsg.toString());
    }
}


