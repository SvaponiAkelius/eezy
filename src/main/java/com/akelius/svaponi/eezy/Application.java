package com.akelius.svaponi.eezy;

import com.akelius.svaponi.eezy.web.WebController;
import com.akelius.svaponi.eezy.web.WebControllerMapping;
import com.akelius.svaponi.eezy.web.WebServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class Application {

    private static Application application;

    public static Application getInstance() {
        if (application == null) {
            throw new IllegalStateException("application not available yet");
        }
        synchronized (application) {
            return application;
        }
    }

    public static void main(final String[] args) throws Exception {
        application = new Application();
    }

    // instance

    private final WebServer webServer;
    private final AnnotationConfigApplicationContext ctx;

    public ConfigurableEnvironment getConfigurableEnvironment() {
        return ctx.getEnvironment();
    }

    public ConfigurableApplicationContext getConfigurableApplicationContext() {
        return ctx;
    }

    private Application() throws Exception {
        ctx = new AnnotationConfigApplicationContext();
        log.info("scanning {}", Application.class.getPackage().getName());
        ctx.scan(Application.class.getPackage().getName());
        ctx.refresh();
        final List<String> beanNames = Arrays.stream(ctx.getBeanDefinitionNames()).sorted().collect(Collectors.toList());
        log.info("found {} components: {}", beanNames.size(), beanNames);
        webServer = new WebServer(8080);
        final Map<String, WebController> controllers = ctx.getBeansOfType(WebController.class);
        controllers.values().forEach(controller -> {
            final WebControllerMapping mapping = controller.getClass().getAnnotation(WebControllerMapping.class);
            Assert.notNull(mapping, "missing @WebControllerMapping in " + controller.getClass());
            Assert.hasText(mapping.value(), "invalid @WebControllerMapping value in " + controller.getClass());
            final int order = Optional.ofNullable(controller.getClass().getAnnotation(Order.class)).map(Order::value)
                    .orElse(0);
            webServer.addMapping(mapping.value(), order, controller);
        });
        webServer.start();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            log.error("uncaught exception on thread '{}': {}", t.getName(), e.getMessage());
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.webServer.stop()));
    }
}
