package com.study.apps.poc.scc.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ConfigController {
    private final ConfigServiceV3 configService;
    private final RefreshScopeTester refreshScopeTester;
    private final SomeConfiguration.DataSourceProxy dataSourceProxy;

    @Autowired
	public ConfigController(final ConfigServiceV3 configService,
                            final RefreshScopeTester refreshScopeTester,
                            final SomeConfiguration.DataSourceProxy dataSourceProxy) {
		this.configService = configService;
		this.refreshScopeTester = refreshScopeTester;
		this.dataSourceProxy = dataSourceProxy;
	}

    @RequestMapping("/")
    Map<String, String> hello() {
        refreshScopeTester.good();
        return configService.getProperties();
    }

    @RequestMapping("/targetDataSource")
    public String targetDataSource() {
        return dataSourceProxy.activeDataSource();
    }
}
