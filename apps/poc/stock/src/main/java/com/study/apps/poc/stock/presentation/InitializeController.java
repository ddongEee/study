package com.study.apps.poc.stock.presentation;

import com.study.apps.poc.stock.application.InitializerDirector;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/init")
@RequiredArgsConstructor
public class InitializeController {
    private final InitializerDirector initializerDirector;
    @PutMapping
    public void init() {
        initializerDirector.init();
    }
}
