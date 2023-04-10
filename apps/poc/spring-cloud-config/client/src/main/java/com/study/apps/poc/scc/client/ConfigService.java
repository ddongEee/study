package com.study.apps.poc.scc.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RefreshScope
public class ConfigService {
	private final AbstractEnvironment abstractEnvironment;
	private final String username;
	private final String password;

	public ConfigService(@Value("${username}") String username,
						 @Value("${password}") String password,
						 AbstractEnvironment abstractEnvironment){
		this.username = username;
		this.password = password;
		this.abstractEnvironment= abstractEnvironment;
	}

	public Map<String, String> getConfig() {
		Map<String, String> map = new HashMap<>();
		map.put("username", username);
		map.put("password", password);
		return map;
	}
}
