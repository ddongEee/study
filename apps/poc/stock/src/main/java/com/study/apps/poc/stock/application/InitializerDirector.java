package com.study.apps.poc.stock.application;

import com.study.apps.poc.stock.domain.Initializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitializerDirector {
    private final List<Initializer> initializers;

    public void init() {
        initializers.forEach(initializer -> {
            try {
                initializer.preProcess();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        initializers.forEach(Initializer::afterProcess);
        log.info("Done to Init");
    }
}
