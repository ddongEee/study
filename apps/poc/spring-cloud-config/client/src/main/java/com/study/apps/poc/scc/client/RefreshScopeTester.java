package com.study.apps.poc.scc.client;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class RefreshScopeTester {
    public RefreshScopeTester() {
        System.out.println("hello");
    }

    public void good() {
        System.out.println("good");
    }
}
