package com.study.apps.poc.scc.client;

import com.amazonaws.util.StringUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "cloud-config")
public class ConfigServiceV2 {
	private Map<String, String> properties;
	private Map<String, String> decryptedProperties;

	public ConfigServiceV2(final Map<String, String> properties) {
		this.properties = properties;
		System.out.println(this.properties);
	}

	/**
	 * 아래 2가지 상황에서 메소드 호출됨
	 * 1. 최초에 객체 생성
	 * 2. /actuator/refresh 로 configuration 의 변경이 반영되고 나서도 호출됨.
	 */
	@PostConstruct
	public void afterRefreshed() {
//		String password = decryptService.decrypt(properties.get("password"));
//		System.out.println(password);
		validateProperties(this.properties); // 1. properties validation
		this.decryptedProperties = decryptedProperties(this.properties);
	}

	private void validateProperties(Map<String, String> properties) {
		for (String key : properties.keySet()) {
			String value = properties.get(key);
			if (StringUtils.isNullOrEmpty(value)) {
				// 장애 방지를 위해 ERROR 로깅만?!
//				throw new RuntimeException("선언된 키값 ["+key+"] 에 대해서 값을 로딩하지 못했습니다. AWS parameter store 를 확인해보세요.");
			}
		}
	}

	private Map<String, String> decryptedProperties(Map<String, String> properties) {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}
