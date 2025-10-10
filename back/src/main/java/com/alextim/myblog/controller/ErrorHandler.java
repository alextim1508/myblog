package com.alextim.myblog.controller;

import com.alextim.myblog.exception.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle(ClientException e, Model model) {
        log.error("ClientException", e);
        model.addAttribute("title", e.getClass().getName());
        return "error";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle(MethodArgumentNotValidException e, Model model) {
        log.error("MethodArgumentNotValidException", e);
        model.addAttribute("title", e.getClass().getName());
        return "error";
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handle(Exception e, Model model) {
        log.error("", e);
        model.addAttribute("title", e.getClass().getName());
        return "error";
    }
}
