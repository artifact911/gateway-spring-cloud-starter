package com.artifact.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityConfig {

    private List<String> publicPaths = new ArrayList<>();
    private List<RolePath> rolePaths = new ArrayList<>();

    @Getter
    @Setter
    public static class RolePath {
        private String path;
        private Set<String> roles = new HashSet<>();
    }
}