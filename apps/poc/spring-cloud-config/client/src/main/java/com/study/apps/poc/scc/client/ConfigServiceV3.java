package com.study.apps.poc.scc.client;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "ConstantConditions"})
@Component
@RefreshScope
public class ConfigServiceV3 {
	private Map<String, String> properties = new HashMap<>();

	public ConfigServiceV3(final AbstractEnvironment abstractEnvironment,
						   final DecryptService decryptService) {
		Map<String,String> sources = (Map<String, String>) abstractEnvironment.getPropertySources()
																			  .get("configserver:aws:ssm:parameter:/config/somedomain-dev/")
																			  .getSource();

		for (String key : sources.keySet()) {
			String decryptValue = decryptService.decrypt(key, sources.get(key));
			properties.put(key, decryptValue);
		}
	}

	public Map<String, String> getProperties() {
		return this.properties;
	}

	public boolean isTargetDatasourceA() {
		return Boolean.parseBoolean(getProperties().get("targetdatasourceisa"));
	}
}
