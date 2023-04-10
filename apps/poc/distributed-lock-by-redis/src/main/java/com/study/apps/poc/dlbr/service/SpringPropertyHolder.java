package com.study.apps.poc.dlbr.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SpringPropertyHolder {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    public boolean isLocal() {
        return activeProfile.equals("local");
    }
}
