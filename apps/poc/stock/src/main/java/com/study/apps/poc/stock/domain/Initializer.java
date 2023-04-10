package com.study.apps.poc.stock.domain;

import java.io.IOException;

public interface Initializer {
    void preProcess() throws IOException, InterruptedException;
    void afterProcess();
}
