package com.alextim.myblog.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public String handle(Exception e, Model model) {
        model.addAttribute("title", e.getMessage());
        return "error";
    }
}
