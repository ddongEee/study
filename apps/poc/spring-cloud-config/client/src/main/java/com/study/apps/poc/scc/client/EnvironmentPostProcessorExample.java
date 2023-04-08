package com.study.apps.poc.scc.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

//https://blog.kingbbode.com/39
@Configuration
public class EnvironmentPostProcessorExample implements EnvironmentPostProcessor {

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println(environment.getPropertySources());
//        Resource path = new ClassPathResource("com/example/myapp/config.yml");
//        PropertySource<?> propertySource = loadYaml(path);
//        environment.getPropertySources().addLast(propertySource);
    }

//    private PropertySource<?> loadYaml(Resource path) {
//        if (!path.exists()) {
//            throw new IllegalArgumentException("Resource " + path + " does not exist");
//        }
//        try {
//            return this.loader.load("custom-resource", path, null);
//        }
//        catch (IOException ex) {
//            throw new IllegalStateException(
//                    "Failed to load yaml configuration from " + path, ex);
//        }
//    }
}
