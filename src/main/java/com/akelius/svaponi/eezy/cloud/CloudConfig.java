package com.akelius.svaponi.eezy.cloud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.environment.EnvironmentManager;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.endpoint.event.RefreshEventListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.stream.Stream;

/**
 * @see org.springframework.cloud.autoconfigure.RefreshAutoConfiguration
 */
@Configuration
public class CloudConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(
            final ConfigurableEnvironment configurableEnvironment,
            final ConfigurableApplicationContext context
    ) {
        final PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        final Resource[] resources = Stream.of(
                "application.properties",
                "bootstrap.properties"
        )
                .map(ClassPathResource::new)
                .peek(r -> Assert.isTrue(r.exists(), "resource not found: " + r.getPath()))
                .toArray(Resource[]::new);
        pspc.setLocations(resources);
        pspc.setEnvironment(configurableEnvironment);
        return pspc;
    }

    @Bean
    public static EnvironmentManager environmentManager(
            final ConfigurableEnvironment configurableEnvironment,
            final ConfigurableApplicationContext context
    ) {
        // final ConfigurableEnvironment configurableEnvironment = context.getEnvironment();
        return new EnvironmentManager(configurableEnvironment);
    }

    @Bean
    @ConditionalOnMissingBean(RefreshScope.class)
    public static RefreshScope refreshScope() {
        return new RefreshScope();
    }

    @Bean
    public RefreshEventListener refreshEventListener(final ContextRefresher contextRefresher) {
        return new RefreshEventListener(contextRefresher);
    }

    @Bean
    @ConditionalOnMissingBean
    public ContextRefresher contextRefresher(final ConfigurableApplicationContext context,
                                             final RefreshScope scope) {
        return new ContextRefresher(context, scope);
    }
}
